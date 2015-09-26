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
        Corpus corpus = new SchemeCorpus(2,0.8);
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
                        testList.add(temp);
                        Program program = new SchemeProgram();
                        program.setProgram(p.getSecond());
                        programAndTests.add(Tuple2.cons(program,tests));
                        System.out.println("Adding to corpus");
                        corpus.addToCorpus(program);
                        System.out.println("Added");
                        
                        break;
                    }
                }
            }
        }
        
        int index = 0; 
        List<String> errorInducing = new ArrayList<>();
        //errorInducing.add("/Users/jefields/Desktop/desk/github_repos/corpus/resources/corpus/recursive-programs/count_tests.ss");
        double[] results = new double[programAndTests.size()];
        //js.load("(define (atom? x) (not (pair? x)))");
        
        for (Tuple2<Program, Program> p : programAndTests) {
            results[index] = 0.0;
            if ((errorInducing.contains(testList.get(index))==false) && (p.getFirst().getProgramAsString().contains("atom")==false)){
                corpus.addToCorpus(p.getFirst());
               
                
                // define program and test cases in the Scheme environment
                js.load(p.getFirst().getProgramAsString());
                js.load(p.getSecond().getProgramAsString());
                System.out.println("evaluating: " + testList.get(index));
                System.out.println(js.eval("(length testcases)"));
                //System.out.println(js.eval("(car (list-ref testcases 0))"));
                // evalate program on its zeroth testcase input
                results[index] = getFitness(js); // proportion of tests passed
                if (results[index]<1.0)
                    System.out.println("Error: " + results[index]);
                   
            }
            index++;
        }

        // calculate negative log probability of the programs in the corpus
        for (Tuple2<Program, Program> p : programAndTests) {
            System.out.println(corpus.probabilityOfProgram(p.getFirst()));
        }
        // now try out some basic optimisation
        int targetProgram = 0;
        if (args.length>0)
            targetProgram = Integer.parseInt(args[0]);
        // load test cases
        js.load(programAndTests.get(targetProgram).getSecond().getProgramAsString());
            
        Expr currExpr = corpus.generateRandomExpression();
        Program currentProgram = new SchemeProgram();
        currentProgram.setProgram(currExpr);
        System.out.println(currExpr);
        js.load(currentProgram.getProgramAsString());
        double fitCurr = getFitness(js);
    }

    private static double getFitness(JScheme js){
        int numberOfTests = Integer.parseInt(js.eval("(length testcases)").toString());
        String start = "(list (apply prog (car (list-ref testcases ";
        String end = "))))";
        int passed = 0;
        for (int i=0; i<numberOfTests; i++){
            Object output = js.eval(start + i + end);
            System.out.println(output);
            if (js.eval("(equal? (list (apply prog (car (list-ref testcases " + i + ")))) (cdr (list-ref testcases " + i + ")))").toString().equals("true")){
                passed++;
            }
            System.out.println(js.eval("(cdr (list-ref testcases " + i + "))"));
        }
        return passed/((double)numberOfTests);
    }
    // resources/corpus/tls-08-lambda-the-ultimate/rember-f2_tests.ss

}
