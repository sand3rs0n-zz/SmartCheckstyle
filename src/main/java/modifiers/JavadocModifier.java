package modifiers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import models.Issue;

import java.util.List;

public class JavadocModifier extends ModifierVisitor<List<Issue>> {
    
    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "JAVADOC_MODIFIER";
    
    public JavadocModifier(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public Visitable visit(CompilationUnit n, List<Issue> issues) {
        if (n.getPackageDeclaration().isPresent()) {
            this.packageName = n.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }
        super.visit(n, issues);
        return n;
    }
    
    @Override
    public Visitable visit(MethodDeclaration n, List<Issue> issues) {
        int lineNumber = n.getRange().get().begin.line;
        String methodName = n.getNameAsString();
        
        if (!n.getJavadoc().isPresent())
            return super.visit(n, issues);
    
        List<JavadocBlockTag> blockTags = n.getJavadoc().get().getBlockTags();
        blockTags.removeIf(tag -> {
            boolean missingJavadoc = (tag.getContent().isEmpty()
                    && (tag.getType() == JavadocBlockTag.Type.PARAM
                    || tag.getType() == JavadocBlockTag.Type.RETURN
                    || tag.getType() == JavadocBlockTag.Type.THROWS
                    || tag.getType() == JavadocBlockTag.Type.DEPRECATED));
            if (missingJavadoc) {
                issues.add(generateIssue(lineNumber, methodName
                        + " Removed tag with no javadoc"));
                
            }
            return missingJavadoc;
        });
        
        JavadocDescription content = n.getJavadoc().get().getDescription();
        Javadoc javadoc = new Javadoc(content);
        for (JavadocBlockTag blocktag: blockTags) {
            javadoc.addBlockTag(blocktag);
        }
        n.setJavadocComment("    ", javadoc);
        
        return super.visit(n, issues);
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
