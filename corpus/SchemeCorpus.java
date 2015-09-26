package corpus;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

import jeep.tuple.Tuple2;

/**
 * Class manages a corpus of Scheme programs
 */
@Preamble(
author = "Jonathan Fieldsend",
date = "16/09/2015",
lastModified = "19/09/2015"
)
public class SchemeCorpus implements Corpus
{
    private static Random randomGenerator = new Random();
    private List<Program> programList = new ArrayList<>();
    private Map<Expr,List<Expr>> fragmentMap = new HashMap<>();
    private final int DEPTH;
    private final boolean SMOOTH;
    private final double SMOOTHING;

    SchemeCorpus(int depth, double smoothing){
        this.DEPTH = depth;
        this.SMOOTHING = smoothing;
        SMOOTH = (smoothing >0.0);
    }

    @Override
    public Map<Expr,List<Expr>> getFragmentMap(){
        return fragmentMap;
    }

    @Override
    public void addToCorpus(Program program){
        fragment(program.getProgramAsExpression());
        programList.add(program);
    }

    @Override
    public Expr generateRandomExpression(){
        return randomSolution();
    }

    @Override
    public double probabilityOfProgram(Program program){
        Expr expression = program.getProgramAsExpression();
        return (SMOOTH) ?
        generateSmoothLogProbabilityOfProgram(wrap(expression,DEPTH)) :
        generateLogProbabilityOfProgram(wrap(expression,DEPTH));     
    }

    @Override
    public Expr mutate(Expr expression) {
        return generateMutation(expression);
    }

    private Expr generateMutation(Expr expression) {
        if (randomGenerator.nextDouble() < 0.3) 
            return generateExpand(extract(expression,DEPTH));
        else {
            if (expression instanceof Expr.ExprList){
                Expr.ExprList mutated = Expr.list(((Expr.ExprList) expression).get(0));
                for (int i=1; i<((Expr.ExprList) expression).size(); i++) {
                    mutated.add(Expr.list(((Expr.ExprList) expression).get(i))); 
                }
                return mutated;
            }
            return expression;
        } 
    }

    private double generateLogProbabilityOfProgram(Expr expression){
        Expr fragHead = extract(expression, DEPTH); 
        Expr fragTail = extract(expression, DEPTH+1);

        if (fragHead.equals(fragTail)){
            return 0.0;
        }
        double prob;    

        if (fragmentMap.containsKey(fragHead)){
            prob = ((double) Collections.frequency(fragmentMap.get(fragHead),fragTail))/fragmentMap.get(fragHead).size();
            prob = safeLog(1.0*prob);
        } else {  
            return safeLog(0.0);
        }    
        if (expression instanceof Expr.ExprList) 
            for (int i=1; i<((Expr.ExprList) expression).size(); i++) 
                prob += generateLogProbabilityOfProgram(((Expr.ExprList) expression).get(i));

        return prob;    
    }   

    private static double safeLog(double value) {
        return (value <= 0.0) ? Double.NEGATIVE_INFINITY : Math.log10(value);
    }

    private void fragment(Expr expression) {
        if (SMOOTH){
            for (int i = 0; i<=DEPTH; i++)
                generateFragment(wrap(expression,i),i);
        } else {
            generateFragment(wrap(expression,DEPTH),DEPTH);
        }
        // QUERY UNIFORM in python?
        
    }

    private void generateFragment(Expr expression,int depth){
        Expr fragHead = extract(expression, depth); 
        Expr fragTail = extract(expression, depth+1);

        // if head and tail are not the same, so not at bottom
        if (fragHead.equals(fragTail)==false) {
            addFragment(fragHead, fragTail);
        }
        if (expression instanceof Expr.ExprList){
            Expr.ExprList temp = (Expr.ExprList) expression;
            for (int i=1; i< temp.size(); i++){
                generateFragment(temp.get(i), depth);
            }
        }

    }

    private void addFragment(Expr fragHead, Expr fragTail){
        if (fragmentMap.containsKey(fragHead))
            fragmentMap.get(fragHead).add(fragTail);
        else {
            System.out.println(fragHead);
            List<Expr> temp = new ArrayList<>();
            temp.add(fragTail);
            fragmentMap.put(fragHead,temp);
        }
    }

    private Expr extract(Expr expression, int depth) {
        if (depth == 0)
            return Expr.atom("_");
        if (expression instanceof Expr.ExprList){
            Expr.ExprList temp;    
            if (depth == 1) {
                temp = Expr.list(((Expr.ExprList) expression).get(0));
                for (int i=1; i<((Expr.ExprList) expression).size(); i++)
                    temp.add(Expr.atom("_"));
                return temp;
            } else {
                temp = Expr.list(extract(((Expr.ExprList) expression).get(0),depth-1)); 
                for (int i=1; i<((Expr.ExprList) expression).size(); i++)
                    temp.add(extract(((Expr.ExprList) expression).get(i),depth-1));
            }
            return temp;
        }
        return expression; //expression not in list
    }

    private Expr randomSolution(){  
        return unwrap(generateExpand(wrap(Expr.list(Expr.atom("_")),DEPTH)),DEPTH);
    }

    /*
     * Given a fragment argment, method generates a complete tree (i.e. without
     * Expr.Atoms containing "_") by growing it randomly from the initial fragment
     * using the tree extension rules in the map of fragments to extended fragments
     */
    private Expr generateExpand(Expr fragment) {
        if (fragmentMap.containsKey(fragment)==false)
            return fragment; // if fragment doesn't exist in map keys, return argument

        Expr extendedFragment = getRandomListMember(fragmentMap.get(fragment));
        return atomOrListOfFirstFollowedByFunctionOnRest(extendedFragment, p -> {return generateExpand(p);});
    }

