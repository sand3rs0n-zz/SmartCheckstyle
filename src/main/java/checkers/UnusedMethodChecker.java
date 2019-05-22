package checkers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UnusedMethodChecker {

    public void checkUnusedMethods(File file) {

            try {
                CompilationUnit cu =  JavaParser.parse(file);
                NodeList<MethodDeclaration> allMethods= new NodeList<>();
                List<Node> children = cu.getChildNodes();
                for (int i = 0; i < children.size(); i++) {
                    NodeList<MethodDeclaration> m = findChildMethods(children.get(i), allMethods);
                    for (int j = 0; j < m.size(); j++){
                        if (!checkContains(m.get(j).getName(), allMethods)) {
                            allMethods.add(m.get(j));
                        }
                    }
                }

                NodeList<MethodCallExpr> usedMethods = new NodeList<>();
                for (int i = 0; i < children.size(); i++){
                    NodeList<MethodCallExpr> m = findUsedMethods(children.get(i), usedMethods);
                    for (int j = 0; j < m.size(); j++){

                        if (!checkContainsInCalls(m.get(j).getName(), usedMethods)) { usedMethods.add(m.get(j));}
                    }
                }

                NodeList<MethodDeclaration> usedDeclarations = new NodeList<>();
                for (int i = 0; i < allMethods.size(); i++){
                    MethodDeclaration m = allMethods.get(i);
                    for (int j = 0; j < usedMethods.size(); j++){
                        MethodCallExpr used = usedMethods.get(j);
                        System.out.println(used.getName());
                        if (m.getName().equals(used.getName())){
                            usedDeclarations.add(m);
                            break;
                        }
                    }
                }

                allMethods.removeAll(usedDeclarations);
                for (int i = 0; i < allMethods.size(); i++) {
                    MethodDeclaration unused = allMethods.get(i);
                    if (!unused.getName().equals("main")){
                        System.out.println("Method " + unused.getName() + " is not used, please remove");
                    }
                }
                System.out.println("Completed Document");

            } catch(IOException e) {
                new RuntimeException(e);
            }
    }

    public NodeList<MethodDeclaration> findChildMethods(Node cu, NodeList<MethodDeclaration> methods) {
        if (cu.getClass().equals(MethodDeclaration.class)){
            methods.add((MethodDeclaration)cu);
            return methods;
        }
        List<Node> children = cu.getChildNodes();
        for (int i = 0; i < children.size(); i++){
            NodeList<MethodDeclaration> m = findChildMethods(children.get(i), methods);
            for (int j = 0; j < m.size(); j++){

                if (!checkContains(m.get(j).getName(), methods)) { methods.add(m.get(j));}
            }
        }
        return methods;
    }

    public NodeList<MethodCallExpr> findUsedMethods(Node cu, NodeList<MethodCallExpr> methods) {
        if (cu.getClass().equals(MethodCallExpr.class)) {
            methods.add((MethodCallExpr)cu);
        }
        List<Node> children = cu.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            NodeList<MethodCallExpr> m = findUsedMethods(children.get(i), methods);
            for (int j = 0; j < m.size(); j++){

                if (!checkContainsInCalls(m.get(j).getName(), methods)) { methods.add(m.get(j));}
            }
        }
        return methods;
    }

    public boolean checkContains(SimpleName methodName, NodeList<MethodDeclaration> methods){
        for (MethodDeclaration m : methods){
            if (m.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }

    public boolean checkContainsInCalls(SimpleName methodName, NodeList<MethodCallExpr> methods){
        for (MethodCallExpr m : methods){
            if (m.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }
}
