package com.runsidekick.agent.probe.sourcecode;

/**
 * @author serkan
 */
public class SourceCodeContent {

    public static final SourceCodeContent EMPTY = new SourceCodeContent(null, null);

    private final SourceCode sourceCode;
    private final String source;
    private final boolean original;

    public SourceCodeContent(SourceCode sourceCode, String source) {
        this.sourceCode = sourceCode;
        this.source = source;
        this.original = true;
    }

    public SourceCodeContent(SourceCode sourceCode, String source, boolean original) {
        this.sourceCode = sourceCode;
        this.source = source;
        this.original = original;
    }

    public SourceCode getSourceCode() {
        return sourceCode;
    }

    public String getSource() {
        return source;
    }

    public boolean isOriginal() {
        return original;
    }

    public String[] getLines() {
        if (source == null) {
            return null;
        }
        return source.split("\\r?\\n");
    }

}
