package corpus;


/**
 * Write a description of class SchemeCorpusCreator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SchemeCorpusCreator implements CorpusCreator<String>
{
    @Override
    public Corpus<String> factory(int depth){
        return new SchemeCorpus(depth);
    }
}
