package corpus;


@Preamble(
    author = "Jonathan Fieldsend",
    date = "16/09/2015"
)
public interface CorpusCreator<T>
{
     /**
     * Will provide a Corpus instance with the depth set as the argument. If a 
     * negative value is added, depth will be set as 0.
     * 
     * @param depth depth to be used in generating fragments
     * @return a Corpus instance
     */
    Corpus<T> factory(int depth, TreeCreator<T> treeCreator);
    
}
