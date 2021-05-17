package org.meier.build.visitor;

import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.meier.bean.VariableBean;
import org.meier.build.util.TypeResolver;
import org.meier.model.CodeContainer;

public class VariablesVisitor extends VoidVisitorAdapter<CodeContainer> {

    @Override
    public void visit(VariableDeclarationExpr n, CodeContainer arg) {
        super.visit(n, arg);
        n.getVariables().forEach(variable -> {
            //TODO: Strange name
            VariableBean varia = new VariableBean(variable.getNameAsString(), TypeResolver.getQualifiedName(variable.getType()), null);
            if (n.getParentNode().isPresent() && n.getParentNode().get() instanceof ForStmt)
                varia.setLoopVariable(true);
            varia.setLineNumber(n.getBegin().get().line);
            arg.addVariable(varia);
        });
    }
}
