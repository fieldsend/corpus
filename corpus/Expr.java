package corpus;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

//////////////////////////////////////////////////////////////////////
@Preamble(
    author = "Jerry Swan",
    date = "20/09/2015",
    lastModified = "21/09/2015"
)
public interface Expr {

	static Expr atom( Object o ) {
		return new Atom( o );
	}

	static ExprList list( Expr... args) {
		return new ExprList( args );
	}
	
	/****
	static ExprList list( Expr e1 ) {
		return new ExprList( e1 );
	}
	
	static ExprList list( Expr e1, Expr e2 ) {
		return new ExprList( e1, e2 );
	}
	
	static ExprList list( Expr e1, Expr e2, Expr e3 ) {
		return new ExprList( e1, e2, e3 );
	}
	static ExprList list( Expr e1, Expr e2, Expr e3, Expr e4 ) {
		return new ExprList( e1, e2, e3, e4 );
	}
****/
	///////////////////////////////
	
	public static final class Atom implements Expr {
		
		private final Object value;
		
		///////////////////////////
		
		public Atom( Object value ) {
			this.value = value;
		}
		
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode( this );
		}

		@Override
		public boolean equals( Object o ) {
			return EqualsBuilder.reflectionEquals( this, o );
		}
		
		@Override
		public String toString() {
			return value.toString();
		}
	}
	
	///////////////////////////////	

	public static final class ExprList 
	extends ArrayList< Expr > implements Expr {

		private static final long serialVersionUID = 7256318525839341507L;
		
		///////////////////////////

		// public ExprList() {}
		
		public ExprList( Expr ... args ) {
			for( Expr e : args )
				add( e );
		}

		/******
		public ExprList( Expr e1, Expr e2 ) {
			add( e1 ); add( e2 );
		}
		
		public ExprList( Expr e1, Expr e2, Expr e3 ) {
			add( e1 ); add( e2 ); add( e3 );
		}

		public ExprList( Expr e1, Expr e2, Expr e3, Expr e4 ) {
			add( e1 ); add( e2 ); add( e3 ); add( e4 );
		}

		******/

		///////////////////////////

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode( this );
		}

		@Override
		public boolean equals( Object o ) {
			return EqualsBuilder.reflectionEquals( this, o );
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append('(');
			for( int i=0; i<size(); ++i )
				buffer.append( " " + get(i) );
			
			buffer.append(')');
			
			return buffer.toString();
		}
	}
}

// End ///////////////////////////////////////////////////////////////