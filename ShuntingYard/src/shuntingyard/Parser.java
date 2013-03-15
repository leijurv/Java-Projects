//	Shunting yard algorithm demo.
//  Copyright Chris Johnson (http://www.chris-j.co.uk), 2008
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//	
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.
package shuntingyard;
import java.util.*;

public class Parser
{
	LinkedList<Token> gInfixQueue = new LinkedList<Token>();
	LinkedList<Token> gPostfixQueue = new LinkedList<Token>();

	String display(LinkedList<?> l)
	{
		String s = new String("<html>");
		for (ListIterator<?> it = l.listIterator(0); it.hasNext(); ) 
		{
			Object t = it.next();
			String tts=t.toString();
			if (tts.equals("*")) s+="&times;";
			else if (tts.equals("/")) s+="&divide;";
			else if (tts.equals("-")) s+="&minus;";
			else s+=tts;
			s+="&nbsp;";
		}
		return s+"</html>";
	}
	
	boolean isNumeric(char c)
	{
		if (Character.isDigit(c)|| c=='.') return true;
		return false;
	}
	String Tokenise(String str)
	{
		int i=0;
		char cur;
		String numstr;
		gInfixQueue.clear();
		while (i<str.length())
		{
			cur=str.charAt(i);
			if (Character.isWhitespace(cur));
			else if (isNumeric(cur))
			{
				numstr=new String();
				numstr=numstr+Character.toString(cur);
				i++;
				while (i<str.length() && isNumeric(str.charAt(i)))
				{
					numstr=numstr+Character.toString(str.charAt(i));
					i++;
				}
				i--;
			
				try
				{
					gInfixQueue.addLast(new Token(new Float(numstr).floatValue()));
				}
				catch (NumberFormatException e)
				{
					gInfixQueue.clear();
					return("Invalid number '"+numstr+"'");
				}
			}
			else if (cur=='+') gInfixQueue.addLast(new Token(cur, Token.OpType.BINARY_LEFT_ASSOC, 50));
			else if (cur=='-') 
			{
				// If a previous object (number, or bracketed expression) exists then '-' is binary, otherwise it is unary
				if (gInfixQueue.size()==0 || (gInfixQueue.getLast().ttype!=Token.TokenType.NUMBER && gInfixQueue.getLast().ttype!=Token.TokenType.BRACKET_RIGHT))
					gInfixQueue.addLast(new Token(cur, Token.OpType.UNARY_PREFIX, 100));
				else gInfixQueue.addLast(new Token(cur, Token.OpType.BINARY_LEFT_ASSOC, 50));
			}
			else if (cur=='*' || cur=='/') gInfixQueue.addLast(new Token(cur, Token.OpType.BINARY_LEFT_ASSOC, 60));
			else if (cur=='^') gInfixQueue.addLast(new Token(cur, Token.OpType.BINARY_RIGHT_ASSOC, 110));
			else if (cur=='(') gInfixQueue.addLast(new Token(Token.TokenType.BRACKET_LEFT));
			else if (cur==')') gInfixQueue.addLast(new Token(Token.TokenType.BRACKET_RIGHT));
			else 
			{
				gInfixQueue.clear();
				return("Unexpected character '"+cur+"'");
			}
			i++;
		}
		return display(gInfixQueue);
	}
	
	
	String ConvertToPostfix(StatusTableModel stm)
	{
		if (gInfixQueue.size()==0) return "";
		LinkedList<Token> shuntingStack = new LinkedList<Token>();
		LinkedList<Token> infixQueue = new LinkedList<Token>(gInfixQueue);
		Token t;
		stm.clear();
		gPostfixQueue.clear();
		shuntingStack.clear();
		stm.clear();
		stm.addRow(new String [] {display(infixQueue),"",""});
		while (infixQueue.size()>0)
		{
			t=infixQueue.removeFirst();
			
			if (t.ttype==Token.TokenType.NUMBER) gPostfixQueue.addLast(t);
			else if (t.ttype==Token.TokenType.OPERATOR)
			{
				if (t.otype==Token.OpType.UNARY_POSTFIX) gPostfixQueue.addLast(t);
				else if (t.otype==Token.OpType.UNARY_PREFIX) shuntingStack.addLast(t);
				else if (t.otype==Token.OpType.BINARY_LEFT_ASSOC) 
				{
					while (shuntingStack.size()>0 && shuntingStack.getLast().precidence >= t.precidence) gPostfixQueue.addLast(shuntingStack.removeLast());
					shuntingStack.addLast(t);
				}
				else if (t.otype==Token.OpType.BINARY_RIGHT_ASSOC)
				{
					while (shuntingStack.size()>0 && shuntingStack.getLast().precidence > t.precidence) gPostfixQueue.addLast(shuntingStack.removeLast());
					shuntingStack.addLast(t);
				}
			}
			else if (t.ttype==Token.TokenType.BRACKET_LEFT) shuntingStack.addLast(t);
			else if (t.ttype==Token.TokenType.BRACKET_RIGHT)
			{
				try {while (shuntingStack.getLast().ttype != Token.TokenType.BRACKET_LEFT) gPostfixQueue.addLast(shuntingStack.removeLast());}
				catch (NoSuchElementException e)
				{
					gPostfixQueue.clear();
					return("Mismatched brackets on shunting stack");
				}
				shuntingStack.removeLast();
			}
			
			stm.addRow(new String [] {display(infixQueue),display(shuntingStack),display(gPostfixQueue)});

		}
		while (shuntingStack.size()>0) 
		{
			if (shuntingStack.getLast().ttype!=Token.TokenType.OPERATOR) 
			{
				gPostfixQueue.clear();
				return("Non-operator on shunting stack");
			}
			else gPostfixQueue.addLast(shuntingStack.removeLast());
		}
		stm.addRow(new String [] {"","",display(gPostfixQueue)});
		stm.fireTableDataChanged();
		return display(gPostfixQueue);
	}
	
