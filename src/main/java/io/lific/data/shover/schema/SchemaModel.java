package io.lific.data.shover.schema;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lific.data.shover.exceptions.schema.SchemaModelException;


public class SchemaModel {

	public static ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	public static SchemaModel.Builder builder() {
		return new SchemaModel.Builder();
	}

	public static class Builder {
		private final ObjectNode node;

		Builder() {
			this.node = JsonNodeFactory.instance.objectNode();
		}

		public Builder put(String field, String value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}
		public Builder put(String field, Integer value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}
		public Builder put(String field, Long value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}
		public Builder put(String field, Float value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}
		public Builder put(String field, Double value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}
		public Builder put(String field, Boolean value) throws SchemaModelException {
			if(this.node == null) throw new SchemaModelException("You have to follow the SchemaModel before the call init() is done");
			this.node.put(field, value);
			return this;
		}

		public Builder setAll(ObjectNode other) {
			if(other != null) {
				this.node.setAll(other);
			}
			return this;
		}

		public ObjectNode build() {
			return this.node;
		}
	}

	public static <T, R> R bind(T data, Class<R> clazz) throws JsonProcessingException {
		return mapper.readValue(mapper.writeValueAsString(data), clazz);
	}
}
