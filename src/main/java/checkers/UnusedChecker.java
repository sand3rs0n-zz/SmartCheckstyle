package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.List;

public class UnusedChecker extends VoidVisitorAdapter {
   @Override
    public void visit(CompilationUnit n, Object arg) {
       super.visit(n, arg);
       List<ImportDeclaration> used = new NodeList<ImportDeclaration>();
       NodeList<ImportDeclaration> imports = n.getImports();
       NodeList<TypeDeclaration<?>> types = n.getTypes();
       List<ClassOrInterfaceDeclaration> interfaces = n.getChildNodesByType(ClassOrInterfaceDeclaration.class);
       boolean found;
       for (int i = 0; i < imports.size(); i++){
           found = false;
           ImportDeclaration iD = imports.get(i);
           for (int j = 0; j < types.size(); j++){
                if (iD.getName().toString().equals(types.get(j).getName().toString())){
                    used.add(iD);
                    found = true;
                    break;
                }
            }
           if (!found) {
               for (ClassOrInterfaceDeclaration inter : interfaces) {
                   if (checkInterfaces(inter, iD)) {
                       used.add(iD);
                   }
               }

           }
       }
       System.out.println(used.size());
    }

    private boolean checkInterfaces(ClassOrInterfaceDeclaration i, ImportDeclaration imp){
        if (!i.isGeneric()){
            return false;
        }
        NodeList<TypeParameter> params = i.getTypeParameters();
        for (TypeParameter p: params ){
            if (p.getName().toString().equals(imp.getName())){
                return true;
            }
        }
        return false;
    }
}