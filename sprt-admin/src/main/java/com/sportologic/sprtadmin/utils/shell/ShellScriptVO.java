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

    public String[] buildScript() {
        String[] ret = new String[this.args.size() + 1];
        ret[0] = srcFolder + scriptFile;
        for (int i = 0; i < this.args.size(); i++) {
            ret[i + 1] = this.args.get(i);
        }
        
        return ret;
    }
}
