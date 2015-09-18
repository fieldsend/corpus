package corpus;
import java.lang.annotation.Documented;

/**
 * Preamble information for classes in package
 */
@Preamble(
    author = "Jonathan Fieldsend",
    date = "15/09/2015",
    lastModified = "16/09/2015"
)
@Documented
@interface Preamble
{
   String author();
   String date();
   int currentRevision() default 1;
   String lastModified() default "N/A";
   String lastModifiedBy() default "N/A";
}
