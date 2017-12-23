package lu.kremi151.mathparser.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Sum extends MathObject{
	
	private final MathObject summands[];
	
	public Sum(MathObject... summands) {
		this.summands = summands;
	}

	@Override
	public double calculate(double input) {
		double res = 0.0;
		for(MathFunction f : summands) {
			res += f.calculate(input);
		}
		return res;
	}

	@Override
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < summands.length ; i++) {
			MathObject summand = summands[i];
			if(i > 0 && (!summand.providesSignum() && (!summand.isConstant() || summand.constantValue() >= 0.0))) {
				sb.append("+");
			}
			if(summand.needsParentheses(MathContext.SUM))sb.append("(");
			sb.append(summand.serialize());
			if(summand.needsParentheses(MathContext.SUM))sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public boolean isConstant() {
		for(MathObject obj : summands) {
			if(!obj.isConstant())return false;
		}
		return true;
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		if(isConstant()) {
			return new Constant(0.0);
		}else {
			MathObject derivatives[] = new MathObject[summands.length];
			for(int i = 0 ; i < summands.length ; i++) {
				derivatives[i] = summands[i].derivative();
			}
			return createEfficientSum(derivatives);
		}
	}
	
	@Override
	public Optional<double[]> findRoots() {
		HashSet<Double> croots = null;
		if(summands.length > 0) {
			for(MathObject obj : summands) {
				if(obj.isConstant() && obj.constantValue() != 0.0) {
					return Optional.empty();
				}else {
					Optional<double[]> roots = obj.findRoots();
					if(roots.isPresent()) {
						if(croots == null) {
							croots = new HashSet<>();
							for(double v : roots.get())croots.add(v);
						}else {
							HashSet<Double> comp = new HashSet<>();
							for(double v : roots.get())comp.add(v);
							Iterator<Double> it = croots.iterator();
							while(it.hasNext()) {
								if(!comp.contains(it.next())) {
									it.remove();
								}
							}
							if(croots.isEmpty()) {
								return Optional.empty();
							}
						}
					}else {
						return Optional.empty();
					}
				}
			}
			if(croots != null && !croots.isEmpty()) {
				double nroots[] = new double[croots.size()];
				int i = 0;
				for(Double v : croots) nroots[i++] = v.doubleValue();
				return Optional.of(nroots);
			}else {
				return Optional.empty();
			}
		}else {
			return Optional.empty();
		}
	}
	
	private static void concatenateSumsRecursively(LinkedList<MathObject> terms, Sum root) {
		for(MathObject obj : root.summands) {
			if(obj.getClass() == Sum.class) {
				concatenateSumsRecursively(terms, (Sum)obj);
			}else {
				terms.add(obj);
			}
		}
	}
	
	private static void combineConstants(LinkedList<MathObject> terms) {
		double constant = 0.0;
		Iterator<MathObject> it = terms.iterator();
		while(it.hasNext()) {
			MathObject obj = it.next();
			if(obj.isConstant()) {
				it.remove();
				constant += obj.constantValue();
			}
		}
		if(constant != 0.0) {
			terms.add(new Constant(constant));
		}
	}
	
	public static MathObject createEfficientSum(MathObject... summands) {
		LinkedList<MathObject> nsummands = new LinkedList<>();
		double constantSum = 0.0;
		for(MathObject obj : summands) {
			if(obj.getClass() == Sum.class) {
				concatenateSumsRecursively(nsummands, (Sum)obj);
			}else if(obj.isConstant()) {
				constantSum += obj.constantValue();
			}else {
				nsummands.add(obj);
			}
		}
		if(constantSum != 0.0) {
			nsummands.add(new Constant(constantSum));
		}
		if(nsummands.size() == 0) {
			return Constant.ZERO;
		}else if(nsummands.size() == 1) {
			return nsummands.getFirst();
		}else {
			combineConstants(nsummands);
			return new Sum(nsummands.toArray(new MathObject[nsummands.size()]));
		}
	}
	
	@Override
	public MathObject add(MathObject term) {
		LinkedList<MathObject> terms = new LinkedList<>(Arrays.asList(summands));
		terms.add(term);
		return new Sum(terms.toArray(new MathObject[terms.size()]));
	}
	
	@Override
	public MathObject subtract(MathObject term) {
		LinkedList<MathObject> terms = new LinkedList<>(Arrays.asList(summands));
		terms.add(new Negation(term));
		return new Sum(terms.toArray(new MathObject[terms.size()]));
	}
	
	@Override
	public boolean equals(MathObject term) {
		if(term.getClass() == Sum.class) {
			HashSet<MathObject> termset = new HashSet<>(Arrays.asList(((Sum)term).summands));
			for(MathObject myterm : summands) {
				if(!termset.remove(myterm)) return false;
			}
			return termset.size() == 0;
		}else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 31;
		for(MathObject term : summands) {
			hash *= term.hashCode();
			hash *= 31;
		}
		return hash;
	}

}
