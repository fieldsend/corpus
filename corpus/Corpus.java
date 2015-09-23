package corpus;

import java.util.Map;
import java.util.List;
/**
 * Defines the methods all program corpi need to provide for their access
 * and minipulation
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "16/09/2015",
    lastModified = "19/09/2015"
)
public interface Corpus
{
    /**
     * Method returns map of fragments to extended fragments (one level deeper)
     * constructed from this corpus.
     * 
     * @return fragment map
     */
    Map<Expr,List<Expr>> getFragmentMap();
     
    /**
     * Add program to the corpus, to be fragmented and mapped.
     */
    void addToCorpus(Program program);
    
    /**
     * Generate a random expression from the corpus. Will return null if the corpus has 
     * not had any programs added to it
     * 
     * @return tree randomly generated from the corpus
     */
    Expr generateRandomExpression();
    
    /**
     * Gives the probability of a program given the corpus contents
     * 
     * @return probability of the program being randomly generated from the corpus
     */
    double probabilityOfProgram(Program program);
    
    
    /**
     * Mutates the argument given the data in the corpus
     * 
     * @param expression to mutate
     * @return a mutated version of the argument
     */
    Expr mutate(Expr expressionToMutate);
}
