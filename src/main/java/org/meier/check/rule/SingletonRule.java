package org.meier.check.rule;

import org.meier.check.bean.DefectCase;
import org.meier.check.bean.DefectMessages;
import org.meier.check.bean.RuleResult;
import org.meier.check.rule.visitor.CreateIfNullVisitor;
import org.meier.check.rule.visitor.ObjectCreationVisitor;
import org.meier.check.rule.visitor.SynchronisedInitializationVisitor;
import org.meier.inject.annotation.Rule;
import org.meier.model.ClassMeta;
import org.meier.model.FieldMeta;
import org.meier.model.MethodMeta;
import org.meier.model.Modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Rule
public class SingletonRule implements CheckRule {

    private static final String RULE_NAME = "Singleton check";
    private final Predicate<FieldMeta> isInstance = field -> field.isStatic() && field.getFullClassName().equals(field.getOwnerClass().getFullName());
    private final Predicate<MethodMeta> isInstanceMethod = meth -> meth.isStatic() && meth.getFullQualifiedReturnType().equals(meth.getOwnerClass().getFullName());

    @Override
    public RuleResult executeRule(Collection<ClassMeta> classes) {
        List<DefectCase> defects = new ArrayList<>();
        classes.stream().filter(this::isSingleton).forEach(cls -> {
            if (!isInstancePrivate(cls)) {
                defects.add(DefectCase.newInstance()
                    .setDefectName(DefectMessages.SINGLETON_ENCAPSULATION_NAME)
                    .setClassName(cls.getFullName())
                    .setDefectDescription(DefectMessages.SINGLETON_ENCAPSULATION_DESCRIPTION));
            }
            if (!isInstanceCreationThreadSafe(cls)) {
                defects.add(DefectCase.newInstance()
                    .setDefectName(DefectMessages.SINGLETON_THREAD_SAFE_NAME)
                    .setClassName(cls.getFullName())
                    .setDefectDescription(DefectMessages.SINGLETON_THREAD_SAFE_DESCRIPTION));
            }
        });
        return new RuleResult(RULE_NAME, defects);
    }

    private boolean isSingleton(ClassMeta cls) {
        if (!cls.getConstructors().stream().allMatch(constructor -> constructor.accessModifier() == Modifier.PRIVATE))
            return false;
        return cls.getFields().values().stream().anyMatch(isInstance) && getInstanceMethod(cls) != null;
    }

    private boolean isInstancePrivate(ClassMeta cls) {
        FieldMeta field = getInstanceField(cls);
        if (field == null)
            return true;
        return field.accessModifier() == Modifier.PRIVATE;
    }

    private FieldMeta getInstanceField(ClassMeta cls) {
        return cls.getFields().values().stream().filter(isInstance).findAny().orElse(null);
    }

    private MethodMeta getInstanceMethod(ClassMeta cls) {
        return cls.getMethods().stream().filter(isInstanceMethod).findAny().orElse(null);
    }

    private boolean isInstanceCreationThreadSafe(ClassMeta cls) {
        MethodMeta instanceMethod = getInstanceMethod(cls);
        FieldMeta instanceField = getInstanceField(cls);
        if (instanceMethod.getContent().accept(new ObjectCreationVisitor(), cls).size() == 0)
            return true;
        Boolean createsIfNull = instanceMethod.getContent().accept(new CreateIfNullVisitor(), instanceField);
        if (instanceMethod.isSynchronised() && createsIfNull != null && createsIfNull)
            return true;
        if (instanceMethod.getCalledMethods().stream().map(MethodMeta::getOwnerClass).anyMatch(clazz -> clazz.getFullName().startsWith("java.util.concurrent.")) &&
            createsIfNull != null && createsIfNull)
            return true;
        Boolean threadSafe = instanceMethod.getContent().accept(new SynchronisedInitializationVisitor(), getInstanceField(cls));
        return threadSafe != null && threadSafe;
    }

}
