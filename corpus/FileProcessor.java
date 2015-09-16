package corpus;


import java.io.IOException;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.util.List;
import java.util.ArrayList;

/**
 * Class provides utility to obtain list of files confirming to
 * extension of programming language required for corpus 
 */ 
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
class FileProcessor extends SimpleFileVisitor<Path>
{
    private List<Path> files = new ArrayList<>();
    private String fileExtension;
    private FileProcessor(String directoryName, String fileExtension) 
    throws IOException {
        this.fileExtension = fileExtension;
        Files.walkFileTree(Paths.get(directoryName), this);
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        System.out.println("Processing file:" + file);
        if (file.toString().regionMatches(true,file.toString().length()-fileExtension.length(), fileExtension, 0, fileExtension.length()))
            files.add(file);
        return FileVisitResult.CONTINUE;
    }

    // Print each directory visited in turn.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) 
    throws IOException {
      System.out.println("Processing directory:" + dir);
      return FileVisitResult.CONTINUE;
    }
        
    // Print exception message if encountered
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) {
        System.err.println(e);
        return FileVisitResult.CONTINUE;
    }
    
    /**
     * Method returns the list of paths to files in directoryName and any of its
     * subdirectories, which end with the specified file extension
     * 
     * @param directoryName name of root directory to search in
     * @param fileExtension extension files must have to be returned in the list
     * @return list of paths to files
     */ 
    static List<Path> getList(String directoryName, String fileExtension) 
    throws IOException {
        FileProcessor temp = new FileProcessor(directoryName, fileExtension);
        return temp.files;
    }
}
