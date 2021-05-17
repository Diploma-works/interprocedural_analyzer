package org.meier.check.rule;

import org.meier.check.bean.DefectCase;
import org.meier.check.bean.DefectMessages;
import org.meier.check.bean.RuleResult;
import org.meier.inject.annotation.Rule;
import org.meier.model.ClassMeta;
import org.meier.model.FieldMeta;
import org.meier.model.MethodMeta;
import org.meier.model.Modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Rule
public class EncapsulationRule implements CheckRule {

    private static final String RULE_NAME = "Encapsulation check";

    @Override
    public RuleResult executeRule(Collection<ClassMeta> classes) {
        List<DefectCase> defects = new ArrayList<>();
        for (ClassMeta cls : classes) {
            getVulnerableFields(cls).forEach(field -> defects.add(DefectCase.newInstance()
                .setDefectName(DefectMessages.ENCAPSULATION_FIELD_NAME)
                .setClassName(cls.getFullName())
                .setLineNumber(field.getStartLine())
                .setDefectDescription(String.format(DefectMessages.ENCAPSULATION_FIELD_DESCRIPTION, field.getName()))));
            getUnusedNonPrivateMethods(cls).forEach(method -> defects.add(DefectCase.newInstance()
                .setDefectName(DefectMessages.ENCAPSULATION_METHOD_NAME)
                .setClassName(cls.getFullName())
                .setLineNumber(method.getStartLine())
                .setMethodName(method.getShortName())
                .setDefectDescription(String.format(DefectMessages.ENCAPSULATION_METHOD_DESCRIPTION, method.getName()))));
        }
        return new RuleResult(RULE_NAME, defects);
    }

    private List<FieldMeta> getVulnerableFields(ClassMeta cls) {
        return cls.getFields().values().stream()
                .filter(field -> field.accessModifier() != Modifier.PRIVATE && !field.getModifiers().contains(Modifier.FINAL))
                .collect(Collectors.toList());
    }

    private List<MethodMeta> getUnusedNonPrivateMethods(ClassMeta cls) {
        return cls.getMethods().stream().filter(meth -> !meth.getModifiers().contains(Modifier.PRIVATE))
                .filter(meth -> meth.getCalledBy().size() == 1 && meth.getCalledBy().contains(cls)).collect(Collectors.toList());
    }

}
