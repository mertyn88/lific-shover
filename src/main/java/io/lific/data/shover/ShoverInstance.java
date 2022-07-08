package io.lific.data.shover;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.lific.data.shover.callback.FileCallback;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.schema.SchemaNotExistException;
import io.lific.data.shover.exceptions.schema.SchemaSendException;
import io.lific.data.shover.schema.SchemaCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class ShoverInstance {

	private KafkaProducer<String, GenericRecord> producer = null;
	private SchemaCache schemaCache = null;
	private String storageDirectory = null;

	/**
	 * Shover 컴포넌트를 생성합니다. 스키마캐시, 프로듀서, 전송현황, 실패 전송개수를 설정 합니다.
	 * @param config Config
	 */
	public ShoverInstance(Config config) {
		try {
			this.schemaCache = new SchemaCache(config.getProperties().getProperty(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG));
			this.producer = new KafkaProducer<>(config.getProperties());
			this.storageDirectory = config.getProperties().getProperty("fail.storage.directory");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Check schema and Send kafka record data and Flush */
	/**
	 * 카프카에 전송하기 전, 스키마레지스트리에 있는 해당 스키마값을 가져옵니다.
	 * 전체적인 전송 프로세스가 이루어 지며 레지스트리 캐시맵에 없는 경우 예외가 발생합니다.
	 * @param topic String
	 * @param data <T extends ObjectNode>
	 * @throws SchemaSendException
	 * @throws SchemaNotExistException
	 */
	public <T extends ObjectNode> void send(String topic, T data) throws SchemaNotExistException {
		Pair<Integer, Schema> schema = this.schemaCache.getSchema(topic);
		if(schema == null) {
			throw new SchemaNotExistException("Schema data is null, check schema-registry", data);
		}
		send(new ProducerRecord<>(topic, parseNodeToRecord(data, schema.getRight())));
	}

	/**
	 * 카프카에 전송합니다. 내부에서 전송하기전, 스키마레지스트리와의 필드 검증을 수행합니다.
	 * 반환값이 없는 비동기 방식으로 전송이 되며 예외를 알 수 없으므로 try - catch는 없습니다.
	 * @param record ProducerRecord<String, GenericRecord>
	 * @throws SchemaSendException
	 */
	private void send(ProducerRecord<String, GenericRecord> record) {
		Optional.ofNullable(this.storageDirectory).ifPresentOrElse(
				directory -> CompletableFuture.runAsync(() -> this.producer.send(record, new FileCallback(record, directory)))
				, ()      -> CompletableFuture.runAsync(() -> this.producer.send(record))
		);
		this.producer.flush();
	}

	/**
	 * 내부 테스트 용도 입니다. 싱글쓰레드
	 * @param topic String
	 * @param data data
	 * @throws SchemaSendException
	 * @throws SchemaNotExistException
	 */
	public <T extends ObjectNode> void sendTest(String topic, T data) throws SchemaSendException, SchemaNotExistException {
		Pair<Integer, Schema> schema = this.schemaCache.getSchema(topic);
		if(schema == null) {
			throw new SchemaNotExistException("Schema data is null, check schema-registry", data);
		}
		sendTest(new ProducerRecord<>(topic, parseNodeToRecord(data, schema.getRight())));
	}

	/**
	 * 내부 테스트 용도 입니다. 싱글쓰레드
	 * @param record ProducerRecord<String, GenericRecord>
	 * @throws SchemaSendException
	 */
	private void sendTest(ProducerRecord<String, GenericRecord> record) throws SchemaSendException {
		try {
			Optional.ofNullable(this.storageDirectory).ifPresentOrElse(
					directory -> this.producer.send(record, new FileCallback(record, directory))
					, ()      -> this.producer.send(record)
			);
		} catch (Exception e) {
			throw new SchemaSendException(record.topic(), record, e);
		}
		this.producer.flush();
	}

	/**
	 * 전달받은 데이터를 스키마레지스트리에 맞도록 컨버팅을 합니다. ObjectNode의 형태를 스키마 형태로 변환합니다.
	 * @param message <T extends ObjectNode> Jackson library
	 * @param schema Schema
	 * @return GenericRecord
	 */
	private <T extends ObjectNode> GenericRecord parseNodeToRecord(T message, Schema schema)  {
		GenericRecord record = new GenericData.Record(schema);
		for (Schema.Field schemaField : schema.getFields()) {
			typeValuePut(record, schemaField, message.get(schemaField.name()));
		}
		return record;
	}

	/**
	 * 스키마의 타입을 체크하여 객체의 타입에 맞는 형태로 GenericRecord에 값을 할당합니다.
	 * 스키마의 타입이 복합(UNION)일 경우, 리스트의 첫번째 타입을 주 타입으로 설정합니다.
	 * 스키마의 default value가 존재하는 경우, 해당 값으로 설정합니다.
	 * @param record GenericRecord
	 * @param schemaField Schema.Field
	 * @param node JsonNode
	 */
	private void typeValuePut(GenericRecord record, Schema.Field schemaField, JsonNode node) {
		if(node != null && !node.asText().equals("null")) {
			Schema.Type type;
			if(Schema.Type.UNION == schemaField.schema().getType()) {
				int typeNum = node.asText() == null ? 0 : 1;
				type = schemaField.schema().getTypes().get(typeNum).getType();
			}else {
				type = schemaField.schema().getType();
			}
			switch (type) {
				case NULL:
					record.put(schemaField.name(), null); break;
				case LONG:
					record.put(schemaField.name(), node.asLong()); break;
				case INT:
					record.put(schemaField.name(), node.asInt()); break;
				case DOUBLE:
					record.put(schemaField.name(), node.asDouble()); break;
				case BOOLEAN:
					record.put(schemaField.name(), node.asBoolean()); break;
				default:
					record.put(schemaField.name(), node.asText());
			}
		}else {
			if(schemaField.defaultVal() != null) {
				record.put(schemaField.name(), schemaField.defaultVal());
			}
		}
	}
}
