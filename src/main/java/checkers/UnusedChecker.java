package checkers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.metamodel.ClassOrInterfaceTypeMetaModel;
import com.google.common.base.Strings;
import me.tomassetti.support.DirExplorer;
import com.github.javaparser.ast.expr.SimpleName;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UnusedChecker {

    public void checkUnusedImports(File projectDir) {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            System.out.println(path);

            try {
                CompilationUnit cu = JavaParser.parse(file);
                NodeList<ImportDeclaration> imports = cu.getImports();
                for (ImportDeclaration i : imports) {
                    System.out.println(i.getName());
                }

                List<Node> children = cu.getChildNodes();
                NodeList<ClassOrInterfaceType> allClasses = new NodeList<>();
                for (int i = 0; i < children.size(); i++) {
                    NodeList<ClassOrInterfaceType> classes = findChildClasses(children.get(i), allClasses);
                    for (int j = 0; j < classes.size(); j++){
                        if (!checkContains(classes.get(j).getName(), allClasses)){
                            allClasses.add(classes.get(j));
                        }
                    }
                }

                //identify non-static imports
                NodeList<ImportDeclaration> used = new NodeList<>();
                for (int i = 0; i < imports.size(); i++){
                    for (int j = 0; j < allClasses.size(); j++){
                        if (imports.get(i).getNameAsString().contains(allClasses.get(j).getNameAsString())){
                            if (!used.contains(imports.get(i))) { used.add(imports.get(i));}
                        }
                    }


                }

                for (ImportDeclaration i : imports){
                    if (!used.contains(i)){
                        System.out.println(i.getName() + " is not used, please remove");
                    }
                }

            } catch (IOException e) {
                new RuntimeException(e);
            }
            System.out.println(Strings.repeat("=", path.length()));
        }).explore(projectDir);
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

    public boolean checkContains(SimpleName className, NodeList<ClassOrInterfaceType> classes){
        for (ClassOrInterfaceType c : classes){
            if (className.equals(c.getName())){
                return true;
            }
        }
        return false;
    }
}
