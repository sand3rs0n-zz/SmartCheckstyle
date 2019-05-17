package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import models.Issue;

import java.util.List;
import java.util.Optional;

public class WhitespaceChecker extends VoidVisitorAdapter<List<Issue>> {
    private String packageName;
    private String fileName;
    private String issueType = "WHITESPACE";

    public WhitespaceChecker(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void visit(CompilationUnit n, List<Issue> issues) {
        if (n.getPackageDeclaration().isPresent()) {
            this.packageName = n.getPackageDeclaration().toString();
        } else {
            this.packageName = "N/A";
        }
        super.visit(n, issues);
        /*
        List<Comment> comments = n.getAllContainedComments();
        for (Comment comment: comments) {
            if (!comment.getCommentedNode().isPresent()) {
                int lineNumber = comment.getRange().get().begin.line;
                String errMessage = "Orphant comment found.";
                Issue issue = new Issue(this.packageName, this.fileName, lineNumber,
                        this.issueType, errMessage);
                System.out.println(issue);
                issues.add(issue);
            }

        }
        */
    }

    @Override
    public void visit(IfStmt n, List<Issue> issues) {
        super.visit(n, issues);
        String ifBlock = n.getParentNode().toString();
        int i=0;
        boolean ifFound = false;
        for (; i < ifBlock.length() - 2; i++) {
            String s = ifBlock.substring(i, i+2);
            if ( s.equals("if")) {
                ifFound = true;
                i += 2;
                break;
            }
        }

        int numWhiteSpaces = 0;
        if (ifFound) {
            while (i < ifBlock.length() && ifBlock.charAt(i) == ' ') {
                numWhiteSpaces++;
                i++;
            }
        }

        if (ifFound && numWhiteSpaces == 0) {
            int line = n.getRange().get().begin.line;
            System.out.println("[Line: " + line + "] if statement is missing whitespace.");
        } else if (numWhiteSpaces > 1) {
            int line = n.getRange().get().begin.line;
            System.out.println("[Line: " + line + "] if statement has multiple whitespace.");
        }
        /*
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()) {
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] public class "
                        + n.getNameAsString() + " is missing document.");
            } else {
                Optional<Javadoc> jd = n.getJavadoc();
                jd.toString();
            }
        }

         */
    }
}
