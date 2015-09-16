package corpus;

import java.util.List;
import java.util.ArrayList;
/**
 * Concrete implementation of the AbstractSyntaxTree
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public class AST<T> implements AbstractSyntaxTree<T>
{
    private List<AbstractSyntaxTree<T>> children = new ArrayList<>();
    private T contents;
    
    // generate a new instance with contents argument, and no children
    private AST(T contents){
        this.contents = contents;
    }
    
    // uses tree argument for a deep copy constructor
    private AST(AbstractSyntaxTree<T> tree){
        this.contents = tree.getContents();
        for (AbstractSyntaxTree a : tree.getSubtree())
            tree.addChild(deepCopy(a));
    }
    
    @Override
    public AbstractSyntaxTree<T> deepCopyFactory(AbstractSyntaxTree<T> tree){
        return new AST(tree);
    }
    
    @Override
    public AbstractSyntaxTree<T> factory(T contents){
        return new AST(contents);
    }
    
    // Method recusively copies argument tree to create a deep copy 
    private AbstractSyntaxTree<T> deepCopy(AbstractSyntaxTree<T> tree) {
        AbstractSyntaxTree temp = new AST(tree.getContents());
        for (AbstractSyntaxTree a : tree.getSubtree())
            tree.addChild(deepCopy(a));
        return temp;    
    }
    
    @Override
    public void addChild(AbstractSyntaxTree<T> child){
        children.add(child);
    }
    
    @Override
    public T getContents() {
        return contents;
    }
    
    @Override
    public List<AbstractSyntaxTree<T>> getSubtrees() {
        return children;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AST) { // false if not same type
            AST temp = (AST) obj;
            if (this.contents.equals(temp.contents)){ // false if contents not the same
                if (this.children.size() == temp.children.size()){ 
                    for (int i=0; i< this.children.size(); i++){
                        if (this.children.get(i).equals(temp.children.get(i))==false){
                            return false; // false if any children (subtrees) are not the same
                        }
                    }
                    return true; // entire tree matches  
                }
            }
        }
        return false;
    }
}
