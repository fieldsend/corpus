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
    
    private Map<Expr,List<Expr>> fragmentMap = new HashMap<>();
    private int depth;
    
    SchemeCorpus(int depth){
        this.depth = depth;
    }
    
    @Override
    public Map<Expr,List<Expr>> getFragmentMap(){
        return fragmentMap;
    }
    
    @Override
    public void addToCorpus(Program program){
        fragment(program.getProgramAsExpression());
    }
    
    @Override
    public Expr generateRandomExpression(){
        return randomSolution();
    }
    
    @Override
    public double probabilityOfProgram(Program program){
        Expr expression = program.getProgramAsExpression();
        if (depth == 0) // unigrams
            return generateLogProbabilityOfProgram(expression);
        if (depth == 1){ // bigrams
            return generateLogProbabilityOfProgram(Expr.list(Expr.atom("start"), expression));
        }
        if (depth == 3) { //trigrams
            return generateLogProbabilityOfProgram(Expr.list(Expr.atom("start"), Expr.atom("start"), expression));
        }
        return -1.0;
    }
    
    @Override
    public Expr mutate(Expr expression) {
        return generateMutation(expression);
    }
    
    private Expr generateMutation(Expr expression) {
        if (randomGenerator.nextDouble() < 0.3) 
            return generateExpand(extract(expression,depth));
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
        Expr fragHead = extract(expression, depth); 
        Expr fragTail = extract(expression, depth+1);
        
        if (fragHead.equals(fragTail))
            return 0.0;
        double prob;    
        if (fragmentMap.containsKey(fragHead)){
            prob = ((double) Collections.frequency(fragmentMap.get(fragHead),fragTail))/fragmentMap.get(fragHead).size();
            prob = Math.log(1.0*prob);
        } else {     
            return Math.log(0.0);
        }    
        if (expression instanceof Expr.ExprList) 
            for (int i=1; i<((Expr.ExprList) expression).size(); i++) 
                prob += generateLogProbabilityOfProgram(((Expr.ExprList) expression).get(i));
        
        return prob;    
    }   
    
    private void fragment(Expr expression) {
        if (depth == 0) // unigrams
            generateFragment(expression);
        if (depth == 1){ // bigrams
            generateFragment(Expr.list(Expr.atom("start"), expression));
        }
        if (depth == 3) { //trigrams
            generateFragment(Expr.list(Expr.atom("start"), Expr.atom("start"), expression));
        }
    }
    
    private void generateFragment(Expr expression){
        Expr fragHead = extract(expression, depth); 
        Expr fragTail = extract(expression, depth+1);
        
        // if head and tail are not the same, so not at bottom
        if (fragHead.equals(fragTail)==false) {
            if (fragmentMap.containsKey(fragHead))
                fragmentMap.get(fragHead).add(fragTail);
            else {
                List<Expr> temp = new ArrayList<>();
                temp.add(fragTail);
                fragmentMap.put(fragHead,temp);
            }
        }
        if (expression instanceof Expr.ExprList){
            Expr.ExprList temp = (Expr.ExprList) expression;
            for (int i=1; i< temp.size(); i++){
                generateFragment(temp.get(i));
            }
        }
        
    }
    
    private Expr extract(Expr expression, int depth) {
        if ((depth == 0) || (expression instanceof Expr.Atom))
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
        return expression;
    }
    
    private Expr randomSolution(){
        if (depth == 0) // unigrams
            return generateExpand(Expr.list(Expr.atom("_")));
        if (depth == 1){ // bigrams
            return generateExpand(Expr.list(Expr.atom("start"), Expr.atom("_")));
        }
        if (depth == 3) { //trigrams
            return generateExpand(Expr.list(Expr.atom("start"), Expr.atom("start"),Expr.atom("_")));
        }
        return null;
    }
    
    private Expr generateExpand(Expr fragment) {
        if (fragmentMap.containsKey(fragment)==false)
            return fragment;
        
        List<Expr> temp = fragmentMap.get(fragment);    
        Expr extendedFragment =  temp.get(randomGenerator.nextInt(temp.size()));   
        if (fragment instanceof Expr.ExprList){
            Expr.ExprList expanded = Expr.list(((Expr.ExprList) extendedFragment).get(0));
            for (int i=1; i<((Expr.ExprList) extendedFragment).size(); i++) {
                expanded.add(generateExpand(((Expr.ExprList) extendedFragment).get(1)));
            }
            return expanded;
        } else {
            return extendedFragment;
        }
    }
    
}
