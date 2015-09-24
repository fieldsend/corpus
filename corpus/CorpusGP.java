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
        JScheme js = new JScheme();
        // parse programs

        // gets all .ss files in path and pulls out file contents
        // as a string
        List< Tuple2< Path, String > > sources = Utils.corpus();
        List<String> testList = new ArrayList<>();
        List<Program> programs = new ArrayList<>();
        Corpus corpus = new SchemeCorpus(2);
        List<Object> inputs = new ArrayList<>();
        List<Object> outputs = new ArrayList<>();
        List<Tuple2<Program,Program>> programAndTests = new ArrayList<>();
        
        // make list containing program and testcase pairs - not efficient as is!
        for (Tuple2<Path, String> t : sources){
            String temp = t.getFirst().toString();
            if (temp.endsWith("_tests.ss")){ // tests exist, so get corresponding program
                Program tests = new SchemeProgram();
                tests.setProgram(t.getSecond());
                String programName = temp.substring(0,temp.length()-9) + ".ss";
                for (Tuple2<Path, String> p : sources){
                    if (p.getFirst().toString().equals(programName)){
                        Program program = new SchemeProgram();
                        program.setProgram(p.getSecond());
                        programAndTests.add(Tuple2.cons(program,tests));
                        corpus.addToCorpus(program);
                        break;
                    }
                }
            }
        }
                
        // Find all tests currently in corpus
        /*for (Tuple2<Path, String> t : sources){
            String temp = t.getFirst().toString();
            if (temp.endsWith("_tests.ss")){
                Program p = new SchemeProgram();
                p.setProgram(t.getSecond()); 
                System.out.println(js.load(p.getProgramAsString()));
                System.out.println(js.getGlobalValue("testcases"));
                System.out.println(js.eval("(length testcases)"));
                
                System.out.println(js.eval("(list-ref testcases 1)"));
                System.out.println(js.eval("(list-ref (list-ref testcases 1) 0)"));
                inputs.add(js.eval("(list-ref (list-ref testcases 1) 0)"));
                outputs.add(js.eval("(list-ref (list-ref testcases 1) 1)"));
                testList.add(temp.substring(0,temp.length()-9) + ".ss");
            }
        }*/
        /*
        int index = 0;
        // add to corpus all programs for which there are current tests
        for (Tuple2<Path, String> t : sources){       
            if (testList.contains(t.getFirst().toString())){
                System.out.println("Adding: " + t.getFirst().toString());
                Program p = new SchemeProgram();
                p.setProgram(t.getSecond()); 
                corpus.addToCorpus(p);
                programs.add(p);
                System.out.println("Added: " + t.getFirst().toString());
                // now evaluate on test data
                System.out.println("evaluating");
                System.out.println(js.load(p.getProgramAsString()));
                
                try {
                    System.out.println(js.eval("(prog (list-ref (list-ref testcases 1) 0) )");
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                index++;
            }
        }*/
        for (Tuple2<Program, Program> p : programAndTests) {
            corpus.addToCorpus(p.getFirst());
            // define program and test cases in the Scheme environment
            js.load(p.getFirst().getProgramAsString());
            js.load(p.getSecond().getProgramAsString());
            System.out.println("evaluating");
            System.out.println(js.getGlobalValue("prog"));
            System.out.println(js.getGlobalValue("testcases"));
            // evalate program on its zeroth testcase input
            System.out.println(js.eval("(prog (list-ref (list-ref testcases 0) 0) )"));
        }
        
        
        // calculate negative log probability of the programs in the corpus
        for (Tuple2<Program, Program> p : programAndTests) {
            System.out.println(corpus.probabilityOfProgram(p.getFirst()));
        }
     }

    
}
