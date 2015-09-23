package corpus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser {
	
	private int ptr;
	private String in;
	
	///////////////////////////////
	
	public Expr 
	parse(String s) throws Exception {
		
	    // in = s;
		in = Utils.stripComments( s );
		// Swan: 21st Sept 2015
		// This parser goes into an infinite loop unless comments are stripped...
		
	    ptr = 0;
	    Expr.ExprList lst = new Expr.ExprList();
	    while( ptr < in.length() ) {
	    	if( cur() == '(' ) {
	            lst.add( parseList() );
	        } else {
	            lst.add( parseAtom() );
	        }
	        if( ptr != in.length() ) {
	            eatWhitespace();
	        }
	    }
	    
	    return lst.size() == 1 ? lst.get( 0 ) : lst;
	}	

	///////////////////////////////

	private void match(char c) throws Exception {
		if (in.charAt(ptr) == c) {
			ptr++;
		} else {
			throw new Exception("Invalid character at " + ptr + ": " + c);
		}
	}

	private boolean validAtomChar(char c) {
		return c != ' ' && c != '\n' && c != '\t' && c != '(' && c != ')'
				&& /* c != '#' && */ c != '[' && c != ']';
		// Swan: 21st Sept 2015
	}

	private boolean endOfString() {
		return ptr == in.length();
	}

	private void eatWhitespace() throws Exception {
		char c = cur();
		while (c == ' ' || c == '\n' || c == '\t') {
			match(c);
			c = cur();
		}
	}

	private char cur() {
		return in.charAt(ptr);
	}

	private boolean isNumber(String s) {
		return s.matches("(-|\\+)?[0-9]+(\\.[0-9]+)?");
	}
	
	// private List<Object> parseList() throws Exception {
	private Expr.ExprList parseList() throws Exception {	
	    // create data structure
	    // LinkedList<Object> lst = new LinkedList<Object>();
		Expr.ExprList lst = new Expr.ExprList();
	     
	    match('(');
	    eatWhitespace();
	         
	    while (cur() != ')') {
	        // loop until finding an end paren
	             
	        if (cur() == '(') {
	            // nested list
	            lst.add(parseList());
	        } else {
	            // atom
	            lst.add(parseAtom());
	        }
	        eatWhitespace();
	    }
	    match(')');
	         
	    return lst;
	}
	
	private Expr parseAtom() throws Exception {
	    String buf = "";
	    while (!endOfString() && validAtomChar(cur())) {
	        // build buffer
	        buf += cur();
	        match(cur());
	    }
	    
	    /****
	    // determine what's in the buffer
	    if (isNumber(buf)) {
	        return new Expr.Atom( new BigDecimal(buf) );
	    } else {
	        return new Expr.Atom( Symbol.getSymbol(buf) );
	    }
	    ****/
	    return new Expr.Atom( buf.trim() );
	}
}

// End ///////////////////////////////////////////////////////////////
