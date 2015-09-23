package corpus;

import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;
import org.apache.commons.io.FileUtils;

/**
 * Class represents Scheme programs and maintains raw program Strings as well as
 * Expr representations
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "21/09/2015",
    lastModifiedBy = "Jonathan Fieldsend"
)
class SchemeProgram implements Program
{
    private static Random randomGenerator = new Random();
    private static Parser parser = new Parser();
    private Expr expression;
    private String programAsString;
    private int fitness = 0; 
    private Path file;
    //private AST tree;
    
    SchemeProgram(){ }
    
    @Override
    public void setProgram(Path file) 
    throws IOException, Exception {
        this.file = file;
        readInFile(file);
    }
    
    private void readInFile(Path file) 
    throws IOException, Exception {
        this.programAsString = FileUtils.readFileToString(file.toFile(), StandardCharsets.UTF_8);
        setProgram(programAsString);
    }
    
    @Override
    public void setProgram(Expr expression) {
        this.expression = expression;
        programAsString = expression.toString();
    }
    
    @Override
    public void setProgram(String s) 
    throws Exception {
        this.programAsString = s;
        this.expression = parser.parse(s);
        System.out.println(expression.toString());
    }

    @Override
    public Expr getProgramAsExpression() {
        return expression; 
    } 
    
    @Override
    public String getProgramAsString() {
        return programAsString;
    }    
   
}
