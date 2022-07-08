package io.lific.data.shover.exceptions.schema;


import io.lific.data.shover.exceptions.SchemaException;

public class SchemaNotExistException extends SchemaException {


	public SchemaNotExistException(String topic, Object message, Throwable cause) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + message + "\n}\n", cause);
	}

	public SchemaNotExistException(String topic, Object message) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + message + "\n}\n");
	}
}
