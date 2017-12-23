package lu.kremi151.mathparser.exception;

public class MathDerivateException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3783353723316797040L;

	public MathDerivateException() {}
	
	public MathDerivateException(String message) {
		super(message);
	}
	
	public MathDerivateException(String message, Exception ex) {
		super(message, ex);
	}
	
	public MathDerivateException(Exception ex) {
		super(ex);
	}
}
