package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.expr.SimpleName;
import models.Issue;

import java.util.List;

public class UnusedImportChecker {

    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "IMPORT_CHECKER";

    public UnusedImportChecker(String fileName) {
        this.fileName = fileName;
    }

    public void checkUnusedImports(CompilationUnit cu, List<Issue> issues) {
        if (cu.getPackageDeclaration().isPresent()) {
            this.packageName = cu.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }

        NodeList<ImportDeclaration> imports = cu.getImports();

        List<Node> children = cu.getChildNodes();
        NodeList<ClassOrInterfaceType> allClasses = new NodeList<>();
        NodeList<MethodCallExpr> allMethods = new NodeList<>();
        for (int i = 0; i < children.size(); i++) {
            NodeList<ClassOrInterfaceType> classes = findChildClasses(children.get(i), allClasses);
            for (int j = 0; j < classes.size(); j++){
                if (!checkContains(classes.get(j).getName(), allClasses)){
                    allClasses.add(classes.get(j));
                }
            }

            NodeList<MethodCallExpr> methods = findUsedMethods(children.get(i), allMethods);
            for (int j = 0; j < methods.size(); j++){
                if (!checkContainsInCalls(methods.get(j).getName(), allMethods)){
                    allMethods.add(methods.get(j));
                }
            }
        }

        NodeList<ImportDeclaration> used = new NodeList<>();
        for (int i = 0; i < imports.size(); i++){

            if (imports.get(i).isAsterisk()){
                String errorMessage = imports.get(i).getName() + " contains an asterisk, please specify the imports used.";
                issues.add(generateIssue(imports.get(i).getRange().get().begin.line, errorMessage));
            }

            // Identify non-static imports
            for (int j = 0; j < allClasses.size(); j++){
                if (imports.get(i).getNameAsString().contains(allClasses.get(j).getNameAsString())){
                    if (!used.contains(imports.get(i))) { used.add(imports.get(i));}
                }
            }

            //identify static imports
            for (int j = 0; j < allMethods.size(); j++){
                if (allMethods.get(j).getScope().isPresent()){
                    if (imports.get(i).getNameAsString().contains((allMethods.get(j).getScope().get().toString()))){
                        if (!used.contains((imports.get(i)))) {
                            used.add((imports.get(i)));
                        }
                    }
                }

            }
        }

        for (ImportDeclaration i : imports){
            if (!used.contains(i)){
                String errorMessage = i.getName() + " is not used, please remove.";
                issues.add(generateIssue(i.getRange().get().begin.line, errorMessage));
            }
        }
    }

    public  NodeList<ClassOrInterfaceType> findChildClasses(Node child, NodeList<ClassOrInterfaceType> classes){
        if (child.getClass().equals(ClassOrInterfaceType.class)){
            classes.add((ClassOrInterfaceType)child);
            return classes;
        }

        List<Node> children = child.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            NodeList<ClassOrInterfaceType> m = findChildClasses(children.get(i), classes);
            for (int j = 0; j < m.size(); j++){
                if (!checkContains(m.get(j).getName(), classes)) {
                    classes.add(m.get(j));
                }
            }
        }
        return classes;

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

    public boolean checkContainsInCalls(SimpleName methodName, NodeList<MethodCallExpr> methods){
        for (MethodCallExpr m : methods){
            if (m.getName().equals(methodName)){
                return true;
            }
        }
        return false;
    }

    public boolean checkContains(SimpleName className, NodeList<ClassOrInterfaceType> classes){
        for (ClassOrInterfaceType c : classes){
            if (className.equals(c.getName())){
                return true;
            }
        }
        return false;
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
