package org.meier.build.visitor;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.meier.bean.VariableBean;
import org.meier.build.util.TypeResolver;

import java.util.List;

public class NameTypeVisitor extends VoidVisitorAdapter<List<VariableBean>> {

    @Override
    public void visit(VariableDeclarator n, List<VariableBean> arg) {
        super.visit(n, arg);
        String className = ((TypeDeclaration<?>)n.getParentNode().get().getParentNode().get()).resolve().getQualifiedName();
        String type = TypeResolver.getQualifiedName(n.getType());
        arg.add(new VariableBean(n.getNameAsString(), type, className));
    }
}
