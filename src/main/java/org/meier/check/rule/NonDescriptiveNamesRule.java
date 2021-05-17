package org.meier.check.rule;

import org.meier.bean.VariableBean;
import org.meier.check.bean.DefectCase;
import org.meier.check.bean.DefectMessages;
import org.meier.check.bean.RuleResult;
import org.meier.inject.annotation.Rule;
import org.meier.model.ClassMeta;
import org.meier.model.CodeBlockMeta;
import org.meier.model.FieldMeta;
import org.meier.model.MethodMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Rule
public class NonDescriptiveNamesRule implements CheckRule {

    private static final String RULE_NAME = "Descriptive naming test";

    private final Set<String> shortDescriptiveNames = Set.of("me", "id", "no", "or", "f", "is", "cl", "i", "rs");

    @Override
    public RuleResult executeRule(Collection<ClassMeta> classes) {
        List<DefectCase> defects = new ArrayList<>();
        classes.forEach(cls -> {
            Set<MethodMeta> methods = cls.getMethods();
            Collection<FieldMeta> fields = cls.getFields().values();
            List<CodeBlockMeta> initializerBlocks = cls.getCodeBlocks();
            for (MethodMeta meth : methods) {
                String methName = meth.getShortName();
                if (isNonDescriptive(methName)) {
                    defects.add(DefectCase.newInstance()
                            .setClassName(cls.getFullName())
                            .setMethodName(methName)
                            .setDefectName(DefectMessages.NAMING_METHOD_NAME)
                            .setDefectDescription(String.format(DefectMessages.NAMING_METHOD_DESCRIPTION, methName))
                            .setLineNumber(meth.getStartLine()));
                }
                for (VariableBean variable : meth.getVariables()) {
                    if (variable.isNotLoopVariable() && isNonDescriptive(variable.getName())) {
                        defects.add(DefectCase.newInstance()
                                .setDefectName(DefectMessages.NAMING_VARIABLE_NAME)
                                .setClassName(cls.getFullName())
                                .setDefectDescription(String.format(DefectMessages.NAMING_VARIABLE_DESCRIPTION, variable.getName()))
                                .setLineNumber(variable.getLineNumber()));
                    }
                }
            }
            for (FieldMeta field: fields) {
                String name = field.getName();
                if (isNonDescriptive(name)) {
                    defects.add(DefectCase.newInstance()
                            .setDefectName(DefectMessages.NAMING_FIELD_NAME)
                            .setClassName(cls.getFullName())
                            .setDefectDescription(String.format(DefectMessages.NAMING_FIELD_DESCRIPTION, name))
                            .setLineNumber(field.getStartLine()));
                }
            }
            for (CodeBlockMeta block : initializerBlocks) {
                for (VariableBean variable : block.getVariables()) {
                    if (variable.isNotLoopVariable()) {
                        String name = variable.getName();
                        if (isNonDescriptive(name)) {
                            defects.add(DefectCase.newInstance()
                                    .setDefectName(DefectMessages.NAMING_VARIABLE_NAME)
                                    .setClassName(cls.getFullName())
                                    .setDefectDescription(String.format(DefectMessages.NAMING_VARIABLE_DESCRIPTION, name))
                                    .setLineNumber(variable.getLineNumber()));
                        }
                    }
                }
            }
        });
        return new RuleResult(RULE_NAME, defects);
    }

    private boolean isNonDescriptive(String name) {
        if (name.length() < 3 && !shortDescriptiveNames.contains(name))
            return true;
        long letters = name.chars().mapToObj(chr -> (char)chr).filter(Character::isLetter).count();
        double ratio = ((double)(name.length() - letters))/((double)(name.length()));
        return ratio >= 0.4;
    }

}
