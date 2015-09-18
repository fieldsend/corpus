package corpus;

import java.nio.file.Path;

/**
 * Defines methods all programs to be loaded/evolved/manipulated 
 * must provide
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015"
)
public interface Program<T>
{
    void setProgram(Path file);
    void setProgram(AbstractSyntaxTree<T> tree);
    void setProgram(String s);
    AbstractSyntaxTree<T> getProgramAsTree();
    String getProgramAsString();
}
