package modifiers;

import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class DocumemtModifier extends ModifierVisitor<Object> {

    @Override
    public Visitable visit(LineComment n, Object arg) {
        return super.visit(n, arg);
    }

}
