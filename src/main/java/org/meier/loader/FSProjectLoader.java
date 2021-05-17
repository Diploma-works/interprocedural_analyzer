package org.meier.loader;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import org.meier.build.visitor.*;
import org.meier.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FSProjectLoader implements ProjectLoader {

    private static final Logger log = LoggerFactory.getLogger(FSProjectLoader.class);

    private ParserConfiguration init(Path projectPath, Path jarDir) throws IOException {
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        JavaParserTypeSolver javaParserTypeSolver = new JavaParserTypeSolver(projectPath);
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);
        Files.walk(jarDir, Integer.MAX_VALUE).filter(this::isJar).forEach(jar -> {
            try {
                combinedSolver.add(new JarTypeSolver(jar));
            } catch (IOException error) {
                log.debug("Failed to add a dependency jar", error);
            }
        });
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        return StaticJavaParser.getConfiguration()
                .setSymbolResolver(symbolSolver);
    }

    @Override
    public void loadProject(String dirPath, String jarsDir) throws IOException {
        Path path = Paths.get(dirPath);
        ProjectRoot projectRoot =
                new SymbolSolverCollectionStrategy(init(path, Paths.get(jarsDir)))
                        .collect(path);

        List<SourceRoot> roots = projectRoot.getSourceRoots();
        List<CompilationUnit> cus = roots.stream().flatMap(root -> {
            try {
                return root.tryToParse().stream();
            } catch (IOException e) {
                log.debug("Failed to parse source file", e);
                return Stream.empty();
            }
        }).map(pr -> pr.getResult().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LinkedHashMap<CompilationUnit, ClassMeta> classMetaForCUs = new LinkedHashMap<>();
        cus.forEach(classAst -> {
            boolean[] isInterface = new boolean[1];
            String clsName = classAst.accept(new ClassNameVisitor(), isInterface);
            if (clsName != null) {
                List<Modifier> modifiersList = classAst.accept(new ModifierVisitor(), ModifierVisitor.ModifierLevel.CLASS);
                ClassMeta cls = new ClassMeta(clsName, modifiersList, isInterface[0]);
                cls.setStartLine(classAst.getBegin().get().line);
                ClassOrInterfaceDeclaration clIntDecl = (ClassOrInterfaceDeclaration) classAst.getChildNodes().stream().filter(node -> node instanceof ClassOrInterfaceDeclaration).findFirst().orElse(null);
                EnumDeclaration enumDecl = (EnumDeclaration) classAst.getChildNodes().stream().filter(node -> node instanceof EnumDeclaration).findFirst().orElse(null);
                if (clIntDecl != null) {
                    try {
                        cls.setExtendedClasses(clIntDecl.getExtendedTypes().stream().map(type -> type.resolve().getQualifiedName()).collect(Collectors.toList()));
                        cls.setImplementedInterfaces(clIntDecl.getImplementedTypes().stream().map(type -> type.resolve().getQualifiedName()).collect(Collectors.toList()));
                    } catch (Exception error) {
                        log.debug("Failed to resolve type", error);
                    }
                } else if (enumDecl != null) {
                    cls.setImplementedInterfaces(enumDecl.getImplementedTypes().stream().map(type -> type.resolve().getQualifiedName()).collect(Collectors.toList()));
                }
                classAst.accept(new InitializerBlocksVisitor(), cls);
                MetaHolder.addClass(cls);
                classAst.accept(new FieldVisitor(), cls);
                classAst.accept(new ConstructorVisitor(), cls);
                classAst.accept(new InnerClassVisitor(), cls);
                classMetaForCUs.put(classAst, cls);
            }
        });
        classMetaForCUs.forEach((classAst, classMeta) -> {
            try {
                classAst.accept(new MethodVisitor(), classMeta);
            } catch(Exception error){
                log.warn("Failed to process source file", error);
            }}
        );
        InnerClassVisitor.runInnerClassesMethodVisitors();
        MetaHolder.forEach(ClassMeta::resolveExtendedAndImplemented);
        MetaHolder.forEach(ClassMeta::resolveMethodCalls);
    }

    private boolean isJar(Path file) {
        return file.toString().endsWith(".jar");
    }
}
