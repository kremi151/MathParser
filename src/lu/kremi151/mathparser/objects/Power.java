package lu.kremi151.mathparser.objects;

import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;
import lu.kremi151.mathparser.functions.Logarithm;

public class Power extends MathObject{
	
	private final MathObject base, power;
	
	public Power(MathObject base, MathObject power) {
		this.base = base;
		this.power = power;
	}

	@Override
	public double calculate(double input) throws MathCalculateException {
		return Math.pow(base.calculate(input), power.calculate(input));
	}

	@Override
	public String serialize() {
		return (base.needsParentheses(MathContext.PRODUCT) ? "(" + base.serialize() + ")" : base.serialize())
				+ "^(" + power.serialize() + ")";
	}

	@Override
	public boolean equals(MathObject term) {
		if(term.getClass() == Power.class) {
			Power other = (Power) term;
			return other.base.equals(base) && other.power.equals(power);
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return base.hashCode() ^ power.hashCode();
	}

	@Override
	public boolean isConstant() {
		return base.isConstant() && power.isConstant();
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		if(base.isConstant() && power.isConstant()) {
			return Constant.ZERO;
		}else if(base.isConstant()) {
			return Product.createEfficientProduct(this, Logarithm.createEfficientLogarithm(Math.E, new Constant(base.constantValue())));
		}else if(power.isConstant()) {
			return Product.createEfficientProduct(power, createEfficientPower(base, Sum.createEfficientSum(power, Constant.MINUS_ONE)), base.derivative());
		}else {
			return super.derivative();
		}
	}
	
	@Override
	public Optional<double[]> findRoots() {
		return base.findRoots();
	}
	
	public static MathObject createEfficientPower(MathObject base, MathObject power) {
		if(base.isConstant() && power.isConstant()) {
			return new Constant(Math.pow(base.constantValue(), power.constantValue()));
		}else if(base.isConstant()) {
			return new Power(new Constant(base.constantValue()), power);
		}else if(power.isConstant()) {
			double value = power.constantValue();
			if(value == 0.0) {
				return Constant.ONE;
			}else if(value == 1.0){
				return base;
			}else {
				return new Power(base, new Constant(value));
			}
		}else {
			return new Power(base, power);
		}
	}

}
