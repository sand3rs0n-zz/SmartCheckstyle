package checkers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UnusedVariableChecker {

    public void checkUnusedVariables(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);

            try {
                CompilationUnit cu =  JavaParser.parse(file);
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
                        System.out.println("Variable " + unused.getName() + " is not used, please remove");
                    }
                }
                System.out.println("Completed Document");

            } catch(IOException e) {
                new RuntimeException(e);
            }
            System.out.println(Strings.repeat("=", path.length()));
        }).explore(projectDir);
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
}
