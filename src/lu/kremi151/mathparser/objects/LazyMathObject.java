package lu.kremi151.mathparser.objects;

import lu.kremi151.mathparser.exception.MathCalculateException;
import lu.kremi151.mathparser.exception.MathDerivateException;
import lu.kremi151.mathparser.exception.MathParseException;
import lu.kremi151.mathparser.parser.MathParser;

@Deprecated
public class LazyMathObject extends MathObject{
	
	private MathObject parsed = null;
	private final String statement;
	private final MathParser context;
	
	public LazyMathObject(String statement, MathParser context) {
		this.statement = statement;
		this.context = context;
	}

	public LazyMathObject(String statement) {
		this(statement, new MathParser());
	}
	
	private MathObject getObject() throws MathParseException {
		if(parsed == null) {
			parsed = context.deserialize(statement);
		}
		return parsed;
	}

	@Override
	public double calculate(double input) throws MathCalculateException {
		try {
			return getObject().calculate(input);
		} catch (MathParseException e) {
			throw new MathCalculateException(e);
		}
	}

	@Override
	public String serialize() {
		return statement;
	}

	@Override
	public boolean equals(MathObject term) {
		try {
			return getObject().equals(term);
		} catch (MathParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		try {
			return getObject().hashCode();
		} catch (MathParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isConstant() {
		try {
			return getObject().isConstant();
		} catch (MathParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		try {
			return getObject().derivative();
		} catch (MathParseException e) {
			throw new MathDerivateException(e);
		}
	}

}
