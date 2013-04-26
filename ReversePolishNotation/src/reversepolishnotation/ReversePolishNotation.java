/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reversepolishnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * I DIDN'T WRITE THIS!
 * I TOOK IT FROM http://andreinc.net/2010/10/05/converting-infix-to-rpn-shunting-yard-algorithm/
 * 
 * 
 * 
 */
public class ReversePolishNotation {
    
	// Associativity constants for operators
	private static final int LEFT_ASSOC = 0;
	private static final int RIGHT_ASSOC = 1;

	// Supported operators
	private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
	static {
		// Map<"token", []{precendence, associativity}>
		OPERATORS.put("+", new int[] { 0, LEFT_ASSOC });
                OPERATORS.put(",", new int[] { 0, LEFT_ASSOC });//For vectors
		OPERATORS.put("-", new int[] { 0, LEFT_ASSOC });
		OPERATORS.put("*", new int[] { 5, LEFT_ASSOC });
		OPERATORS.put("/", new int[] { 5, LEFT_ASSOC });
		OPERATORS.put(".", new int[] { 5, LEFT_ASSOC });//For vectors
                OPERATORS.put("%", new int[] { 5, LEFT_ASSOC });
                OPERATORS.put("^", new int[] { 10, RIGHT_ASSOC });
                OPERATORS.put("sin", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("cos", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("tan", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("sec", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("log", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("csc", new int[] { 15, RIGHT_ASSOC });
                OPERATORS.put("cot", new int[] { 15, RIGHT_ASSOC });
	}

	/**
	 * Test if a certain is an operator .
	 * @param token The token to be tested .
	 * @return True if token is an operator . Otherwise False .
	 */
	private static boolean isOperator(String token) {
		return OPERATORS.containsKey(token);
	}

	/**
	 * Test the associativity of a certain operator token .
	 * @param token The token to be tested (needs to operator).
	 * @param type LEFT_ASSOC or RIGHT_ASSOC
	 * @return True if the tokenType equals the input parameter type .
	 */
	private static boolean isAssociative(String token, int type) {
		if (!isOperator(token)) {
			throw new IllegalArgumentException("Invalid token: " + token);
		}
		if (OPERATORS.get(token)[1] == type) {
			return true;
		}
		return false;
	}

	/**
	 * Compare precendece of two operators.
	 * @param token1 The first operator .
	 * @param token2 The second operator .
	 * @return A negative number if token1 has a smaller precedence than token2,
	 * 0 if the precendences of the two tokens are equal, a positive number
	 * otherwise.
	 */
	private static final int cmpPrecedence(String token1, String token2) {
		if (!isOperator(token1) || !isOperator(token2)) {
			throw new IllegalArgumentException("Invalied tokens: " + token1
					+ " " + token2);
		}
		return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
	}

	public static String[] infixToRPN(String[] inputTokens) {
		ArrayList<String> out = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		// For all the input tokens [S1] read the next token [S2]
		for (String token : inputTokens) {
			if (isOperator(token)) {
				// If token is an operator (x) [S3]
				while (!stack.empty() && isOperator(stack.peek())) {
					// [S4]
					if ((isAssociative(token, LEFT_ASSOC) && cmpPrecedence(
							token, stack.peek()) <= 0)
							|| (isAssociative(token, RIGHT_ASSOC) && cmpPrecedence(
									token, stack.peek()) < 0)) {
						out.add(stack.pop()); 	// [S5] [S6]
						continue;
					}
					break;
				}
				// Push the new operator on the stack [S7]
				stack.push(token);
			} else if (token.equals("(")) {
				stack.push(token); 	// [S8]
			} else if (token.equals(")")) {
				// [S9]
				while (!stack.empty() && !stack.peek().equals("(")) {
					out.add(stack.pop()); // [S10]
				}
				stack.pop(); // [S11]
			} else {
				out.add(token); // [S12]
			}
		}
		while (!stack.empty()) {
			out.add(stack.pop()); // [S13]
		}
		String[] output = new String[out.size()];
		return out.toArray(output);
	}

	public static void example() {
		String[] input = "( 1 + 2 ) * ( 3 / 4 ) ^ ( 5 + 6 )".split(" ");
		String[] output = infixToRPN(input);
		for (String token : output) {
			System.out.print(token + " ");
		}
	}
}