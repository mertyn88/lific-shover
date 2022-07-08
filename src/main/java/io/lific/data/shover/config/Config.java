package io.lific.data.shover.config;


import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;


public class Config {

	private final Properties properties;


	public Properties getProperties() {
		return this.properties;
	}

	Config(Builder builder) {
		this.properties = builder.properties;
	}

	public static class Builder {
		private final Properties properties = new Properties();

		public Builder bootstrapServersConfig (List<String> lists) {
			this.properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, lists);
			return this;
		}

		public Builder clientId(String id) {
			this.properties.put(ProducerConfig.CLIENT_ID_CONFIG, id);
			return this;
		}

		public Builder keySerializerClass(String className) {
			this.properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, className);
			return this;
		}

		public Builder keySerializerClass() {
			this.properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			return this;
		}

		public Builder valueSerializerClass(String className) {
			this.properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, className);
			return this;
		}

		public Builder valueSerializerClass() {
			this.properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
			return this;
		}

		public Builder schemaRegistryUrl(String registryUrl) {
			this.properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registryUrl);
			return this;
		}

		public Builder failStorage(String directory) {
			this.properties.put("fail.storage.directory", directory);
			return this;
		}

		public Config build() {
			return new Config(this);
		}
	}
}
