package modifiers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import models.Issue;

import java.util.List;

public class ImportModifier {

    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "IMPORT_MODIFIER";

    public ImportModifier(String fileName) {
        this.fileName = fileName;
    }

    public void visit(CompilationUnit cu, List<Issue> issues) {
        if (cu.getPackageDeclaration().isPresent()) {
            this.packageName = cu.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }

        NodeList<ImportDeclaration> imports = cu.getImports();
        for (Issue i : issues) {
            if (i.getErrMessage().contains("asterisk")) {
                continue;
            }

            int importLineNumber = i.getLineNumber();
            for (int j = 0; j < imports.size(); j++) {
                if (imports.get(j).getRange().get().begin.line == importLineNumber) {
                    imports.remove(j);
                }
            }
        }

        cu.setImports(imports);
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
