package lu.kremi151.mathparser.interfaces;

import lu.kremi151.mathparser.objects.MathObject;

public interface MathObjectComposer {

	MathObject compose(MathObject... terms);
	
}
