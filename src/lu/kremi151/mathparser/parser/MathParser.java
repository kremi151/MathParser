package lu.kremi151.mathparser.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lu.kremi151.mathparser.exception.MathParseException;
import lu.kremi151.mathparser.functions.Logarithm;
import lu.kremi151.mathparser.interfaces.MathFunctionComposer;
import lu.kremi151.mathparser.interfaces.MathObjectComposer;
import lu.kremi151.mathparser.objects.Constant;
import lu.kremi151.mathparser.objects.MathObject;
import lu.kremi151.mathparser.objects.Negation;
import lu.kremi151.mathparser.objects.Parameter;
import lu.kremi151.mathparser.objects.Power;
import lu.kremi151.mathparser.objects.Product;
import lu.kremi151.mathparser.objects.Quotient;
import lu.kremi151.mathparser.objects.Sum;
import lu.kremi151.mathparser.objects.Unknown;

public class MathParser {
	
	//private static final Pattern MATH_PATTERN = Pattern.compile("[0-9]+|\\+|\\-|\\/|\\*|\\^|\\(|\\)|\\{|\\}|[a-zA-Z]+");
	private static final Pattern MATH_PATTERN = Pattern.compile("[0-9]+|[a-zA-Z]+|.");
	
	private MathObjectComposer sumComposer = Sum::new;
	private MathObjectComposer productComposer = Product::new;
	private BiFunction<MathObject, MathObject, MathObject> quotientComposer = Quotient::new;
	private BiFunction<MathObject, MathObject, MathObject> powerComposer = Power::new;
	
	private final HashMap<String, MathFunctionComposer> functions = new HashMap<>();

	public MathParser() {
		functions.put("log", (param, inner) -> new Logarithm(param.or(Constant.TEN), inner));
		functions.put("ln", (param, inner) -> new Logarithm(Constant.E, inner));
	}
	
	public MathParser setEfficient() {
		sumComposer = Sum::createEfficientSum;
		productComposer = Product::createEfficientProduct;
		quotientComposer = Quotient::createEfficientQuotient;
		powerComposer = Power::createEfficientPower;
		return this;
	}
	
