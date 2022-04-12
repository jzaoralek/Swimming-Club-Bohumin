package com.sportologic.sprtadmin.utils.shell;

import java.util.ArrayList;
import java.util.List;

public class ShellScriptVO {
    private final String srcFolder;
    private final String scriptFile;
    private List<String> args;

    public ShellScriptVO(String srcFolder, String scriptFile) {
        this.srcFolder = srcFolder;
        this.scriptFile = scriptFile;
        this.args = new ArrayList<>();
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }

    public String buildScript() {
        StringBuilder cmdSb = new StringBuilder(srcFolder);
        cmdSb.append(scriptFile);
        this.args.forEach(i ->  cmdSb.append(" " + i));
        return cmdSb.toString();
    }
}
