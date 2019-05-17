package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.Issue;

import java.util.List;

public class DeclarationChecker extends VoidVisitorAdapter<List<Issue>> {

    private String packageName;
    private String fileName;
    private static final String ISSUE_TYPE = "DECLARATION";

    public DeclarationChecker(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void visit(CompilationUnit n, List<Issue> arg) {
        if (n.getPackageDeclaration().isPresent()) {
            this.packageName = n.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<Issue> issues) {
        super.visit(n, issues);
        String className = n.getNameAsString();
        int line = n.getRange().get().begin.line;
        if (andCheck(className)) {
            issues.add(generateIssue(line,"Class name "
                    + className + " contains the word 'and'. "
                    + "Consider splitting this into two classes"));
        }
        if (orCheck(className)) {
            issues.add(generateIssue(line,"Class name "
                    + className + " contains the word 'or'. "
                    + "Consider splitting this into two classes"));
        }
    }

    @Override
    public void visit(MethodDeclaration n, List<Issue> issues) {
        super.visit(n, issues);
        String methodName = n.getNameAsString();
        int line = n.getRange().get().begin.line;
        if (andCheck(methodName)) {
            issues.add(generateIssue(line,"Method name "
                    + methodName + " contains the word 'and'. "
                    + "Consider splitting this into two methods"));
        }
        if (orCheck(methodName)) {
            issues.add(generateIssue(line,"Method name "
                    + methodName + " contains the word 'or'. "
                    + "Consider splitting this into two methods"));
        }
    }

    @Override
    public void visit(FieldDeclaration n, List<Issue> issues) {
        super.visit(n, issues);
        String fieldName = n.getVariables().get(0).getName().asString();
        int line = n.getRange().get().begin.line;
        if (andCheck(fieldName)) {
            issues.add(generateIssue(line,"Field name "
                    + fieldName + " contains the word 'and'. "
                    + "Consider splitting this into two fields"));
        }
        if (orCheck(fieldName)) {
            issues.add(generateIssue(line,"Field name "
                    + fieldName + " contains the word 'or'. "
                    + "Consider splitting this into two fields"));
        }
    }

    private boolean andCheck(final String name) {
        return !name.isEmpty() && name.toLowerCase().contains("and")
                && !name.toLowerCase().startsWith("and") && !name.toLowerCase().endsWith("and");
    }

    private boolean orCheck(final String name) {
        return !name.isEmpty() && name.toLowerCase().contains("or")
                && !name.toLowerCase().startsWith("or") && !name.toLowerCase().endsWith("or");
    }

    private Issue generateIssue(final int lineNumber, final String error) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, error);
    }

}