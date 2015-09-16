package corpus;


/**
 * Interface defines the factory methods required to provide 
 * AbstractSyntaxTree instances 
 *
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public interface TreeCreator<T>
{
    /**
     * Method makes a deep copy of the argument tree and returns it
     * 
     * @param tree to be copied
     * @return a deep copy of tree
     */
    AbstractSyntaxTree<T> deepCopyFactory(AbstractSyntaxTree<T> tree);
    
    /**
     * Method creates a new tree with the contents provided, and no
     * children
     * 
     * @param contents to be put in tree produced
     * @return constructed tree
     */ 
    AbstractSyntaxTree<T> factory(T contents);
}
