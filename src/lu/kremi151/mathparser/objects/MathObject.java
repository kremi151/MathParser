package lu.kremi151.mathparser.objects;

import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;

public abstract class MathObject implements MathFunction{

	public abstract String serialize();
	
	public abstract boolean equals(MathObject term);
	
	@Deprecated
	protected int unsafeHashCode() {
		return super.hashCode();
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public final boolean equals(Object object) {
		if(object == this) {
			return true;
		}else if(object != null && object instanceof MathObject) {
			return equals((MathObject)object);
		}else {
			return false;
		}
	}
	
	protected String humanReadableName() {
		return this.getClass().getName();
	}
	
	/**
	 * Gets the constant value of this object in case it is constant
	 * @return
	 * @throws MathCalculateException Thrown if this object is not constant
	 */
	public double constantValue() throws MathCalculateException{
		if(isConstant()) {
			return calculate(0.0);
		}else {
			throw new MathCalculateException("This object has no constant value");
		}
	}
	
	@Override
	public String toString() {
		return serialize();
	}
	
	public boolean providesSignum() {
		return false;
	}
	
	public boolean needsParentheses(MathContext context) {
		return true;
	}
	
	/**
	 * Checks if this math object hs a constant value. If it is composed of other math objects, each object should be iterated in order to verify a constant value. A constant object should be able to return its value given 0 as an argument.
	 * @return
	 */
	public abstract boolean isConstant();
	
	public MathObject derivative() throws MathDerivateException{
		throw new MathDerivateException("Not derivable for type " + humanReadableName() + " [Expression: " + serialize() + "]");
	}
	
	public Optional<double[]> findRoots() {
		return Optional.empty();
	}
	
	public MathObject multiply(MathObject term) {
		return new Product(this, term);
	}
	
	public MathObject add(MathObject term) {
		return new Sum(this, term);
	}
	
	public MathObject divide(MathObject term) {
		return new Quotient(this, term);
	}
	
	public MathObject subtract(MathObject term) {
		return new Sum(this, new Negation(term));
	}
	
}
