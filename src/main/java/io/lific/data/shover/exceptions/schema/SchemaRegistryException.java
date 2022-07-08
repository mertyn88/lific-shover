package io.lific.data.shover.exceptions.schema;


import io.lific.data.shover.exceptions.SchemaException;

public class SchemaRegistryException extends SchemaException {

	public SchemaRegistryException() {
	}

	public SchemaRegistryException(String message) {
		super(message);
	}

	public SchemaRegistryException(Throwable cause) {
		super(cause);
	}

	public SchemaRegistryException(String message, Throwable cause) {
		super(message, cause);
	}

}
