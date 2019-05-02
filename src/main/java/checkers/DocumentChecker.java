package checkers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;

import java.util.Optional;


public class DocumentChecker extends VoidVisitorAdapter {

    public void visit(ClassOrInterfaceDeclaration n, Object arg){
        super.visit(n, arg);
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()){
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] public class " + n.getNameAsString() + " is missing document.");
            } else {
                Optional<Javadoc> jd = n.getJavadoc();
                jd.toString();
            }
        }
    }

    public void visit(MethodDeclaration n, Object arg){
        super.visit(n, arg);
        if (!n.isPrivate()) {
            if (!n.hasJavaDocComment()){
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] non-private method " + n.getNameAsString() + " is missing document.");
            } else {
                if (n.getJavadoc().get().getDescription().isEmpty()){
                    int line = n.getRange().get().begin.line;
                    System.out.println("[Line: " + line + "] non-private method " + n.getNameAsString() + " is missing description.");
                }
            }
        }
    }

    public void visit(FieldDeclaration n, Object arg) {
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()){
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] " + n.getModifiers().toString() + " is missing document.");
            }
        }
    }

}