	String EvaluatePostfix(StatusTableModel stm)
	{
		if (gPostfixQueue.size()==0) return "";
		Token t;
		stm.clear();
		LinkedList<Token> postfixQueue = new LinkedList<Token>(gPostfixQueue);
		LinkedList<Float> rpevalStack = new LinkedList<Float>();
		rpevalStack.clear();
		stm.addRow(new String [] {display(postfixQueue),display(rpevalStack)});
		while (postfixQueue.size()>0)
		{
			try
			{
				t=postfixQueue.removeFirst();
				if (t.ttype==Token.TokenType.NUMBER) rpevalStack.addLast(t.val);
				else if (t.ttype==Token.TokenType.OPERATOR)
				{
					float a1=rpevalStack.removeLast();
					if (t.op=='+') rpevalStack.addLast(a1+rpevalStack.removeLast());
					else if (t.op=='-' && t.otype==Token.OpType.BINARY_LEFT_ASSOC) rpevalStack.addLast(rpevalStack.removeLast()-a1);
					else if (t.op=='-' && t.otype==Token.OpType.UNARY_PREFIX) rpevalStack.addLast(-a1);
					else if (t.op=='*') rpevalStack.addLast(a1*rpevalStack.removeLast());
					else if (t.op=='/') rpevalStack.addLast(rpevalStack.removeLast()/a1);
					else if (t.op=='^') rpevalStack.addLast(new Float(Math.pow(rpevalStack.removeLast(),a1)));
				}
			}
			catch (NoSuchElementException e)
			{
				System.out.println("No more tokens to evaluate");
				break;
			}
			stm.addRow(new String [] {display(postfixQueue),display(rpevalStack)});
		}
		stm.fireTableDataChanged();
		if (rpevalStack.size()!=1)
			return new String("Invalid postfix");
		else return rpevalStack.getLast().toString();
	}
	
}
