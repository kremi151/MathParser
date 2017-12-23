package lu.kremi151.mathparser.objects;

public class Parameter{
	
	public static final Parameter EMPTY = new Parameter(null);
	
	private final MathObject parameter;
	
	public Parameter(MathObject parameter) {
		this.parameter = parameter;
	}
	
	public MathObject or(MathObject def) {
		if(parameter == null) {
			return def;
		}else {
			return parameter;
		}
	}
	
	public boolean has() {
		return parameter != null;
	}
	
	public MathObject get() {
		if(parameter != null) {
			return parameter;
		}else {
			throw new NullPointerException();
		}
	}

}
