package lu.kremi151.mathparser.interfaces;

import lu.kremi151.mathparser.objects.MathObject;
import lu.kremi151.mathparser.objects.Parameter;

public interface MathFunctionComposer {

	MathObject compose(Parameter parameter, MathObject inner);
	
}
