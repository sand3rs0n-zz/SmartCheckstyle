package utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.javadoc.Javadoc;
import models.Issue;

import java.util.List;
import java.util.Optional;

public class JavadocRemover extends ModifierVisitor {
    
    @Override
    public Visitable visit(JavadocComment n, Object arg) {
        super.visit(n, arg);
        System.out.println("Visiting Javadoc");
        if (n.getParentNode().isPresent()) {
            Optional<Node> parent = n.getParentNode();
            
        }
        return n;
    }
    
    @Override
    public Visitable visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        if (!n.isPrivate()) {
            Optional<Javadoc> javadoc = n.getJavadoc();
            System.out.println("Removing JavaDocComment: " + n.removeJavaDocComment());
        }
        return n;
    }
    
    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.hasJavaDocComment()) {
            n.removeJavaDocComment();
            System.out.println("Removed javadoc for " + n.getNameAsString());
        }
        return n;
    }
}