    private Expr getRandomListMember(List<Expr> list){
        return list.get(randomGenerator.nextInt(list.size()));   
    }

    /*
     * If argument is not of type Expr.ExprList, returns argument, otherwise returns the
     * Expr.ExprList containing the first element of the argment, with all subsequent 
     * elements expanded with the expander argument's expand method
     */
    private static Expr atomOrListOfFirstFollowedByFunctionOnRest(Expr fragment, Expander expander){
        if (fragment instanceof Expr.ExprList){ 
            Expr.ExprList list = Expr.list(((Expr.ExprList) fragment).get(0));
            for (int i=1; i<((Expr.ExprList) fragment).size(); i++) {
                list.add(expander.expand(((Expr.ExprList) fragment).get(i)));
            }
            return list;
        }
        return fragment;
    }

    private Expr extendFragment(Expr fragment){
        if (((fragmentMap.containsKey(fragment)) && (randomGenerator.nextDouble() < SMOOTHING)) || ((fragment instanceof Expr.ExprList)==false))
            return getRandomListMember(fragmentMap.get(fragment));
        return atomOrListOfFirstFollowedByFunctionOnRest(fragment, p -> {return extendFragment(p);});
    }

    private Expr generateSmoothExpand(Expr fragment){
        Expr extendedFragment = extendFragment(fragment);
        return atomOrListOfFirstFollowedByFunctionOnRest(fragment, p -> {return generateSmoothExpand(p);});
    }

    private double probabilityOfExtendingFragment(Expr fragHead, Expr fragTail){
        if (fragHead.equals(fragTail))
            return 1.0;
        
        // what is the probability of generating a tail with lower order fragments of the
        // head if thisoption is taken
        double probLower =1.0;
        if (fragHead instanceof Expr.ExprList){
            Expr.ExprList tempHead = (Expr.ExprList) fragHead;
            Expr.ExprList tempTail = (Expr.ExprList) fragTail;
            for (int i=1; i<tempHead.size(); i++)
                probLower *= probabilityOfExtendingFragment(tempHead.get(i),tempTail.get(i));
            
        } else 
            probLower = 0.0;
        // is there a rule that matches the head?
        if (fragmentMap.keySet().contains(fragHead)){
            // if yes what is the probability of the tail given teh whole head?
            double probWhole = 1.0 * ((double) Collections.frequency(fragmentMap.get(fragHead),fragTail))/fragmentMap.get(fragHead).size();
            return SMOOTHING*probWhole + (1.0-SMOOTHING)*probLower;
        }
        return probLower;    
    }
   
 
    private double generateSmoothLogProbabilityOfProgram(Expr fragment) {
        Expr fragHead = extract(fragment,DEPTH);
        Expr fragTail = extract(fragment,DEPTH+1);
        
        if (fragHead.equals(fragTail))
            return 0.0;
        
        double logProb = safeLog(probabilityOfExtendingFragment(fragHead,fragTail));
        if (logProb == safeLog(0.0))
            return logProb;
        
        if (fragment instanceof Expr.ExprList){
            Expr.ExprList list = (Expr.ExprList) fragment;
            for (int i=1; i<list.size(); i++) 
                logProb += generateSmoothLogProbabilityOfProgram(list.get(i));
        }
        return logProb;
    }
    
    /*
     * number of levels in a tree
     */
    private int depth(Expr expression) {
        if (expression instanceof Expr.ExprList) {
            Expr.ExprList temp = (Expr.ExprList) expression;
            if (temp.size()==1)
                return 1;
            int maxDepth = 0;
            for (int i=1; i<temp.size(); i++)
                maxDepth = Math.max(maxDepth, 1+ depth(temp.get(i)));
            return maxDepth;
        }
        return 1;
    }
    
    /*
     * number of nodes in a tree
     */
    private int size(Expr expression) {
        if (expression instanceof Expr.ExprList) {
            Expr.ExprList temp = (Expr.ExprList) expression;
            if (temp.size()==1)
                return 1;
            int size = 1;
            for (int i=1; i<temp.size(); i++)
                size += size(temp.get(i));
            return size;
        }
        return 1;
    }
    /*
     * missing perplexity methods
     * 
     */
    private double averageDepth(){
        double average = 0.0;
        for (Program p : programList)
            average += depth(p.getProgramAsExpression());
        return average;
    }
    
    private double averageSize(){
        double average = 0.0;
        for (Program p : programList)
            average += size(p.getProgramAsExpression());
        return average;
    }
    
    private double modelSize(){
        return fragmentMap.size();
    }
    /*
     * Missing vocabulary methods
     */
    
    private Expr wrap(Expr expression, int depth){
        Expr wrappedList = expression;
        for (int i=0; i<depth; i++){
            wrappedList = Expr.list(Expr.atom("start"), wrappedList);
        }
        return expression;
    }
    
    private Expr unwrap(Expr expression, int depth){
        for (int i=0; i<depth; i++){
            expression = ((Expr.ExprList) expression).get(1);
        }
        return expression;
    }
    
    private Expr expand(Expr fragment) {
        return (SMOOTH) ? 
        generateSmoothExpand(fragment) :
        generateExpand(fragment);
    }
    
    @FunctionalInterface
    private interface Expander{
        Expr expand(Expr toExpand);
    }
}
