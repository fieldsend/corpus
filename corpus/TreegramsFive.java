package corpus;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import jscheme.JScheme;


@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015"
)
public class TreegramsFive
{
    /*List<Program> programCorpus = new ArrayList<>();
    int depth;
    public static void main(String[] args) 
    throws IOException {
        TreegramsFive treegrams = new TreegramsFive();
        treegrams.loadCorpus(args[0]);
        treegrams.setDepth(1);
        treegrams.runGP();
    }
    
    void setDepth(int depth) {
        this.depth = depth;
    }
    
    void runGP() {
        Program current = randomSolution();
        int counter = 1;
        while (current.getFitness().intValue() > 0) {
            child = new Program(current); // must do deep copy
            counter ++;
            if (counter % 100 == 0)
                System.out.print(".");
            if (current.getFitness().intValue() <= child.getFitness().intValue())
                current = child;
        }
        System.out.println();
        System.out.println("Final solution: " + current);
        System.out.println("Number of evaluations: " + counter);
    }
    
    
    
    /**
     * Method loads the programs representing the corpus
     
    void loadCorpus(String rootDirectory) 
    throws IOException {
        FileProcessor processor = new FileProcessor(rootDirectory, ".ss");
        List<Path> files = processor.getList();
        
    }
    
    */
}
