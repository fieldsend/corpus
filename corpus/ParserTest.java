package corpus;



import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import jeep.lang.Diag;

public class ParserTest
{
    @Test
	public void test() throws Exception {
		
		Parser parser = new Parser();
		
		String ex1Str = "(+ 3 2)";
		Expr.ExprList ex1 = Expr.list( Expr.atom("+"), Expr.atom("3"), Expr.atom("2") );
		Expr a1 = parser.parse( ex1Str );
		assertEquals( ex1, a1 );
		assertEquals( Utils.stripWhitespace( ex1Str ), Utils.stripWhitespace( a1.toString() ) );

		// Diag.println( ex1Str );
		// Diag.println( a1 );		
		
		///////////////////////////
		
		String ex2Str = "(+ (+ 3 2) 1)";
		Expr.ExprList ex2 = Expr.list( Expr.atom("+"), ex1, Expr.atom("1") );
		Expr a2 = parser.parse( ex2Str );		
		assertEquals( ex2, a2 );		
		assertEquals( Utils.stripWhitespace( ex2Str ), Utils.stripWhitespace( a2.toString() ) );		

		// Diag.println( ex2Str );
		// Diag.println( a2 );		
		
		///////////////////////////
		
		String ex3Str = "(define prog (lambda (n) (* n n) ))";		
		Expr.ExprList ex3 = Expr.list( Expr.atom("define"), Expr.atom("prog"), 
			Expr.list( Expr.atom( "lambda" ), Expr.atom( "n" ), 
				Expr.list( Expr.atom( "*" ), Expr.atom( "n" ), Expr.atom( "n" ) ) ) );

		Expr a3 = parser.parse( ex3Str );
		assertEquals( ex3, a3 );
		
		assertEquals( Utils.stripWhitespace( ex3Str ), Utils.stripWhitespace( a3.toString() ) );
		
		// Diag.println( ex3Str );
		// Diag.println( a3 );		
	}
}
