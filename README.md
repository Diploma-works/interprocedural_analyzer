# JADA
Java architecture defect analyser
An AST and an object representation of project is built and passed to rule runner which executes 
all rules present on them. Results are exported to Exporter module.

### Run
Build the analyser using gradle uberjar task.
Run the jar with 2 arguments - root directory with source code of a project to
analyse, and a root directory with all dependency jars for that project.

### Meta representation description
This project uses Javaparser (https://github.com/javaparser/javaparser) library to build an AST 
for the project and then constructs a meta representation of a project, which consists of the following
classes:
+ **Meta** - an interface for all meta classes
+ **ClassMeta** - representation of a java class. It contains references to all methods, inner classes 
fields, initialization blocks, constructors and modifiers of a class.
+ **MethodMeta** - representation of a method. It contains references to all arguments, variables,
called and caller methods, accessed fields. It also contains method's signature and a reference to 
a part of an AST for the method.
+ **FieldMeta** - representation of a field. Contains a reference to owner class, name, type and a list of modifiers.
+ **CodeBlockMeta** - representation of an initialization block. It contains references to variables, 
presence of a static modifier and a reference to owner class.
+ **ConstructorMeta** - representation of a constructor. Contains a reference to owner class and a
list of arguments.
+ **EnumMeta** - extends ClassMeta. Describes an enum and contains a list of all enum constants.
+ **EnumConstantMeta** - extends EnumMeta. Contains a name.

### Implemented checks
Currently there are 8 rules:
+ **Single responsibility rule** - checks if class is single responsible. It calculates the number of 
common used fields for each pair of methods, saving the largest number, and divides methods into groups. Each group 
consists of methods that share more than largest\*threshold common fields. This property is transitive. If class 
contains more than one group, it is probably not single responsible.
+ **Inversion control rule** - checks if class is using some of his descendants.
+ **Encapsulation rule** - checks if all fields are either private or final and that all methods are either private or
called from outside their class.
+ **Descriptive naming rule** - checks if names are longer than 2 characters or are known and commonly used (such as "id")
and that ratio of non-character symbols in a name is below threshold.
+ **Factory method rule** - checks if all factory methods are necessary, that being using polymorphism or setting up
objects based on conditions.
+ **Visitor rule** - checks if each class hiererchy is visitor-consistent (either no classes could be visited by some visitor or
all of them). If some classes of hierarchy could be visited, but they acquire that by implementing some interface from outside the 
hierarchy, it does not count as defect. This rule also provides recommendations to implement visitor for classes with tree-like 
object structure.
+ **Decorator rule** - looks for potential decorators. If class A has a field of type B and calls its methods in some methods of
its own and number of own methods calling B's is greater than threshold and A doesn't extend or implement B, than it probably should
to implement the decorator pattern.
+ **Singleton rule** - checks if all singletons' instances are encapsulated and created in a thread-safe manner.

### Extension
There are 3 ways to extend JADA:
1. Create new Rule. New rule class has to implement CheckRule interface, reside in org.meier.check.rule 
package and it has to be annotated with @Rule. Dependency injection will do the rest.
2. Create new Runner. New runner class has to implement RuleRunner interface. A new constant has to be added to 
RunnerType enum. In order to use it, set runnerType parameter of @InjectRunner annotation accordingly.
3. Create new Exporter. New exporter class has to implement Exporter interface. A new constant has to be added to
ExporterType enum. In order to use it, set exporterType parameter of @InjectRunner annotation accordingly.

 
