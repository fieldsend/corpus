package corpus;

import java.util.List;
/**
 * Interface defines the methods all Abstract Syntax Trees manipulated by the 
 * GP need to provide
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public interface AbstractSyntaxTree<T>
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
    /**
     * Method adds the child tree to this tree
     * 
     * @param child (subtree) to add
     */ 
    void addChild(AbstractSyntaxTree<T> child);
    /**
     * Method returns the contents of the node at the top of
     * the tree (i.e. head)
     * 
     * @return The contents at the top of the tree
     */ 
    T getContents();
    /**
     * Method returns all the subtrees beneath the head of this 
     * tree, in list
     * 
     * @return list of subtrees
     */ 
    List<AbstractSyntaxTree<T>> getSubtrees();    
}
