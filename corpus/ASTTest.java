package corpus;



import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The test class ASTTest.
 *
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public class ASTTest
{
    AST<String> a;
    
    /**
     * Default constructor for test class ASTTest
     */
    public ASTTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        a = new AST("bob");
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        a = null;
    }
    
    @Test
    public void contentsTest()
    {
        assertEquals("contents not equal to construction argument",a.getContents(),"bob");
    }
    
    @Test
    public void emptyChildrenTest()
    {
        assertEquals("Not 0 children on construction",a.getSubtrees().size(),0);
    }
    
    @Test
    public void simpleDeepCopyTest()
    {
        AST<String> b = new AST(a);
        assertEquals("Not equal on deep copy",a,b);
        assertFalse("Deep copy copying reference",a==b);
    }
    
    @Test
    public void deepCopyTest()
    {
        AST<String> x = new AST("x");
        AST<String> y = new AST("y");
        AST<String> z = new AST("z");
        a.addChild(x);
        y.addChild(z);
        a.addChild(y);
        AST<String> b = new AST(a);
        assertEquals("Not equal on deep copy",a,b);
        assertFalse("Deep copy copying reference",a==b);
    }
    
}
