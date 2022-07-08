package io.lific.data.shover.schema;


import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.lific.data.shover.exceptions.schema.SchemaRegistryException;
import org.apache.avro.Schema;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchemaCache {

	private final Map<String, Pair<Integer, Schema>> subjectCache = new HashMap<>();

	/**
	 * 스키마 레지스트리에 접근하여 저장되어 있는 레지스트리를 캐싱합니다.
	 * 단, 이전버전들은 불필요하며 항상 최신버전의 레지스트리를 저장합니다.
	 * 버전이 변경될 시에, 재기동이 필요합니다. 또한 문제 발생시 해당 버전을 알기 위해 Pair형태로 버전값을 가지고 있습니다.
	 * @param schemaRegistryUrl String
	 * @throws SchemaRegistryException
	 */
	public SchemaCache(String schemaRegistryUrl) throws SchemaRegistryException {
		SchemaRegistryClient client = new CachedSchemaRegistryClient(schemaRegistryUrl, 1000);
		try {
			for(String subject : client.getAllSubjects()) {
				SchemaMetadata schema = client.getLatestSchemaMetadata(subject);
				subjectCache.put(subject, Pair.of(schema.getVersion(), new Schema.Parser().parse(schema.getSchema())));
			}
		}catch (IOException | RestClientException e) {
			throw new SchemaRegistryException(e);
		}
	}

	public Pair<Integer, Schema> getSchema(String topic) {
		return subjectCache.getOrDefault(topic + "-value", null);
	}
}
