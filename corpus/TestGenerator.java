package corpus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
//import java.nio.file.Path;
import jscheme.JScheme;
import jscheme.SchemeException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



import jeep.tuple.Tuple2;

/**
* Class generates new test input/output pairs for corpus programs
*/
@Preamble(
author = "Jonathan Fieldsend",
date = "07/10/2015"
)
public class TestGenerator
{
    private static int maxSeconds = 2;
    public static void main(String[] args) throws Exception {
        //checkProgramsPassTheirTests();
        generateTests();
    }
    
    private static void checkProgramsPassTheirTests() throws Exception{
        List<Program> programs = new ArrayList<>();
        List<Tuple2<Program,Program>> programAndTests = CorpusGP.getProgramsAndTests();
        int index = 0; 
        //errorInducing.add("/Users/jefields/Desktop/desk/github_repos/corpus/resources/corpus/recursive-programs/count_tests.ss");
        double[] results = new double[programAndTests.size()];
        System.out.println("CHECKING PROGRAMS");
        for (Tuple2<Program, Program> p : programAndTests) {
            // define program and test cases in the Scheme environment
            results[index] = CorpusGP.getFitness(p.getFirst().getProgramAsString(), p.getSecond().getProgramAsString(), 10);
            index++;
            System.out.println(p.getSecond().getName());
            System.out.println("Program: " + index + ", sucess rate: " + results[index-1]);
            if (results[index-1]<1.0)
                System.out.println(p.getSecond().getName());
        }
    }
    
    private static void generateTests() throws Exception{
        double oldAverage = 0;
        double newAverage = 0;
        List<Program> programs = new ArrayList<>();
        List<Tuple2<Program,Program>> programAndTests = CorpusGP.getProgramsAndTests();
        //List<Tuple2<Program,Set<Tuple2<String,String>>>> programAndLegalTests = new ArrayList<>();
        // now apply in turn each test, in each test case, for every program, to each program
        int i=0;
        System.out.println("GENERATE TESTS");
        for (Tuple2<Program,Program> p : programAndTests){
            Set<Tuple2<String,String>> allProcessableQuotedTests = new HashSet<>();
            Set<Tuple2<String,String>> allProcessableQuasiquotedTests = new HashSet<>();
            for (Tuple2<Program,Program> t : programAndTests){
                List<Tuple2<String,String>> processedTests = getProcessed(p.getFirst().getProgramAsString(),t.getSecond().getProgramAsString());
                // processedTests now contains indices of all tests passed
                if (t.getSecond().getProgramAsString().contains("  `("))
                    allProcessableQuasiquotedTests.addAll(processedTests);
                else
                    allProcessableQuotedTests.addAll(processedTests);
            }
            //programAndLegalTests.add(Tuple2.cons(p.getFirst(),allProcessableTests));
            int oldNumber = getNumberOfTests(p.getSecond().getProgramAsString());
            int newNumber = allProcessableQuasiquotedTests.size() + allProcessableQuotedTests.size();
            System.out.println("Number of original: " + oldNumber + ", number in extended: "+ newNumber);
            oldAverage += oldNumber;
            newAverage += newNumber;
            i++;
            System.out.println(oldAverage/i + " : " + newAverage/i);
            System.out.println("QuasiQUOTED");
            for (Tuple2<String,String> t : allProcessableQuasiquotedTests){
                System.out.println(t.getSecond() + " " + t.getFirst());
            }
            System.out.println("QUOTED");
            for (Tuple2<String,String> t : allProcessableQuotedTests){
                System.out.println(t.getSecond() + " " + t.getFirst());
            }
            writeExtendedTestFile(p.getSecond().getName(),allProcessableQuotedTests,false);
            writeExtendedTestFile(p.getSecond().getName(),allProcessableQuasiquotedTests,true);
        }
        oldAverage /=  programAndTests.size();
        newAverage /=  programAndTests.size();
        System.out.println("Average number in original: " + oldAverage + ", average number in extended: " + newAverage);
        System.exit(0);
    }

    private static void writeExtendedTestFile(String oldTestName, Set<Tuple2<String,String>> allProcessableTests, boolean quasiQuoted)
        throws IOException {
            
        String testName = oldTestName.substring(0,oldTestName.length()-9) + (quasiQuoted ? "_extended_quasi_quoted_tests.ss" : "_extended_quoted_tests.ss");
        Path testFile = Paths.get(testName);
        Files.createFile(testFile);
        try(PrintWriter printWriter = new PrintWriter(new FileWriter(testFile.toFile())) ) {
            printWriter.println(";; defines testcases as a list, where each element is a list, el[0] list of inputs, el[1] result");
            printWriter.println("(define testcases");
            String start = quasiQuoted ? "  `(" : "  '(";
            printWriter.println(start);
            for (Tuple2<String,String> t : allProcessableTests ) {
                printWriter.println("    (" + 
                t.getSecond().substring(1,t.getSecond().length()-1) + 
                " " + t.getFirst().substring(1,t.getFirst().length()-1) + ")");
            }
            printWriter.println("  )");
            printWriter.println(")");
        }
    }
    
    
    private static int getNumberOfTests(String testcases){
       JScheme js = new JScheme();
       js.load(testcases); 
       return Integer.parseInt(js.eval("(length testcases)").toString());
    }
    
    private static List<Tuple2<String,String>> getProcessed(String program, String testcases){
        final List<Tuple2<String,String>> result = new ArrayList<>();
        JScheme js = new JScheme();
        js.load(testcases);
        js.load(program);
        boolean proc = testcases.contains("  `(");
        int numberOfTests = Integer.parseInt(js.eval("(length testcases)").toString());
        String start = "(list (apply prog (car (list-ref testcases ";
        String end = "))))";
        for (int i=0; i<numberOfTests; i++){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            final int index = i;
            Future future = executor.submit(new Thread(){public void run(){
                String s = processTest(js,index);
                if(s!=null) {
                    String input = js.eval("(list (car (list-ref testcases " + index + ")))").toString();
                    if (proc)
                        if (inputAProcedure(testcases, input))
                            input = "(," + input.substring(1,input.length());
                    
                    result.add(Tuple2.cons(s,input));
                }}});
            try { 
                future.get(maxSeconds, TimeUnit.SECONDS); 
            }
            catch (Exception ex) {
                System.out.print("to");
            } 
            if (!executor.isTerminated())
                executor.shutdownNow(); 
        }
        return result;
    }
    private static boolean inputAProcedure(String testcases, String input){
        System.out.println(input);
        return testcases.contains( "(," + input.substring(1,input.length()));
    }
    
    private static String processTest(JScheme js, int testNumber){
        try {
          return (js.eval("(list (apply prog (car (list-ref testcases " + testNumber + "))))")).toString();
        } catch (Exception e) {
          return null; // problem with program processing the inputs/running
        }
    }

}
