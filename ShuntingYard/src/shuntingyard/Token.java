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
public class Token
{
	char op;
	float val;
	int precidence;
	enum OpType { UNARY_PREFIX, UNARY_POSTFIX, BINARY_LEFT_ASSOC, BINARY_RIGHT_ASSOC };
	enum TokenType { NUMBER, OPERATOR, BRACKET_LEFT, BRACKET_RIGHT};
	OpType otype;
	TokenType ttype;
	
	Token(char o, OpType t, int prec)
	{
		op=o;
		ttype=TokenType.OPERATOR;
		otype=t;
		precidence=prec;
	}
	
	Token(float f)
	{	
		ttype=TokenType.NUMBER;
		val=f;
	}
	
	Token(TokenType t)
	{	
		if (t==TokenType.BRACKET_LEFT || t==TokenType.BRACKET_RIGHT) ttype=t;
	}
	
	public String toString()
	{
		if (ttype==TokenType.NUMBER)
		{
			if (Math.floor(val)==val) return new Integer((int)val).toString();
			else return String.valueOf(val);
		}
		if (ttype==TokenType.OPERATOR) return String.valueOf(op);
		if (ttype==TokenType.BRACKET_LEFT) return "(";
		if (ttype==TokenType.BRACKET_RIGHT) return ")";
		else return ""; // should never happen
	}
}
