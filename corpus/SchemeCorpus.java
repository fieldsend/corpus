package corpus;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

@Preamble(
    author = "Jonathan Fieldsend",
    date = "16/09/2015"
)
public class SchemeCorpus implements Corpus<String>
{
    private static Random randomGenerator = new Random();
    
    private Map<AbstractSyntaxTree<String>,List<AbstractSyntaxTree<String>>> fragmentMap = new HashMap<>();
    private TreeCreator<String> treeCreator;
    private int depth;
    
    SchemeCorpus(int depth, TreeCreator<String> treeCreator){
        this.depth = depth;
        this.treeCreator = treeCreator;
    }
    
    @Override
    public Map<AbstractSyntaxTree<String>,List<AbstractSyntaxTree<String>>> getFragmentMap(){
        return fragmentMap;
    }
    
    @Override
    public void addToCorpus(Program<String> program){
        fragment(program.getProgramAsTree());
    }
    
    @Override
    public AbstractSyntaxTree<String> generateRandomTree(){
        return randomSolution();
    }
    
    @Override
    public double probabilityOfProgram(Program<String> program){
        AbstractSyntaxTree<String> tree = program.getProgramAsTree();
        if (depth == 0) // unigrams
            return generateProbabilityOfProgram(tree);
        if (depth == 1){ // bigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(tree);
            return generateProbabilityOfProgram(temp);
        }
        if (depth == 3) { //trigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(treeCreator.factory("start"));
            temp.getSubtrees().get(0).addChild(tree);
            return generateProbabilityOfProgram(temp);
        }
        return -1.0;
    }
    
    
    
    private double generateProbabilityOfProgram(AbstractSyntaxTree<String> tree){
        AbstractSyntaxTree<String> fragHead = extract(tree, depth); 
        AbstractSyntaxTree<String> fragTail = extract(tree, depth+1);
        
        if (fragHead.equals(fragTail))
            return 1.0;
        double prob;    
        if (fragmentMap.containsKey(fragHead))
            prob = ((double) Collections.frequency(fragmentMap.get(fragHead),fragTail))/fragmentMap.get(fragHead).size();
        else     
            return 0.0;
            
        for (AbstractSyntaxTree<String> a : tree.getSubtrees())
            prob *= generateProbabilityOfProgram(a);
        
        return prob;    
    }   
    
    private void fragment(AbstractSyntaxTree<String> tree) {
        if (depth == 0) // unigrams
            generateFragment(tree);
        if (depth == 1){ // bigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(tree);
            generateFragment(temp);
        }
        if (depth == 3) { //trigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(treeCreator.factory("start"));
            temp.getSubtrees().get(0).addChild(tree);
            generateFragment(temp);
        }
    }
    
    private void generateFragment(AbstractSyntaxTree<String> tree){
        AbstractSyntaxTree<String> fragHead = extract(tree, depth); 
        AbstractSyntaxTree<String> fragTail = extract(tree, depth+1);
        
        // if head and tail are not the same, so not at bottom
        if (fragHead.equals(fragTail)==false) {
            if (fragmentMap.containsKey(fragHead))
                fragmentMap.get(fragHead).add(fragTail);
            else {
                List<AbstractSyntaxTree<String>> temp = new ArrayList<>();
                temp.add(fragTail);
                fragmentMap.put(fragHead,temp);
            }
        }
        
        for (AbstractSyntaxTree<String> a : tree.getSubtrees())
            generateFragment(a);
    }
    
    private AbstractSyntaxTree<String> extract(AbstractSyntaxTree<String> tree, int depth) {
        if (depth == 0)
            return new AST<String>("_");
        AbstractSyntaxTree<String> temp = treeCreator.factory(tree.getContents());   
        if (depth == 1){
            for (int i=0; i< tree.getSubtrees().size(); i++)
                temp.addChild(treeCreator.factory("_"));
        } else {
            for (AbstractSyntaxTree<String> s : tree.getSubtrees())
                 temp.addChild(extract(s, depth-1));
        }
        return temp;
    }
    
    private AbstractSyntaxTree<String> randomSolution(){
        if (depth == 0) // unigrams
            return generateExpand(treeCreator.factory("_"));
        if (depth == 1){ // bigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(treeCreator.factory("_"));
            return generateExpand(temp);
        }
        if (depth == 3) { //trigrams
            AbstractSyntaxTree<String> temp = treeCreator.factory("start");
            temp.addChild(treeCreator.factory("start"));
            temp.getSubtrees().get(0).addChild(treeCreator.factory("_"));
            return generateExpand(temp);
        }
        return null;
    }
    
    private AbstractSyntaxTree<String> generateExpand(AbstractSyntaxTree<String> fragment) {
        if (fragmentMap.containsKey(fragment)==false)
            return fragment;
        
        List<AbstractSyntaxTree<String>> temp = fragmentMap.get(fragment);    
        AbstractSyntaxTree<String> extendedFragment =  treeCreator.deepCopyFactory(temp.get(randomGenerator.nextInt(temp.size())));   
        
        List<AbstractSyntaxTree<String>> children = extendedFragment.getSubtrees();
        for (int i=0; i<children.size(); i++) {
            children.set(i,generateExpand(children.get(i))); 
        }
        return extendedFragment;
    }
    
}
