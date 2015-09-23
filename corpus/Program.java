package corpus;

import java.nio.file.Path;
import java.io.IOException;

/**
 * Defines methods all programs to be loaded/evolved/manipulated 
 * must provide
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015"
)
public interface Program
{
    void setProgram(Path file) throws IOException, Exception;
    void setProgram(Expr expression);
    void setProgram(String s) throws Exception;
    Expr getProgramAsExpression();
    String getProgramAsString();
}
