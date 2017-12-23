package lu.kremi151.mathparser.objects;

import java.util.Optional;

import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Quotient extends MathObject{
	
	private final MathObject numerator, denominator;
	
	public Quotient(MathObject numerator, MathObject denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public double calculate(double input) {
		try {
			return numerator.calculate(input) / denominator.calculate(input);
		}catch(ArithmeticException e) {
			throw new MathCalculateException(e);
		}
	}

	@Override
	public String serialize() {
		return "(" + numerator.serialize() + ")/(" + denominator.serialize() + ")";
	}

	@Override
	public boolean isConstant() {
		return numerator.isConstant() && denominator.isConstant();
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		if(isConstant()) {
			return new Constant(0.0);
		}else {
			return createEfficientQuotient(Sum.createEfficientSum(Product.createEfficientProduct(numerator.derivative(), denominator), new Negation(Product.createEfficientProduct(numerator, denominator.derivative()))), Product.createEfficientProduct(denominator, denominator));
		}
	}
	
	@Override
	public MathObject multiply(MathObject term) {
		return new Quotient(new Product(numerator, term), denominator);
	}
	
	@Override
	public MathObject divide(MathObject term) {
		return new Quotient(numerator, new Product(denominator, term));
	}
	
	@Override
	public Optional<double[]> findRoots() {
		return numerator.findRoots();
	}
	
	public static MathObject createEfficientQuotient(MathObject numerator, MathObject denominator) {
		if(numerator.isConstant() && denominator.isConstant()) {
			return new Constant(numerator.constantValue() / denominator.constantValue());
		}else {
			return new Quotient(numerator, denominator);
		}
	}

	@Override
	public boolean equals(MathObject term) {
		if(term.getClass() == Quotient.class) {
			Quotient other = (Quotient) term;
			return other.numerator.equals(numerator) && other.denominator.equals(denominator);
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return numerator.hashCode() ^ denominator.hashCode();
	}

}
