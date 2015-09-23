package corpus;

import java.util.ArrayList;
import java.util.List;

import jeep.lang.Diag;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

//////////////////////////////////////////////////////////////////////

public interface Expr {

	static Expr atom( Object o ) {
		return new Atom( o );
	}

	static ExprList list( Expr... args) {
		return new ExprList( args );
	}
	
	static ExprList list( Expr e1 ) {
		return new ExprList( e1 );
	}
	
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
			/*********
			if( !( o instanceof Atom ) ) 
				return false;
			
			Atom rhs = (Atom)o;
			Diag.println( value.getClass() + " " + rhs.value.getClass() );
			
			final boolean result = value.equals( rhs.value );
			// return value.equals( rhs.value );
			Diag.println( "<<" + value + ">>"  + " equals <<" + rhs.value + ">>? " + result );
			return result;
			*********/
		}
		
		@Override
		public String toString() {
			return value.toString();
		}
	}
	
	///////////////////////////////	

	public static final class ExprList 
	// extends ArrayList< Expr > 
	implements Expr {

		// private static final long serialVersionUID = 7256318525839341507L;
		private List< Expr > impl = new ArrayList< Expr >();
		
		///////////////////////////

		public ExprList( Expr ... args ) {
			for( Expr e : args )
				impl.add( e );
		}

		///////////////////////////

		public int size() { return impl.size(); }
		public Expr get( int i ) { return impl.get( i ); }
		
		public void add( Expr e ) { impl.add( e ); }
		
		///////////////////////////
		
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode( this );
		}

		@Override
		public boolean equals( Object o ) {
			return EqualsBuilder.reflectionEquals( this, o );
			/****
			if( !( o instanceof ExprList ) ) 
				return false;
			
			ExprList rhs = (ExprList)o;
			if( size() != rhs.size() ) {
				// Diag.println();				
				return false;
			}
			else {
				for( int i=0; i<size(); ++i ) {
					// Diag.println( "<<" + get( i ) + ">>" );
					// Diag.println( "<<" + rhs.get( i ) + ">>" );				
					if( !get( i ).equals( rhs.get( i ) ) ) {
						// Diag.println();						
						return false;
					}
				}
			}
			
			return true;
			****/
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