	private boolean isNumeric(String input) {
		boolean pointing = false;
		for(int i = 0 ; i < input.length() ; i++) {
			int ascii = input.charAt(i);
			if(input.charAt(i) == '.' || input.charAt(i) == ',') {
				if(pointing) {
					return false;
				}else {
					pointing = true;
				}
			}else if(ascii < 48 || ascii > 57) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isSign(String token) {
		char c;
		return token.length() == 1 && ((c = token.charAt(0)) == '+' || c == '-' || c == '*' || c == '/');
	}
	
	public MathObject deserialize(String input) throws MathParseException{
		Matcher matcher = MATH_PATTERN.matcher(input);
		ArrayList<String> tokens = new ArrayList<>();
		while(matcher.find()) {
			tokens.add(matcher.group());
		}
		return parseObject(new StringIterator(tokens.toArray(new String[tokens.size()])));
	}
	
	private MathObject parseObject(StringIterator it) throws MathParseException{
		return parseObject(it, ParseMode.SUM);
	}
	
	private MathObject parseObject(StringIterator it, final ParseMode mode) throws MathParseException{
		return parseObject(it, mode, new LinkedList<>());
	}
	
	private MathObject parseObject(StringIterator it, final ParseMode mode, final LinkedList<MathObject> parsed) throws MathParseException{
		try {
			QuotientState qmode = QuotientState.NONE;
			boolean negateNext = false;
			boolean awaitPower = false;
			String previousToken = null;
			while(it.hasNext()) {
				boolean skipNegation = false;
				boolean skipPower = false;
				String part = it.next();
				if(part.equals("(")) {
					parsed.add(parseObject(it));
					if(qmode == QuotientState.DENOMINATOR) {
						MathObject den = parsed.removeLast();
						MathObject num = parsed.removeLast();
						parsed.add(quotientComposer.apply(num, den));
						qmode = QuotientState.NONE;
					}
				}else if(part.equals(")") || part.equals("}")) {
					return pack(parsed, mode);
				}else if(part.equals("/")) {
					qmode = QuotientState.DENOMINATOR;
				}else if(part.equals("+")) {
					switch(mode) {
					case PRODUCT:
						return pack(parsed, ParseMode.PRODUCT);
					case SUM: break;
					default:
						throw new IllegalStateException("No rule defined for parse mode " + mode);
					}
				}else if(part.equals("-")) {
					switch(mode) {
					case PRODUCT:
						it.back();
						return pack(parsed, ParseMode.PRODUCT);
					case SUM: 
						negateNext = true;
						skipNegation = true;
						break;
					default:
						throw new IllegalStateException("No rule defined for parse mode " + mode);
					}
				}else if(part.equals("*")) {
					switch(mode) {
					case PRODUCT: break;
					case SUM:
						LinkedList<MathObject> list = new LinkedList<>();
						list.add(parsed.removeLast());
						parsed.add(parseObject(it, ParseMode.PRODUCT, list));
						break;
					default:
						throw new IllegalStateException("No rule defined for parse mode " + mode);
					}
				}else if(part.equals("^")) {
					awaitPower = true;
					skipPower = true;
				}else if(isNumeric(part)) {
					parsed.add(new Constant(Double.parseDouble(part)));
					if(qmode == QuotientState.DENOMINATOR) {
						MathObject den = parsed.removeLast();
						MathObject num = parsed.removeLast();
						parsed.add(quotientComposer.apply(num, den));
						qmode = QuotientState.NONE;
					}
				}else {
					if(part.equals("x")) {
						parsed.add(Unknown.X);
					}else if(functions.containsKey(part) && it.hasNext() && (it.seeNext().equals("(") || it.seeNext().equals("{"))) {
						Parameter param;
						if(it.seeNext().equals("{")) {
							it.next();
							param = new Parameter(parseObject(it));
						}else {
							it.next();
							param = Parameter.EMPTY;
						}
						parsed.add(functions.get(part).compose(param, parseObject(it)));//TODO
					}else {
						throw new MathParseException("Unknown token: " + part);
					}
					if(previousToken != null && !isSign(previousToken)) {
						MathObject last = parsed.removeLast();
						parsed.add(productComposer.compose(parsed.removeLast(), last));
					}
					if(qmode == QuotientState.DENOMINATOR) {
						MathObject den = parsed.removeLast();
						MathObject num = parsed.removeLast();
						parsed.add(quotientComposer.apply(num, den));
						qmode = QuotientState.NONE;
					}
				}
				if(!skipPower && awaitPower) {
					MathObject power = parsed.removeLast();
					parsed.add(powerComposer.apply(parsed.removeLast(), power));
				}
				if(!skipNegation && negateNext) {
					parsed.add(new Negation(parsed.removeLast()));
					negateNext = false;
				}
				previousToken = part;
			}
			return pack(parsed, mode);
		}catch(RuntimeException e) {
			throw new MathParseException("Parsing error for token \"" + it.current() + "\"", e);
		}
	}
	
	private MathObject pack(Collection<MathObject> parsed, ParseMode mode) throws MathParseException {
		if(parsed.size() > 1) {
			if(mode == ParseMode.SUM) {
				return sumComposer.compose(parsed.toArray(new MathObject[parsed.size()]));
			}else if(mode == ParseMode.PRODUCT){
				return productComposer.compose(parsed.toArray(new MathObject[parsed.size()]));
			}else {
				throw new MathParseException("Invalid parser state: " + mode);
			}
		}else if(parsed.size() == 1) {
			return parsed.iterator().next();
		}else {
			return Constant.ZERO;
		}
	}
	
	private static enum ParseMode{
		SUM,
		PRODUCT
	}
	
	private static enum QuotientState{
		NONE,
		DENOMINATOR
	}
	
	private static class StringIterator implements Iterator<String>{
		
		private final String array[];
		private int index = 0;
		
		private StringIterator(String array[]) {
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return index < array.length;
		}

		@Override
		public String next() {
			return array[index++];
		}
		
		void back() {
			index--;
		}
		
		String seeNext() {
			return array[index];
		}
		
		String current() {
			return array[index-1];
		}
		
	}
}
