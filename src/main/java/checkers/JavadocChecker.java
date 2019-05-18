package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import models.Issue;

import java.util.*;


public class JavadocChecker extends VoidVisitorAdapter<List<Issue>> {

    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "JAVADOC";
    
    public JavadocChecker(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void visit(CompilationUnit n, List<Issue> issues) {
        if (n.getPackageDeclaration().isPresent()) {
            this.packageName = n.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }
        super.visit(n, issues);
        List<Comment> comments = n.getAllContainedComments();
        for (Comment comment: comments) {
            if (!comment.getCommentedNode().isPresent()) {
                int lineNumber = comment.getRange().get().begin.line;
                issues.add(generateIssue(lineNumber, "Orphant comment found."));
            }
        }

    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<Issue> issues) {
        super.visit(n, issues);
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()) {
                int lineNumber = n.getRange().get().begin.line;
                issues.add(generateIssue(lineNumber, n.getNameAsString()
                        + " is missing javadoc."));
            } else {
                Optional<Javadoc> jd = n.getJavadoc();
            }
        }
    }

    @Override
    public void visit(MethodDeclaration n, List<Issue> issues) {

        super.visit(n, issues);
        if (n.isPrivate())
            return;

        int lineNumber = n.getRange().get().begin.line;
        String methodName = n.getNameAsString();
        if (!n.hasJavaDocComment()) {
            issues.add(generateIssue(lineNumber, "Non-private method, "
                    + methodName + ", is missing javadoc."));
            
        } else {
            
            if (n.getJavadoc().get().getDescription().isEmpty()) {
                issues.add(generateIssue(lineNumber, "Non-private method, "
                        + methodName + ", is missing javadoc."));
            }

            if (n.getJavadoc().get().getBlockTags().size() < n.getParameters().size()) {
                issues.add(generateIssue(lineNumber, "Non-private method, "
                        + methodName + ", is missing tags."));
                
            } else if (n.getJavadoc().get().getBlockTags().size() > n.getParameters().size()) {
                issues.add(generateIssue(lineNumber, "Non-private method, "
                        + methodName + ", has unnecessary tag(s)."));
                
            } else {
                List<JavadocBlockTag> blockTags = n.getJavadoc().get().getBlockTags();
                HashSet<String> tagSet = new HashSet<>();
                for (JavadocBlockTag blockTag: blockTags) {
                    try {
                        blockTag.getName().ifPresent(name -> tagSet.add(name));
                    } catch (NoSuchElementException e) {
                        System.out.println(fileName + " " + lineNumber);
                        throw e;
                    }
                    
                }

                if (tagSet.size() > 0) {
                    issues.add(generateIssue(lineNumber, "Non-private method, "
                            + methodName + ", has unmatched tag(s)."));
                }
            }

            for (JavadocBlockTag blockTag : n.getJavadoc().get().getBlockTags()) {
                if (blockTag.getContent().isEmpty()) {
                    if (blockTag.getType() == JavadocBlockTag.Type.PARAM
                        || blockTag.getType() == JavadocBlockTag.Type.RETURN
                        || blockTag.getType() == JavadocBlockTag.Type.THROWS
                        || blockTag.getType() == JavadocBlockTag.Type.DEPRECATED) {
                        issues.add(generateIssue(lineNumber, "block tag "
                                + blockTag.getName().toString() + " is missing description."));
                    }
                }
            }

        }

    }

    @Override
    public void visit(FieldDeclaration n, List<Issue> issues) {
        super.visit(n, issues);
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()) {
                int lineNumber = n.getRange().get().begin.line;
                String fieldName = n.getMetaModel().getMetaModelFieldName();
                issues.add(generateIssue(lineNumber, "Public field "
                        + fieldName + " is missing document."));
            }
        }
    }
    
    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }

}
