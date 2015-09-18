package corpus;


/**
 * Interface defines the factory methods required to provide 
 * TestData instances 
 *
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "19/09/2015"
)
public interface TestDataCreator<T,J>
{
    /**
     * Method creates a new TestData instance with the given input and 
     * output pairing
     * 
     * @param input a program input
     * @param output the output expected given the program input
     * @return constructed TestData
     */ 
    TestData<T,J> factory(T input, J ouput);
}
