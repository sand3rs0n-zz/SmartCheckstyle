package checkers;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.util.*;


public class JavaDocChecker extends VoidVisitorAdapter<Object> {

    @Override
    public void visit(CompilationUnit n, Object arg) {
        super.visit(n, arg);
        List<Comment> comments = n.getAllContainedComments();
        for (Comment comment: comments) {
            if (!comment.getCommentedNode().isPresent()) {
                int line = comment.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] Orphant comment found; "
                        + comment.getContent());
            }

        }

    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()) {
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] public class "
                        + n.getNameAsString() + " is missing document.");
            } else {
                Optional<Javadoc> jd = n.getJavadoc();
                jd.toString();
            }
        }
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {

        super.visit(n, arg);
        if (n.isPrivate())
            return;

        int line = n.getRange().get().begin.line;
        if (!n.hasJavaDocComment()) {
            System.out.println("[Line: " + line + "] non-private method "
                    + n.getNameAsString() + " is missing document.");
        } else {
            if (n.getJavadoc().get().getDescription().isEmpty()) {
                System.out.println("[Line: " + line + "] non-private method "
                        + n.getNameAsString() + " is missing description.");
            }

            if (n.getJavadoc().get().getBlockTags().size() < n.getParameters().size()) {
                System.out.println("[Line: " + line + "] non-private method "
                        + n.getNameAsString() + " is missing tag(s).");
            } else if (n.getJavadoc().get().getBlockTags().size() > n.getParameters().size()) {
                System.out.println("[Line: " + line + "] non-private method "
                        + n.getNameAsString() + " has unnecessary tag(s).");
            } else {
                // TODO: check missing parameters and add them to node.
                List<JavadocBlockTag> blockTags = n.getJavadoc().get().getBlockTags();
                HashSet<String> tagSet = new HashSet<>();
                for (JavadocBlockTag blockTag: blockTags) {
                    tagSet.add(blockTag.getName().get());
                }

                for (Parameter parameter: n.getParameters()) {
                    tagSet.remove(parameter.getNameAsString());
                }

                if (tagSet.size() > 0) {
                    System.out.println("[Line: " + line + "] non-private method "
                            + n.getNameAsString() + " has unmatched tag(s).");
                }
            }

            for (JavadocBlockTag blockTag : n.getJavadoc().get().getBlockTags()) {
                if (blockTag.getContent().isEmpty()) {
                    if (blockTag.getType() == JavadocBlockTag.Type.PARAM
                        || blockTag.getType() == JavadocBlockTag.Type.RETURN
                        || blockTag.getType() == JavadocBlockTag.Type.THROWS
                        || blockTag.getType() == JavadocBlockTag.Type.DEPRECATED) {
                        System.out.println("[Line: " + line + "] tag "
                                + blockTag.getType().toString() + " is missing description.");
                    }
                }
            }

            /*
            // TODO: Smart feature(?) - remove missing line (do we want this or just warning?)
            Predicate<JavadocBlockTag> emptyCheck = e -> e.getContent().isEmpty();
            System.out.println(n.getJavadoc().get().getBlockTags().size());
            n.getJavadoc().get().getBlockTags().removeIf(emptyCheck);
            System.out.println(n.getJavadoc().get().getBlockTags().size());
            */
        }

    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isPublic()) {
            if (!n.hasJavaDocComment()) {
                int line = n.getRange().get().begin.line;
                System.out.println("[Line: " + line + "] " + n.getModifiers().toString()
                        + " is missing document.");
            }
        }
    }

}
