package corpus;

/**
 * Class provides methods to return AST instances
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
public class ASTCreator<T> implements TreeCreator<T>
{
    @Override
    public AbstractSyntaxTree<T> deepCopyFactory(AbstractSyntaxTree<T> tree){
        return new AST<T>(tree);
    }
    
    @Override
    public AbstractSyntaxTree<T> factory(T contents){
        return new AST<T>(contents);
    }    
}
