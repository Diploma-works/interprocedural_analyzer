package org.meier.check.bean;

public final class DefectMessages {

    private DefectMessages(){}

    public static final String DECORATOR_NAME = "Missed decorator opportunity";
    public static final String DECORATOR_DESCRIPTION = "There is a \"has-a\" relationship between %s and %s. Field %s in class %s is " +
            "used to decorate class %s, but there is no inheritance between classes and therefore " +
            "polymorphism is disabled. That is probably a missed opportunity to use Decorator pattern.";
    public static final String DEPENDENCY_INVERSION_NAME = "Ancestor depends on child class";
    public static final String DEPENDENCY_INVERSION_DESCRIPTION = "Class %s is ancestor of class %s, yet it depends on it, thus breaking the dependency inversion principle.";
    public static final String ENCAPSULATION_FIELD_NAME = "Field is not encapsulated";
    public static final String ENCAPSULATION_FIELD_DESCRIPTION = "Field %s is not private and not final and therefore is not encapsulated";
    public static final String ENCAPSULATION_METHOD_NAME = "Method is only used in its own class and is not private";
    public static final String ENCAPSULATION_METHOD_DESCRIPTION = "Method %s, which is only used by its own class, is not private. This could be a sign of lack of encapsulation";
    public static final String FACTORY_METHOD_NAME = "Unnecessary Factory method";
    public static final String FACTORY_METHOD_DESCRIPTION = "This factory method serves no purpose - it is not a part of Singleton or Builder pattern, " +
            "all implementations create the object of same type and it is always set up the same way. Using constructor should probably be considered";
    public static final String NAMING_METHOD_NAME = "Non-descriptive method name";
    public static final String NAMING_METHOD_DESCRIPTION = "\"%s\" is probably not descriptive enough";
    public static final String NAMING_VARIABLE_NAME = "Non-descriptive variable name";
    public static final String NAMING_VARIABLE_DESCRIPTION = "\"%s\" is probably not descriptive enough";
    public static final String NAMING_FIELD_NAME = "Non-descriptive field name";
    public static final String NAMING_FIELD_DESCRIPTION = "\"%s\" is probably not descriptive enough";
    public static final String SINGLE_RESPONSIBILITY_STATIC_NAME = "Suspicious set of static and non-static methods in class";
    public static final String SINGLE_RESPONSIBILITY_STATIC_DESCRIPTION = "Static methods' ratio is high, yet class contains non-static methods as well. " +
            "Static and non-static methods probably serve different purposes, which breaks the" +
            " Single Responsibility principle";
    public static final String SINGLE_RESPONSIBILITY_GROUPS_NAME = "Several logic groups of methods in a single class";
    public static final String SINGLE_RESPONSIBILITY_GROUPS_DESCRIPTION = "Class contains several almost independent groups of methods, which could be " +
            "a sign of breaking the Single Responsibility principle. Groups:\n";
    public static final String SINGLETON_ENCAPSULATION_NAME = "Singleton instance is not encapsulated";
    public static final String SINGLETON_ENCAPSULATION_DESCRIPTION = "Instance field of a singleton class is not private";
    public static final String SINGLETON_THREAD_SAFE_NAME = "Singleton instance creation is not thread-safe";
    public static final String SINGLETON_THREAD_SAFE_DESCRIPTION = "Singleton instance is created in an access method and there is no check if instance" +
            "has already been created or there is no synchronisation implemented to prevent instance from being " +
            "created several times in a multi-threaded environment";
    public static final String VISITOR_CONSISTENCY_NAME = "Inconsistent visitable classes hierarchy";
    public static final String VISITOR_CONSISTENCY_DESCRIPTION = "More than one descendant of class %s " +
            "implement a visitor pattern with %s as a visitor class, but some classes " +
            "in the hierarchy can not be visited. This is probably an inconsistency";
    public static final String VISITOR_ADVISED_NAME = "Visitor is advised";
    public static final String VISITOR_ADVISED_DESCRIPTION = "Class %s represents a complex tree structure. It is advisable to " +
            "implement a Visitor to process this class' objects. That would help separate data " +
            "definition and business logic";
}
