package corpus;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jeep.lang.Diag;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestParser {

	@Test
	public void equalsTest(){

		Expr a1 = Expr.list(Expr.atom("a"));
		Expr a2 = Expr.list(Expr.atom("a"));
		Expr b1 = Expr.list(Expr.atom("b"));
		
		assertEquals(a1,a2);
		assertEquals(a1,a2);
		// assertFalse(a1.equals(b1));
		assertThat(a1, not(equalTo(b1)));
		
		Expr a = Expr.list(Expr.atom("start"), Expr.atom("start"),
			Expr.list(Expr.atom("begin"),Expr.atom("_"), Expr.atom("_")));
	       
		Expr b = Expr.list(Expr.atom("start"), Expr.atom("start"),
			Expr.list(Expr.atom("begin"),
					Expr.list(Expr.atom("define"),Expr.atom("_"), Expr.atom("_")),
						Expr.list(Expr.atom("define"),Expr.atom("_"), Expr.atom("_"))));
		
		assertFalse(a.equals(b));
	}
	
	///////////////////////////////
	
	@Test
	public void test() throws Exception {
		
		Parser parser = new Parser();
		
		String ex1Str = "(+ 3 2)";
		Expr.ExprList ex1 = Expr.list( Expr.atom("+"), Expr.atom("3"), Expr.atom("2") );
		Expr a1 = parser.parse( ex1Str );
		assertEquals( Utils.stripWhitespace( ex1Str ), Utils.stripWhitespace( a1.toString() ) );
		assertEquals( ex1, a1 );

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
			Expr.list( Expr.atom( "lambda" ), Expr.list( Expr.atom("n") ), 
				Expr.list( Expr.atom( "*" ), Expr.atom( "n" ), Expr.atom( "n" ) ) ) );

		Expr a3 = parser.parse( ex3Str );
		assertEquals( Utils.stripWhitespace( ex3Str ), Utils.stripWhitespace( a3.toString() ) );		
		assertEquals( ex3, a3 );
		
		// Diag.println( ex3Str );
		// Diag.println( a3 );		
	}
}

// End ///////////////////////////////////////////////////////////////

