package com.sportologic.sprtadmin.utils.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ShellScriptRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShellScriptRunner.class);

    private final ShellScriptVO shellScript;

    public ShellScriptRunner(ShellScriptVO shellScript) {
        this.shellScript = shellScript;
    }

    public int run() throws IOException, InterruptedException {
        String[] shScript = shellScript.buildScript();
        logger.info("Running shell script: {}", shScript);
        System.out.println("Running shell script: " + shScript);
        Process process = Runtime.getRuntime().exec(shScript);

        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = 0;
        exitCode = process.waitFor();
        assert exitCode == 0;


        logger.info("Finished shell script: {}, exitCode: {}", shScript, exitCode);
        return exitCode;
    }
}
