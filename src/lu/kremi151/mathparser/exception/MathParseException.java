package lu.kremi151.mathparser.exception;

public class MathParseException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7723210021888891648L;
	
	public MathParseException() {}
	
	public MathParseException(String message) {
		super(message);
	}
	
	public MathParseException(String message, Exception ex) {
		super(message, ex);
	}
	
	public MathParseException(Exception ex) {
		super(ex);
	}

}
