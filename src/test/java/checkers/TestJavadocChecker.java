package checkers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;
import utils.JavadocRemover;

import java.io.File;
import java.io.FileNotFoundException;

public class TestJavadocChecker{
    
    @Test
    public void testNoJavaDocInPublicClass() throws FileNotFoundException {
        CompilationUnit cu;
        cu = JavaParser.parse(new File("/Users/haujd98/Documents/UW/csep590_sp19/SmartCheckstyle/source_to_parse/checkers/DocumentChecker1.java"));
        JavadocRemover jdc = new JavadocRemover();
        jdc.visit(cu, null);
        
    }
}
