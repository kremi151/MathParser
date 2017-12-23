package lu.kremi151.mathparser.objects;

import lu.kremi151.mathparser.exception.MathCalculateException;

public interface MathFunction {

	double calculate(double input) throws MathCalculateException;
}
