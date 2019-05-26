package utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;


public class JavadocRemover extends ModifierVisitor {
    
    private int removedDocsTotal = 0;
    
    public int getRemovedDocsTotal(){
        return removedDocsTotal;
    }
    
    @Override
    public Visitable visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        if (!n.isPrivate()) {
            removeJavadoc(n);
        }
        return n;
    }
    
    @Override
    public Visitable visit(ConstructorDeclaration n, Object arg) {
        super.visit(n, arg);
        removeJavadoc(n);
        return n;
    }
    
    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        removeJavadoc(n);
        return n;
    }
    
    private boolean removeJavadoc(NodeWithJavadoc n) {
        boolean removed = false;
        if (n.hasJavaDocComment()) {
            removed = n.removeJavaDocComment();
            System.out.println("Removed javadoc for " + n.toString());
            removedDocsTotal += 1;
        }
        
        return removed;
    }
}
