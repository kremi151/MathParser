package lu.kremi151.mathparser;

import java.util.Scanner;

import lu.kremi151.mathparser.exception.MathDerivateException;
import lu.kremi151.mathparser.exception.MathParseException;
import lu.kremi151.mathparser.objects.MathObject;
import lu.kremi151.mathparser.parser.MathParser;

public class Main {

	public static void main(String[] args) {
		MathParser mp = new MathParser();//.setEfficient();
		try(Scanner s = new Scanner(System.in)) {
			System.out.println("Enter your function");
			String input = s.nextLine();
			MathObject obj = mp.deserialize(input);
			System.out.println("Input: " + input);
			System.out.println("Parsed: " + obj);
			try {
				System.out.println("Derivative: " + obj.derivative());
			} catch (MathDerivateException e) {
				System.out.println("This function is not derivable: " + e.getMessage());
			}
			System.out.print("Roots: [");
			obj.findRoots().ifPresent(roots -> {
				for(double root : roots) System.out.print(root + " ");
			});
			System.out.println("]");
			System.out.println("Is constant? -> " + obj.isConstant());
			if(!obj.isConstant()) {
				System.out.println("x?");
				System.out.println("" + obj.calculate(s.nextDouble()));
			}else {
				System.out.println("Constant value: " + obj.constantValue());
			}
		} catch (MathParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
