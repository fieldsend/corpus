package corpus;

import java.util.Map;
import java.util.List;

@Preamble(
    author = "Jonathan Fieldsend",
    date = "16/09/2015",
    lastModified = "19/09/2015"
)
public interface Corpus<T>
{
    /**
     * Method returns map of fragments to extended fragments (one level deeper)
     * constructed from this corpus.
     * 
     * @return fragment map
     */
    Map<AbstractSyntaxTree<T>,List<AbstractSyntaxTree<T>>> getFragmentMap();
     
    /**
     * Add program to the corpus, to be fragmented and mapped. If setDepth
     */
    void addToCorpus(Program<T> program);
    
    /**
     * Generate a random tree from the corpus. Will return null if the corpus has 
     * not had any programs added to it
     * 
     * @return tree randomly generated from the corpus
     */
    AbstractSyntaxTree<T> generateRandomTree();
    
    /**
     * Gives the probability of a program given the corpus contents
     * 
     * @return probability of the program being randomly generated from the corpus
     */
    double probabilityOfProgram(Program<T> program);
    
    
    /**
     * Mutates the argument given the data in the corpus
     * 
     * @param tree to mutate
     * @return a mutated version of the argument
     */
    AbstractSyntaxTree<T> mutate(AbstractSyntaxTree<T> treeToMutate);
}
