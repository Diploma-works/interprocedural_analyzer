package org.meier;

import org.meier.check.RuleRunner;
import org.meier.inject.Application;
import org.meier.inject.annotation.InjectRunner;
import org.meier.loader.FSProjectLoader;
import org.meier.model.ClassMeta;
import org.meier.model.MetaHolder;

import java.io.IOException;
import java.util.Collection;
//TODO: Rename the class to Manager, since it is a control class
public class Main {

    @InjectRunner
    private static RuleRunner runner;
    //TODO: Remove System.out.println() everywhere, use log4j
    public static void main(String[] args) {
        //TODO: Extract all magic variables to constants
        if (args.length < 2) {
            System.out.println("Provide project src directory and dependencies jar directory");
            System.out.println("Format: java -jar jarname.jar project_source jar_source");
        } else {
            try {
                Application.run();
                FSProjectLoader loader = new FSProjectLoader();
                //TODO: Extract all magic variables to constants
                loader.loadProject(args[0], args[1]);
                Collection<ClassMeta> classes = MetaHolder.getClasses().values();
                runner.setData(classes);
                runner.executeRules();
            } catch (IOException error) {
                //TODO: Write errors to a log file, type error
                System.out.println(error.getMessage());
            }
        }
    }

}
