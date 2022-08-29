package com.runsidekick.agent.tracepoint.domain;

import java.util.List;

/**
 * Holds line information with the associated line number,
 * source code and active local variable informations
 * where information is collected.
 *
 * @author serkan
 */
public class LineInfo {

    private int line;
    private String source;
    private List<Variable> localVars;

    public LineInfo(int line) {
        this.line = line;
    }

    public LineInfo(int line, String source, List<Variable> localVars) {
        this.line = line;
        this.source = source;
        this.localVars = localVars;
    }

    public int getLine() {
        return line;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<Variable> getLocalVars() {
        return localVars;
    }

    public void setLocalVars(List<Variable> localVars) {
        this.localVars = localVars;
    }

    @Override
    public String toString() {
        return "LineInfo{" +
                "line=" + line +
                ", source='" + source + '\'' +
                ", localVars=" + localVars.toString() +
                '}';
    }

}
