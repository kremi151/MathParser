package lu.kremi151.mathparser.functions;

import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;
import lu.kremi151.mathparser.objects.Constant;
import lu.kremi151.mathparser.objects.MathObject;
import lu.kremi151.mathparser.objects.Product;
import lu.kremi151.mathparser.objects.Quotient;

public class Logarithm extends MathObject{
	
	private final MathObject base, inner;
	
	public Logarithm(MathObject base, MathObject inner) {
		this.base = base;
		this.inner = inner;
	}
	
	public Logarithm(MathObject inner) {
		this(Constant.E, inner);
	}

	@Override
	public double calculate(double input) throws MathCalculateException {
		return Math.log(inner.calculate(input)) / Math.log(base.calculate(input));
	}

	@Override
	public String serialize() {
		return "log{" + base.serialize() + "}(" + inner.serialize() + ")";
	}

	@Override
	public boolean equals(MathObject term) {
		if(term.getClass() == Logarithm.class) {
			Logarithm other = (Logarithm) term;
			return other.base.equals(base) && other.inner.equals(inner);
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * (base.hashCode() ^ inner.hashCode());
	}

	@Override
	public boolean isConstant() {
		return base.isConstant() && inner.isConstant();
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		return Quotient.createEfficientQuotient(inner.derivative(), Product.createEfficientProduct(inner, createEfficientLogarithm(Math.E, base)));
	}
	
	public static MathObject createEfficientLogarithm(double base, MathObject inner) {
		return createEfficientLogarithm(new Constant(base), inner);
	}
	
	public static MathObject createEfficientLogarithm(MathObject base, MathObject inner) {
		if(inner.isConstant()) {
			double value = inner.constantValue();
			if(value == 1.0) {
				return Constant.ZERO;
			}else {
				inner = new Constant(value);
			}
		}
		return new Logarithm(base, inner);
	}

}
