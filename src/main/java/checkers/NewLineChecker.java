package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.Issue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewLineChecker extends VoidVisitorAdapter<List<Issue>> {
    private String packageName;
    private String fileName;
    private String ISSUE_TYPE = "NEWLINE";

    public NewLineChecker(String fileName) {
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

        Set<Integer> importLines = new HashSet<>();
        NodeList<ImportDeclaration> imports = n.getImports();
        for (ImportDeclaration id : imports) {
            int line = id.getRange().get().begin.line;
            if (importLines.contains(line)) {
                issues.add (generateIssue(line,"] Place import " + id.getNameAsString() + " in a new line."));
            }
            else {
                importLines.add(line);
            }
        }
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}