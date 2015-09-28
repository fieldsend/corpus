package corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import jscheme.JScheme;
import jscheme.SchemeException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

                /*
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
                 */ 
            }
            index++;
        }
        /*
        // calculate negative log probability of the programs in the corpus
        for (Tuple2<Program, Program> p : programAndTests) {
        System.out.println(corpus.probabilityOfProgram(p.getFirst()));
        }
         */
        // now try out some basic optimisation
        int targetProgram = 0;
        if (args.length>0)
            targetProgram = Integer.parseInt(args[0]);
        // load test cases
        String targets = programAndTests.get(targetProgram).getSecond().getProgramAsString();

        Expr currExpr = corpus.generateRandomExpression();
        Program currentProgram = new SchemeProgram();
        currentProgram.setProgram(currExpr);

        double fitCurr = getFitness(currentProgram.getProgramAsString(), targets, 10);
        System.out.println("Random sol: " + currExpr);
        System.out.println("Random sol fitness: " + fitCurr);
        double fitChild;
        int evals = 1;
        while (fitCurr!=1.0){
            currExpr = corpus.generateRandomExpression();
            currentProgram.setProgram(currExpr);
            fitChild = getFitness(currentProgram.getProgramAsString(), targets, 10);
            //if (evals % 100 == 0)
                System.out.print(".");
            if (fitChild>fitCurr){
                Program program = new SchemeProgram();
                program.setProgram(currExpr);
                corpus.addToCorpus(program);
            }
            evals++;
        }
        System.out.println("Total evals to hit:" + evals);
    }

    private static double getFitness(String program, String targets, int maxSeconds){
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Double> task = new Callable<Double>() {
                public Double call() {
                    return fitnessCall(program,targets);
                }
            };
        Future<Double> future = executor.submit(task);
        try {
            return future.get(maxSeconds, TimeUnit.SECONDS); 
        } catch (Exception ex) {
            return 0.0; // any timeout or execution exceptions should result in a zero fitness
        } finally {
            future.cancel(true); // try to interrupt
            if (!executor.isTerminated())
                executor.shutdownNow(); // Stop the code that hasn't finished.
        }
    }
    // resources/corpus/tls-08-lambda-the-ultimate/rember-f2_tests.ss

    private static double fitnessCall(String program, String targets) {
        JScheme js = new JScheme();
        js.load(targets);
        //System.out.println("entering getFitness");
        try {
            js.load(program);
        } catch (Exception e) {
            System.out.println("Program error");
            return 0.0; // problem with program
        }
        int numberOfTests = Integer.parseInt(js.eval("(length testcases)").toString());
        String start = "(list (apply prog (car (list-ref testcases ";
        String end = "))))";
        int passed = 0;
        //System.out.println("got test lengths");

        for (int i=0; i<numberOfTests; i++){
            //Object output = js.eval(start + i + end);
            //System.out.println(output);
            try {
                if (js.eval("(equal? (list (apply prog (car (list-ref testcases " + i + ")))) (cdr (list-ref testcases " + i + ")))").toString().equals("true")){
                    passed++;
                }
            } catch (Exception e) {
                System.out.println("Program error");
                return 0.0; // problem with program processing the inputs
            }
            //System.out.println(js.eval("(cdr (list-ref testcases " + i + "))"));
        }
        return passed/((double)numberOfTests);
    }
}
