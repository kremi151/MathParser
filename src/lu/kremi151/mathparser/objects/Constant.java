package lu.kremi151.mathparser.objects;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Constant extends MathObject{

	public final static Constant MINUS_ONE = new Constant(-1.0);
	public final static Constant ZERO = new Constant(0.0);
	public final static Constant ONE = new Constant(1.0);
	public final static Constant TEN = new Constant(10.0);
	public final static Constant E = new NamedConstant("e", Math.E);
	
	private final double constant;
	
	public Constant(double constant) {
		this.constant = constant;
	}

	@Override
	public double calculate(double input) {
		return constant;
	}

	@Override
	public double constantValue() throws MathCalculateException{
		return constant;
	}

	@Override
	public String serialize() {
		if(constant % 1 == 0) {
			return String.valueOf((int)constant);
		}else {
			return String.valueOf(constant);
		}
	}
	
	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public boolean needsParentheses(MathContext context) {
		return false;
	}

	@Override
	public MathObject derivative() throws MathDerivateException{
		return new Constant(0);
	}

	@Override
	public boolean equals(MathObject term) {
		return term instanceof Constant && ((Constant)term).constant == this.constant;
	}

	@Override
	public int hashCode() {
		return ((Double)constant).hashCode();
	}
	
	private static class NamedConstant extends Constant{
		
		private final String name;

		public NamedConstant(String name, double constant) {
			super(constant);
			this.name = name;
		}
		
		@Override
		public String serialize() {
			return name;
		}
		
	}
}
