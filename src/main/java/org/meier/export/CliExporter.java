package org.meier.export;

import org.meier.check.bean.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliExporter implements Exporter {

    private static final Logger log = LoggerFactory.getLogger(CliExporter.class);

    @Override
    public void exportResults(AnalysisResult result) {
        result.getResults().forEach(ruleResult -> {
            log.info(String.format("%s:", ruleResult.getRuleName()));
            if (ruleResult.getFoundDefects().isEmpty()) {
                log.info("No defects found\n\n\n");
            }
            else {
                ruleResult.getFoundDefects().forEach(defect -> {
                    log.info(String.format("%s:", defect.getDefectName()));
                    if (defect.getClassName() != null)
                        log.info(String.format("in %s", defect.getClassName()));
                    if (defect.getLineNumber() != null)
                        log.info(String.format("at %d:", defect.getLineNumber()));
                    if (defect.getMethodName() != null)
                        log.info(String.format("in %s:", defect.getMethodName()));
                    log.info(defect.getDefectDescription()+"\n\n\n");
                });
            }
        });
        printTotal(result);
    }

    private void printTotal(AnalysisResult result) {
        log.info("Total defects found:");
        result.getResults().forEach(res -> log.info(res.getRuleName()+"\t:\t"+res.getFoundDefects().size()));
    }

}
