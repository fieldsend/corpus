package corpus;

import java.util.List;
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public interface AbstractSyntaxTree<T>
{
    AbstractSyntaxTree<T> deepCopyFactory(AbstractSyntaxTree<T> tree);
    AbstractSyntaxTree<T> factory(T contents);
    void addChild(AbstractSyntaxTree<T> child);
    T getContents();
    List<AbstractSyntaxTree<T>> getSubtree();    
}
