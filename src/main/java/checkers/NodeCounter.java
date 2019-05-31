package checkers;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import models.Metrics;

public class NodeCounter extends VoidVisitorAdapter<Metrics> {
    
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Metrics metrics) {
        metrics.addNumClass(1);
        metrics.addNumLines(n.toString().split("\n").length);
        super.visit(n, metrics);
    }
    
    @Override
    public void visit(MethodDeclaration n, Metrics metrics) {
        metrics.addtNumMethods(1);
        
    }
    
    
}
