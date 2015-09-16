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

@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015"
)
class FileProcessor extends SimpleFileVisitor<Path>
{
    private List<Path> files = new ArrayList<>();
    private String fileExtension;
    FileProcessor(String directoryName, String fileExtension) 
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
    
    List<Path> getList() {
        return files;
    }
}
