package corpus;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jeep.tuple.Tuple2;
import corpus.Preamble;

//////////////////////////////////////////////////////////////////////

@Preamble(
    author = "Jerry Swan",
    date = "20/09/2015",
    lastModified = "21/09/2015"
)
public final class Utils {
	
	static < A, B > Tuple2< List< A >, List< B > >
	unzip( List< Tuple2< A, B > > l ) {
		List< A > a = new ArrayList< A >();
		List< B > b = new ArrayList< B >();
		for( int i=0; i<l.size(); ++i ) {
			a.add( l.get( i ).getFirst() );
			b.add( l.get( i ).getSecond() );			
		}
		
		return Tuple2.cons( a,b );
	}

	public static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator, boolean parallel) {
	    final Iterable<T> iterable = () -> iterator;
	    return StreamSupport.stream(iterable.spliterator(), parallel);
	}
	
	public static <A, B, C> Stream<C> zip(Stream<A> streamA, Stream<B> streamB, BiFunction<A, B, C> zipper) {
	    final Iterator<A> iteratorA = streamA.iterator();
	    final Iterator<B> iteratorB = streamB.iterator();
	    final Iterator<C> iteratorC = new Iterator<C>() {
	        @Override
	        public boolean hasNext() {
	            return iteratorA.hasNext() && iteratorB.hasNext();
	        }

	        @Override
	        public C next() {
	            return zipper.apply(iteratorA.next(), iteratorB.next());
	        }
	    };
	    final boolean parallel = streamA.isParallel() || streamB.isParallel();
	    return iteratorToFiniteStream(iteratorC, parallel);
	}	
	
	
	private static String readFile( Path p ) {
		try {
			return new Scanner( p ).useDelimiter("\\Z").next();
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
	}
	
	static List< Path > corpusDirectories() {
		//String root = System.getProperty( "user.dir" )  + "/resources/corpus_extended/";
		String root = System.getProperty( "user.dir" )  + "/resources/corpus/";
		String [] files = new File( root ).list();
		return Arrays.asList( files ).stream().map( 
			( String x ) -> Paths.get( root + "/" + x ) ).collect(Collectors.toList());
	}

	///////////////////////////////
	
	private static List< Tuple2< Path, String > > 
	sources( Path path ) {
		try {
			List<Path> paths = FileProcessor.getList( path.toString(), "ss" );
			return paths.stream().map( 
					( Path p ) -> Tuple2.cons( p, readFile( p )) 
				).collect(Collectors.toList());
			
		} catch (IOException e) {
			throw new RuntimeException( e );
		}
	}
	
	///////////////////////////////
	
	static List< Tuple2< Path, String > > corpus() {
		List< Path > dirs = corpusDirectories();
		List< Tuple2< Path, String > 
			> allSources = dirs.stream().map( d -> sources( d ) 
					).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
			
		return allSources;
	}

	///////////////////////////////
	
	public static String stripComments( String s ) {

		String [] strings = s.split( "\n");

		StringBuffer buffer = new StringBuffer();
		for( String ss : strings ) {
			final int index = ss.indexOf( ';' );
			if( index == -1 )
				buffer.append( ss );
			else
				buffer.append( ss.substring( 0, index ) );	    	  
	    }

		return buffer.toString();
	}

	public static String stripWhitespace( String s ) {
		return s.chars().filter( ch -> !Character.isWhitespace( ch ) 
				).mapToObj(c -> Character.toString((char)c))
                .collect(Collectors.joining());
	}
	
	public static String trimWhitespace( String s ) {
		
		List< Tuple2< Character, Integer > > rle = runLengthEncode(s);
		rle.stream().map( t -> { 
			if( Character.isWhitespace( t.getFirst() ) ) {
				final char ch = t.getFirst() == '\n' ? '\n' : ' ';
				return Tuple2.cons( ch, 1 ); 
			}
			else 
				return t; 
		} );
		
		return runLengthDecode( rle );		
	}
	
    public static List< Tuple2< Character, Integer > > 
    runLengthEncode(String source) {
    	List< Tuple2< Character, Integer > > result = new ArrayList< 
    		Tuple2< Character, Integer > >(); 
        // StringBuffer dest = new StringBuffer();
        for( int i=0; i<source.length(); ++i ) {
            int runLength = 1;
            while (i+1 < source.length() && source.charAt(i) == source.charAt(i+1)) {
                ++runLength;
                ++i;
            }
           //  dest.append(runLength);
            // dest.append(source.charAt(i));
            result.add( Tuple2.cons( source.charAt(i), runLength ) );
        }
        return result;
    }
    
    public static String
    runLengthDecode( List< Tuple2< Character, Integer > > l ) {
    	StringBuffer buffer = new StringBuffer();
    	for( int i=0; i<l.size(); ++i ) {

    		char [] fill = new char[ l.get(i).getSecond() ];
    		Arrays.fill( fill, l.get(i).getFirst() );
    		String s = new String(fill);
    		buffer.append(s);
    	}
    	
    	return buffer.toString();
    }
    
    /**
     * Class provides utility to obtain list of files confirming to
     * extension of programming language required for corpus 
     */ 
    @Preamble(
        author = "Jonathan Fieldsend",
        date = "15/09/2015",
        lastModified = "16/09/2015"
    )
    public static final class FileProcessor 
    extends SimpleFileVisitor< Path > {
    	
    	public static Logger LOGGER = Logger.getLogger( 
    			FileProcessor.class.getName() );
    		
    	static {
    		ConsoleHandler handler = new ConsoleHandler();
    		handler.setLevel(Level.ALL);
    		handler.setFormatter(new SimpleFormatter());
    		LOGGER.addHandler(handler);		
    	}

    	///////////////////////////
    	
        private List<Path> files = new ArrayList<>();
        private String fileExtension;
        
        
        FileProcessor(String directoryName, String fileExtension) 
        throws IOException {
            this.fileExtension = fileExtension;
            Files.walkFileTree(Paths.get(directoryName), this);
        }
        
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        	
        	LOGGER.info("Processing file:" + file);
        	
            if (file.toString().regionMatches(true,file.toString().length()-fileExtension.length(), fileExtension, 0, fileExtension.length()))
                files.add(file);
            return FileVisitResult.CONTINUE;
        }

        // Print each directory visited in turn.
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) 
        throws IOException {
        	LOGGER.info("Processing directory:" + dir);
          return FileVisitResult.CONTINUE;
        }
            
        // Print exception message if encountered
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) {
        	LOGGER.warning( e.getMessage() );
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * Method returns the list of paths to files in directoryName and any of its
         * subdirectories, which end with the specified file extension
         * 
         * @param directoryName name of root directory to search in
         * @param fileExtension extension files must have to be returned in the list
         * @return list of paths to files
         */ 
        public static List<Path> getList(String directoryName, String fileExtension) 
        throws IOException {
            FileProcessor temp = new FileProcessor(directoryName, fileExtension);
            return temp.files;
        }
    }
}

// End ///////////////////////////////////////////////////////////////

