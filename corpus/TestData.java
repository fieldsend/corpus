package corpus;

/**
 * TestData instances wrap together an input and output pair 
 * associated with a correctly performing program.
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "18/09/2015"
)
public interface TestData<T,J>
{
    /**
     * Repaces the stored input and output with the instance arguments.
     * 
     * @param input program input
     * @param output expected output given program input
     */
    void replaceTestData(T input, J output);
    
    /**
     * Method returns the stored input.
     * 
     * @return stored input object
     */
    T getInput();
    
    /**
     * Method returns true if argument equals the stored (expected)
     * output for a correctly functioning program
     * 
     * @param programOutput the output from a program
     * @return true if programOutput matches stored output
     */
    boolean matchesOutput(J programOutput);
}
