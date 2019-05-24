package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import models.Issue;

import java.util.List;

public class UnusedMethodChecker {

    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "METHOD_CHECKER";

    public UnusedMethodChecker(String fileName) {
        this.fileName = fileName;
    }

    public void handleUnusedMethods(CompilationUnit cu, List<Issue> issues, boolean modify) {
        if (cu.getPackageDeclaration().isPresent()) {
            this.packageName = cu.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }

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
            int lineNumber = unused.getRange().get().begin.line;
            if (!unused.getName().equals("main")) {
                String errorMessage = "Method " + unused.getName() + " is not used, please remove.";
                issues.add(generateIssue(lineNumber, errorMessage));

                if (modify) {
                    NodeList<BodyDeclaration<?>> newDeclarations = new NodeList<>();

                    for (BodyDeclaration declaration : cu.getTypes().get(0).getMembers()) {
                        if (declaration instanceof MethodDeclaration) {
                            MethodDeclaration mDec = (MethodDeclaration) declaration;
                            if (!mDec.equals(unused)) {
                                newDeclarations.add(mDec);
                            }
                        } else {
                            newDeclarations.add(declaration);
                        }
                    }

                    cu.getTypes().get(0).setMembers(newDeclarations);
                }
            }
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

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
