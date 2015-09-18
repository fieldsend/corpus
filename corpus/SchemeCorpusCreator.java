package corpus;


@Preamble(
    author = "Jonathan Fieldsend",
    date = "18/09/2015",
    lastModified = "19/09/2015"
)
public class SchemeCorpusCreator implements CorpusCreator<String>
{
    @Override
    public Corpus<String> factory(int depth, TreeCreator<String> treeCreator){
        return new SchemeCorpus(depth,treeCreator);
    }
}
