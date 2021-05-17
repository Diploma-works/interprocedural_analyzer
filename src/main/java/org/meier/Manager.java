package org.meier;

import org.meier.check.RuleRunner;
import org.meier.inject.Application;
import org.meier.inject.annotation.InjectRunner;
import org.meier.loader.FSProjectLoader;
import org.meier.model.ClassMeta;
import org.meier.model.MetaHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class Manager {

    private static final Logger log = LoggerFactory.getLogger(Manager.class);

    private static final int minArgs = 2;
    private static final int srcPathArg = 0;
    private static final int jarsPathArg = 1;

    @InjectRunner
    private static RuleRunner runner;

    public static void main(String[] args) {
        if (args.length < minArgs) {
            log.error("Provide project src directory and dependencies jar directory");
            log.error("Format: java -jar jarname.jar project_source jar_source");
        } else {
            try {
                Application.run();
                FSProjectLoader loader = new FSProjectLoader();
                loader.loadProject(args[srcPathArg], args[jarsPathArg]);
                Collection<ClassMeta> classes = MetaHolder.getClasses().values();
                runner.setData(classes);
                runner.executeRules();
            } catch (IOException error) {
                log.error(error.getMessage());
            }
        }
    }

}
