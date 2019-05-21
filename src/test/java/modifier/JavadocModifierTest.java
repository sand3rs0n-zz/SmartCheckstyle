package modifier;

import checkers.JavadocChecker;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import models.Issue;
import modifiers.JavadocModifier;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;

public class JavadocModifierTest {
    
    @Test
    public void testRemoveReturnTagMissingJavadocs() throws FileNotFoundException {
        
        ClassLoader classLoader = this.getClass().getClassLoader();
        String fileName = classLoader.getResource("checkers/DocumentChecker1.java").getFile();
        CompilationUnit cu = JavaParser.parse(new File(fileName));
        JavadocModifier jdr = new JavadocModifier(fileName);
        List<Issue> issues = new ArrayList<>();
        jdr.visit(cu, issues);
        int lineNumber = issues.get(0).getLineNumber();
        
        // now we check if the tree contains tag that doesn't have document.
        List<Issue> foundedIssues = new ArrayList<>();
        JavadocChecker jdc = new JavadocChecker(fileName);
        jdc.visit(cu, foundedIssues);
        
        boolean hasTagMissingJavadoc = false;
        for (Issue issue: foundedIssues) {
            
            if (issue.getLineNumber() == lineNumber) {
                if (issue.getErrMessage().contains("description")) {
                    hasTagMissingJavadoc = true;
                    break;
                }
            }
        }
        assertFalse("Failed to remove block tag node missing Javadoc",
                    hasTagMissingJavadoc);
        
    }
}
