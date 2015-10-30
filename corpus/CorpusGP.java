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
        // parse programs

        // gets all .ss files in path and pulls out file contents
        // as a string
        List<Program> programs = new ArrayList<>();
        Corpus corpus = new SchemeCorpus(3,0.0); //0.8
        List<Tuple2<Program,Program>> programAndTests = getProgramsAndTests();
        for (Tuple2<Program,Program> t : programAndTests){
            System.out.println("Adding to corpus");
            corpus.addToCorpus(t.getFirst());
            System.out.println("Added");
        }
        int index = 0; 
        //errorInducing.add("/Users/jefields/Desktop/desk/github_repos/corpus/resources/corpus/recursive-programs/count_tests.ss");
        double[] results = new double[programAndTests.size()];
        //js.load("(define (atom? x) (not (pair? x)))");

        for (Tuple2<Program, Program> p : programAndTests) {
            results[index] = 0.0;
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
        int timeouts = 0, runtimeErrors = 0, syntaxErrors = 0, evals = 1;
        while (fitCurr!=1.0){
            currExpr = corpus.generateRandomExpression();
            currentProgram.setProgram(currExpr);
            fitChild = getFitness(currentProgram.getProgramAsString(), targets, 10);
            if (fitChild ==-3.0)
                timeouts++;
            else if (fitChild ==-2.0)
                runtimeErrors++;
            else if (fitChild ==-1.0)
                syntaxErrors++;
            if (evals % 100 == 0)
                System.out.println("best fitness so far: " + fitCurr);
            System.out.print(".");
            if (fitChild>fitCurr){
                Program program = new SchemeProgram();
                program.setProgram(currExpr);
                corpus.addToCorpus(program);
                fitCurr = fitChild;
            }
            evals++;
        }
        System.out.println();
        System.out.println("Total evals to hit:" + evals);
        System.out.println("Total timeouts:" + timeouts);
        System.out.println("Total syntax errors:" + syntaxErrors);
        System.out.println("Total runtime errors:" + runtimeErrors);
        System.exit(0);
    }
    
    static List<Tuple2<Program,Program>> getProgramsAndTests()
        throws Exception {
        List<String> testList = new ArrayList<>();
        
        List< Tuple2< Path, String > > sources = Utils.corpus();
        List<Tuple2<Program,Program>> programAndTests = new ArrayList<>();
        // make list containing program and testcase pairs - not efficient as is!
        for (Tuple2<Path, String> t : sources){
            String temp = t.getFirst().toString();
            //if (temp.endsWith("extended_tests.ss")){ // tests exist, so get corresponding program
            if (temp.endsWith("tests.ss")){ // tests exist, so get corresponding program
                System.out.println("processing pairs: "+ temp);
                Program tests = new SchemeProgram();
                tests.setProgram(t.getSecond());
                tests.setName(temp);
                String programName = temp.substring(0,temp.length()-9) + ".ss";
                //String programName = temp.substring(0,temp.length()-17) + ".ss";
                
                for (Tuple2<Path, String> p : sources){
                    if (p.getFirst().toString().equals(programName)){
                        testList.add(temp);
                        Program program = new SchemeProgram();
                        program.setProgram(p.getSecond());
                        programAndTests.add(Tuple2.cons(program,tests));
                        break;
                    }
                }
            }
        }
        return programAndTests;
    }
    
    static double getFitness(String program, String targets, int maxSeconds){
        /*ExecutorService executor = Executors.newCachedThreadPool();
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
        }*/
        final DoubleWrapper result = new DoubleWrapper();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future future = executor.submit(new Thread(){public void run(){result.setValue(fitnessCall(program,targets));}});
        try { 
            future.get(maxSeconds, TimeUnit.SECONDS); 
        }
        catch (Exception ex) {
            System.out.print("to");
            result.setValue(-3.0); // indicate timeout issue
        } 
        if (!executor.isTerminated())
            executor.shutdownNow(); 
        return result.getValue();
    }
    // resources/corpus/tls-08-lambda-the-ultimate/rember-f2_tests.ss

    private static double fitnessCall(String program, String targets) {
        JScheme js = new JScheme();
        js.load(targets);
        //System.out.println("entering getFitness");
        try {
            js.load(program);
        } catch (Exception e) {
            System.out.print("ei");
            return -1.0; // indicate syntax error with program
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
                } else if(js.eval("(string=? (list (apply prog (car (list-ref testcases " + i + ")))) (cdr (list-ref testcases " + i + ")))").toString().equals("true")){
                    passed++; //use when comparing procedures
                }
                else {
                    System.out.println("INPUT: " + js.eval("(car (list-ref testcases " + i + "))").toString());
                    System.out.println("WANTED: " + js.eval("(cdr (list-ref testcases " + i + "))").toString());
                    System.out.println("RETURNED: " + js.eval("(list (apply prog (car (list-ref testcases " + i + "))))").toString());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return -2.0; // problem with program processing the inputs/running
            }
            //System.out.println(js.eval("(cdr (list-ref testcases " + i + "))"));
        }
        return passed/((double)numberOfTests);
    }
    
    private static class DoubleWrapper {
        private double value;
        
        private double getValue(){
            return value;
        }
        private void setValue(double value){
            this.value = value;
        }
    }
}
