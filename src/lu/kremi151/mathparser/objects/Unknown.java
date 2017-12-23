package lu.kremi151.mathparser.objects;

import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Unknown extends MathObject{
	
	public static final Unknown X = new Unknown();
	
	private Unknown() {}

	@Override
	public double calculate(double input) {
		return input;
	}

	@Override
	public String serialize() {
		return "x";
	}

	@Override
	public boolean isConstant() {
		return false;
	}
	
	@Override
	public boolean needsParentheses(MathContext context) {
		return false;
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		return Constant.ONE;
	}

	@Override
	public Optional<double[]> findRoots() {
		return Optional.of(new double[] {0.0});
	}
	
	@Override
	public MathObject multiply(MathObject term) {
		if(term.getClass() == Unknown.class) {
			return new Power(X, new Constant(2.0));
		}else {
			return super.multiply(term);
		}
	}
	
	@Override
	public MathObject divide(MathObject term) {
		if(term.getClass() == Unknown.class) {
			return Constant.ONE;
		}else {
			return super.divide(term);
		}
	}

	@Override
	public MathObject add(MathObject term) {
		if(term.getClass() == Unknown.class) {
			return new Product(new Constant(2.0), X);
		}
		return super.add(term);
	}

	@Override
	public MathObject subtract(MathObject term) {
		if(term.getClass() == Unknown.class) {
			return Constant.ZERO;
		}
		return super.subtract(term);
	}

	@Override
	public boolean equals(MathObject term) {
		return term == this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int hashCode() {
		return super.unsafeHashCode();
	}

}
