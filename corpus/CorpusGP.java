package corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import jscheme.JScheme;

import jeep.tuple.Tuple2;

@Preamble(
author = "Jonathan Fieldsend",
date = "23/09/2015"
)
public class CorpusGP
{

    public static void main(String[] args) throws Exception {

        // parse programs

        // gets all .ss files in path and pulls out file contents
        // as a string
        List< Tuple2< Path, String > > sources = Utils.corpus();
        List<String> testList = new ArrayList<>();
        List<Program> programs = new ArrayList<>();
        Corpus corpus = new SchemeCorpus(2);
        // Find all tests currently in corpus
        for (Tuple2<Path, String> t : sources){
            String temp = t.getFirst().toString();
            if (temp.endsWith("_tests.ss")){
                testList.add(temp.substring(0,temp.length()-9) + ".ss");
            }
        }

        // add to corpus all programs for which there are current tests
        for (Tuple2<Path, String> t : sources){       
            if (testList.contains(t.getFirst().toString())){
                System.out.println("Adding: " + t.getFirst().toString());
                Program p = new SchemeProgram();
                p.setProgram(t.getSecond()); 
                corpus.addToCorpus(p);
                programs.add(p);
                System.out.println("Added: " + t.getFirst().toString());
            }
        }
        
        // calculate negative log probability of the programs in the corpus
        for (Program p : programs) 
            System.out.println(corpus.probabilityOfProgram(p));
//System.out.println(corpus.probabilityOfProgram(programs.get(0)));
    }

}
