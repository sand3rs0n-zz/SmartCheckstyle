# Comparision Java AST Libraries

## References:

* https://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fdom%2FASTVisitor.html

* https://www.javadoc.io/doc/com.github.javaparser/javaparser-core/3.14.0

## Eclipse

* API(handler) to access to workspace/project

```java
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        // Get all projects in the workspace
        IProject[] projects = root.getProjects();
        // Loop over all projects
        for (IProject project : projects) {
            try {
                if (project.isNatureEnabled(JDT_NATURE)) {
                    analyseMethods(project);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
```

* API to perform some operation to specific nodes at the event of visiting and endvisiting. Allows to stop visiting children nodes.
  
```java
public boolean visit(T node)
public void endVisit(T node)
```

* API to define before and after behaviors of visitor

```java
public void preVisit(ASTNode node)
public void postVisit(ASTNode node)
```

## JavaParser

* APIs allows to pass additional parameter to visitors - benefits of chaining visitor while keeping the outputs

```java
GenericVisitorWithDefaults<R,A>
ModifierVisitor<A>
...
    Visitable 	visit(T n, A arg) 

VoidVisitorAdapter<A>
```

* Comes with SymbolSolver library - For example, easy to analize semantic information about a declaration.

```java
void aMethod() {
    int a;
    a = a + 1;
}
```

* Deliverables - Test automation integration. 

## Oracle OpenJDK javac

* APIs to develop Java compiler plugins. Access to AST, Code modifications.
* The plugin gets called by compiler and generates byte code.

* Deliverables - Compiler plugin that can generate bytecode directly after style check/modification

## Conclusion

We chose to use the javaparser library with the preference of high-level APIs available, i.e., the visitor with additional parameter and symbol solver. Eclipse JDT could benefit us to control the specific behavior of visiting node and improve performance. Developing a compiler plugin could benefit to simplify code and efficiency on code generation if there is good documentation.
