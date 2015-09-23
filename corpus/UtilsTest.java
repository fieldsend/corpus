package corpus;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.*;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import jeep.lang.Diag;
import jeep.tuple.Tuple2;


public class UtilsTest
{
    public static Logger LOGGER = Logger.getLogger( 
		UtilsTest.class.getName() );
	
	static {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		handler.setFormatter(new SimpleFormatter());
		LOGGER.addHandler(handler);		
	}
	
	///////////////////////////////
	
	private static Expr parse( String source ) {
		Parser parser = new Parser();
		try {
			return parser.parse( source );
		} catch (Exception e) {
			throw new RuntimeException( e );
		} 
	}
	
	///////////////////////////////
	
	@Test
	public void testParseCorpus() {
		
		List< Tuple2< Path, String > > sources = Utils.corpus();
		
		sources = sources.stream().filter( 
			t -> !t.getFirst().toString().endsWith( "value.ss" ) 
				).collect(Collectors.toList());

		List< Object > parsed = sources.stream().map( 
				t -> parse( t.getSecond() ) 
					).collect(Collectors.toList());
		
		assertEquals( sources.size(), parsed.size() );
	}
	
	@Test
	public void testRegenerateParsedSource() {

		List< Tuple2< Path, String > > sources = Utils.corpus();

		List< String > regeneratedSources = sources.stream().map( 
			t -> parse( t.getSecond() ).toString() // ToSourceCode.objectListToString( parse( t.getSecond() ) ) 
				).collect(Collectors.toList());
		
		Tuple2< List< String> , List<String > > t = Utils.unzip( Utils.zip( sources.stream(), 
			regeneratedSources.stream(), 
				( a, b ) -> Tuple2.cons( a.getSecond(), b 
					) ).collect(Collectors.toList()) );
		
		assertEquals( t.getFirst().size(), t.getSecond().size() );
		
		for( int i=0; i<t.getFirst().size(); ++i ) {
			
			String expected = Utils.stripWhitespace( Utils.stripComments( t.getFirst().get( i ) ) );
			String actual = Utils.stripWhitespace( t.getSecond().get( i ) ); 
			
			LOGGER.info( "expected:" + expected + " actual: " + actual );			
			
			assertEquals( expected, actual );
		}
	}
}
