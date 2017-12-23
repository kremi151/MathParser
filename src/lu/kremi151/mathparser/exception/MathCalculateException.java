package lu.kremi151.mathparser.exception;

public class MathCalculateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5194133980941383380L;
	
	public MathCalculateException() {}
	
	public MathCalculateException(String message) {
		super(message);
	}
	
	public MathCalculateException(String message, Exception ex) {
		super(message, ex);
	}
	
	public MathCalculateException(Exception ex) {
		super(ex);
	}

}
