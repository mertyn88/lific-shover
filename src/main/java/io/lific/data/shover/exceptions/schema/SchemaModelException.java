package io.lific.data.shover.exceptions.schema;


import io.lific.data.shover.exceptions.SchemaException;

public class SchemaModelException extends SchemaException {


	public SchemaModelException(String topic, Object message, Throwable cause) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + message + "\n}\n", cause);
	}

	public SchemaModelException(Object message) {
		super("{\n\tmessage : " + message + "\n}\n");
	}
}
