package corpus;

import jscheme.JScheme;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;

@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015"
)
class SchemeProgram implements Program<String>
{
    private static Random randomGenerator = new Random();
    private AbstractSyntaxTree<String> tree;
    private String programAsString;
    private boolean evaluated = false;
    private int fitness = 0; 
    private Path file;
    //private AST tree;
    
    SchemeProgram(){ }
    
    @Override
    public void setProgram(Path file){
        this.file = file;
        readInFile(file);
        updateTreeRepresentation();
        evaluated = false;
    }
    
    // TODO
    private void readInFile(Path file) {
        this.programAsString = null;
    }
    
    //TODO
    private void updateTreeRepresentation() {
        
    }
    
    //TODO
    private void updateStringRepresentation() {
        
    }
    
    @Override
    public void setProgram(AbstractSyntaxTree<String> tree) {
        this.tree = tree;
        updateStringRepresentation();
        evaluated = false;
    }
    
    @Override
    public void setProgram(String s){
        this.programAsString = s;
        updateTreeRepresentation();
        evaluated = false;
    }

    
    /* TO DO in different class
    private void evaluateOnTestProblems() {
        fitness = 0;
        JScheme interpreter = new JScheme();
        interpreter.load(programAsString);
    
        String name = extractProgramName();
        for (TestInputOutput t : tests){
            Object ouput = interpreter.call(name, t.getInputs());
            fitness += t.compareToOutput(output);
        }
        
    }
    
    //TODO
    private String extractProgramName() {
        
    }*/
    
    @Override
    public AbstractSyntaxTree<String> getProgramAsTree() {
        return tree; 
    } 
    
    @Override
    public String getProgramAsString() {
        return programAsString;
    }    
   
}
