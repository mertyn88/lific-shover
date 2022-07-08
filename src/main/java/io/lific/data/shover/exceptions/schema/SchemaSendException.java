package io.lific.data.shover.exceptions.schema;


import io.lific.data.shover.exceptions.SchemaException;

import java.util.Arrays;

public class SchemaSendException extends SchemaException {

	public SchemaSendException(Object message) {
		super("{\n\tmessage : " + message + "\n}\n");
	}

	public SchemaSendException(String topic, Object message) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + message + "\n}\n");
	}

	public SchemaSendException(String topic, Object message, Throwable cause) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + message + "\n}\n", cause);
	}

	public SchemaSendException(String topic, Object[] message) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + Arrays.toString(message) + "\n}\n");
	}

	public SchemaSendException(String topic, Object[] message, Throwable cause) {
		super("{\n\ttopic : " + topic + "\n\t, message : " + Arrays.toString(message) + "\n}\n", cause);
	}
}
