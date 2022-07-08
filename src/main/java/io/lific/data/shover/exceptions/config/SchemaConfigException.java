package io.lific.data.shover.exceptions.config;


public class SchemaConfigException extends RuntimeException {

	public SchemaConfigException() {
	}

	public SchemaConfigException(String message) {
		super(message);
	}

	public SchemaConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchemaConfigException(Throwable cause) {
		super(cause);
	}

	public static class WrongPathOrNullValue extends SchemaConfigException {

		public WrongPathOrNullValue(String path) {
			super("ShoverConfigException : must have the path '" + path + "' and value");
		}

	}

	public static class WrongTypeValue extends SchemaConfigException {

		public WrongTypeValue(String path) {
			super("ShoverConfigException : the value of the path '" + path + "' is wrong type");
		}

	}

}
