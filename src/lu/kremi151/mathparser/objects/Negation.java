package lu.kremi151.mathparser.objects;

import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Negation extends MathObject{
	
	private final MathObject function;
	
	public Negation(double constant) {
		this.function = new Constant(1.0);
	}
	
	public Negation(MathObject function) {
		this.function = function;
	}

	@Override
	public double calculate(double input) {
		return -this.function.calculate(input);
	}

	@Override
	public String serialize() {
		return "-(" + function.serialize() + ")";
	}
	
	@Override
	public boolean providesSignum() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return function.isConstant();
	}

	@Override
	public boolean needsParentheses(MathContext context) {
		return context != MathContext.SUM;
	}

	@Override
	public MathObject derivative() throws MathDerivateException{
		return new Negation(function.derivative());
	}
	
	@Override
	public Optional<double[]> findRoots() {
		return function.findRoots();
	}

	@Override
	public boolean equals(MathObject term) {
		return term instanceof Negation && ((Negation)term).function.equals(function);
	}

	@Override
	public int hashCode() {
		return 31 * function.hashCode();
	}

}
