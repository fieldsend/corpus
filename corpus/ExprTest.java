package corpus;



import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExprTest
{
    /**
     * Default constructor for test class ExprTest
     */
    public ExprTest()
    {
       
    }

    @Test
    public void equalsTest(){
       Expr a = Expr.list(Expr.atom("start"), Expr.atom("start"), 
                          Expr.list(Expr.atom("begin"),Expr.atom("_"), Expr.atom("_"))); 
       Expr b = Expr.list(Expr.atom("start"), Expr.atom("start"), 
                          Expr.list(Expr.atom("begin"), 
                          Expr.list(Expr.atom("define"),Expr.atom("_"), Expr.atom("_")), 
                          Expr.list(Expr.atom("define"),Expr.atom("_"), Expr.atom("_")))); 
       assertFalse(a.equals(b));
    }
    
    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
}
