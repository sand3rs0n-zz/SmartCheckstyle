package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import models.Issue;

import java.util.List;

public class UnusedVariableChecker {

    private String packageName;
    private String fileName;
    private final static String ISSUE_TYPE = "VARIABLE_CHECKER";

    public UnusedVariableChecker(String fileName) {
        this.fileName = fileName;
    }

    public void checkUnusedVariables(CompilationUnit cu, List<Issue> issues, boolean modify) {
        if (cu.getPackageDeclaration().isPresent()) {
            this.packageName = cu.getPackageDeclaration().get().getNameAsString().trim();
        } else {
            this.packageName = "N/A";
        }

        NodeList<VariableDeclarator> allVariables= new NodeList<>();
        List<Node> children = cu.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            NodeList<VariableDeclarator> m = findChildVariables(children.get(i), allVariables);
            for (int j = 0; j < m.size(); j++){
                if (!checkContains(m.get(j).getName(), allVariables)) {
                    allVariables.add(m.get(j));
                }
            }
        }

        NodeList<NameExpr> usedVariables = new NodeList<>();
        for (int i = 0; i < children.size(); i++){
            NodeList<NameExpr> m = findUsedVariables(children.get(i), usedVariables);
            for (int j = 0; j < m.size(); j++){

                if (!checkContainsInCalls(m.get(j).getName(), usedVariables)) { usedVariables.add(m.get(j));}
            }
        }

        NodeList<VariableDeclarator> usedDeclarations = new NodeList<>();
        for (int i = 0; i < allVariables.size(); i++){
            VariableDeclarator m = allVariables.get(i);
            for (int j = 0; j < usedVariables.size(); j++){
                NameExpr used = usedVariables.get(j);
                if (m.getName().equals(used.getName())){
                    usedDeclarations.add(m);
                    break;
                }
            }
        }

        allVariables.removeAll(usedDeclarations);

        for (int i = 0; i < allVariables.size(); i++) {
            VariableDeclarator unused = allVariables.get(i);
            if (!unused.getName().equals("main")){
                String errorMessage = "Variable " + unused.getName() + " is not used, please remove.";
                issues.add(generateIssue(unused.getRange().get().begin.line, errorMessage));

                if (modify) {
                    NodeList<BodyDeclaration<?>> newDeclarations = new NodeList<>();

                    for (BodyDeclaration declaration : cu.getTypes().get(0).getMembers()) {
                        if (declaration instanceof FieldDeclaration) {
                            FieldDeclaration fDec = (FieldDeclaration) declaration;
                            NodeList<VariableDeclarator> newVariables = new NodeList<>();

                            for (VariableDeclarator v : fDec.getVariables()) {
                                if (!v.equals(unused)) {
                                    newVariables.add(v);
                                }
                            }

                            fDec.setVariables(newVariables);
                            if (fDec.getVariables().size() != 0) {
                                newDeclarations.add(fDec);
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

    public NodeList<VariableDeclarator> findChildVariables(Node n, NodeList<VariableDeclarator> variables){
        if (n.getClass().equals(VariableDeclarator.class)){
            variables.add((VariableDeclarator)n);
            return variables;
        }
        List<Node> children = n.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            NodeList<VariableDeclarator> m = findChildVariables(children.get(i), variables);
            for (int j = 0; j < m.size(); j++){
                if (!checkContains(m.get(j).getName(), variables)) {
                    variables.add(m.get(j));
                }
            }
        }
        return variables;
    }

    public NodeList<NameExpr> findUsedVariables(Node cu, NodeList<NameExpr> methods) {
        if (cu.getClass().equals(NameExpr.class)) {
            methods.add((NameExpr)cu);
            return methods;
        }
        List<Node> children = cu.getChildNodes();
        for (int i = 0; i < children.size(); i++) {
            NodeList<NameExpr> m = findUsedVariables(children.get(i), methods);
            for (int j = 0; j < m.size(); j++){

                if (!checkContainsInCalls(m.get(j).getName(), methods)) { methods.add(m.get(j));}
            }
        }
        return methods;
    }


    public boolean checkContains(SimpleName variableName, NodeList<VariableDeclarator> variables){
        for (VariableDeclarator v : variables){
            if (v.getName().equals(variableName)){
                return true;
            }
        }
        return false;
    }

    public boolean checkContainsInCalls(SimpleName variableName, NodeList<NameExpr> variables){
        for (NameExpr n : variables){
            if (n.getName().equals(variableName)){
                return true;
            }
        }
        return false;
    }

    private Issue generateIssue(int lineNumber, String errMessage) {
        return new Issue(packageName, fileName, lineNumber, ISSUE_TYPE, errMessage);
    }
}
