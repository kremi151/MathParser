package lu.kremi151.mathparser.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import lu.kremi151.mathparser.enums.MathContext;
import lu.kremi151.mathparser.exception.MathDerivateException;

public class Product extends MathObject{
	
	private final MathObject objects[];
	
	public Product(MathObject... objects) {
		this.objects = objects;
	}

	@Override
	public double calculate(double input) {
		if(objects.length > 0) {
			double res = 1.0;
			for(MathFunction f : objects) {
				res *= f.calculate(input);
			}
			return res;
		}else {
			return 0.0;
		}
	}

	@Override
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0 ; i < objects.length ; i++) {
			MathObject obj = objects[i];
			if(i > 0) {
				sb.append("*");
			}
			if(obj.needsParentheses(MathContext.PRODUCT))sb.append("(");
			sb.append(obj.serialize());
			if(obj.needsParentheses(MathContext.PRODUCT))sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public boolean isConstant() {
		for(MathObject obj : objects) {
			if(!obj.isConstant())return false;
		}
		return true;
	}
	
	@Override
	public MathObject derivative() throws MathDerivateException{
		if(isConstant()) {
			return new Constant(0.0);
		}else {
			LinkedList<MathObject> derivatives = new LinkedList<>();
			for(int i = 0 ; i < objects.length ; i++) {
				MathObject dbase = objects[i].derivative();
				for(int j = 0 ; j < objects.length ; j++) {
					if(i != j) {
						derivatives.add(createEfficientProduct(dbase, objects[j]));
					}
				}
			}
			MathObject array[] = derivatives.toArray(new MathObject[derivatives.size()]);
			return Sum.createEfficientSum(array);
		}
	}
	
	@Override
	public Optional<double[]> findRoots() {
		HashSet<Double> croots = new HashSet<>();
		for(MathObject obj : objects) {
			obj.findRoots().ifPresent(roots -> {
				for(double v : roots)croots.add(v);
			});
		}
		if(croots.isEmpty()) {
			return Optional.empty();
		}else {
			double nroots[] = new double[croots.size()];
			int i = 0;
			for(Double v : croots) nroots[i++] = v.doubleValue();
			return Optional.of(nroots);
		}
	}
	
	private static void concatenateProductsRecursively(LinkedList<MathObject> terms, Product root) {
		for(MathObject obj : root.objects) {
			if(obj.getClass() == Product.class) {
				concatenateProductsRecursively(terms, (Product)obj);
			}else {
				terms.add(obj);
			}
		}
	}
	
	private static void combineConstants(LinkedList<MathObject> terms) {
		double constant = 1.0;
		Iterator<MathObject> it = terms.iterator();
		while(it.hasNext()) {
			MathObject obj = it.next();
			if(obj.isConstant()) {
				it.remove();
				constant *= obj.constantValue();
			}
		}
		if(constant != 1.0) {
			terms.addFirst(new Constant(constant));
		}
	}
	
	public static MathObject createEfficientProduct(MathObject... terms) {
		LinkedList<MathObject> nterms = new LinkedList<>();
		double prod = 1.0;
		for(MathObject obj : terms) {
			if(obj.getClass() == Product.class) {
				concatenateProductsRecursively(nterms, (Product)obj);
			}else if(obj.isConstant()) {
				double constant = obj.constantValue();
				if(constant == 0.0) {
					return Constant.ZERO;
				}else {
					prod *= constant;
				}
			}else {
				nterms.add(obj);
			}
		}
		if(prod != 1.0) {
			nterms.addFirst(new Constant(prod));
		}
		if(nterms.size() == 0) {
			return Constant.ZERO;
		}else if(nterms.size() == 1) {
			return nterms.getFirst();
		}else {
			combineConstants(nterms);
			return new Product(nterms.toArray(new MathObject[nterms.size()]));
		}
	}
	
	@Override
	public MathObject multiply(MathObject term) {
		LinkedList<MathObject> terms = new LinkedList<>(Arrays.asList(objects));
		terms.add(term);
		return new Product(terms.toArray(new MathObject[terms.size()]));
	}

	@Override
	public boolean equals(MathObject term) {
		if(term.getClass() == Product.class) {
			HashSet<MathObject> termset = new HashSet<>(Arrays.asList(((Product)term).objects));
			for(MathObject myterm : objects) {
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
		for(MathObject term : objects) {
			hash *= term.hashCode();
			hash *= 31;
		}
		return hash;
	}

}
