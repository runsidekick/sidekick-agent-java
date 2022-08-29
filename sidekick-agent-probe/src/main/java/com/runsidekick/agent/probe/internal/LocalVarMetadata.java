package com.runsidekick.agent.probe.internal;

/**
 * @author serkan
 */
class LocalVarMetadata implements Comparable<LocalVarMetadata> {

    private final int startPc;
    private final int idx;
    private final String name;
    private final String originalName;
    private final String typeSignature;

    LocalVarMetadata(int startPc, int idx, String name, String typeSignature) {
        this.startPc = startPc;
        this.idx = idx;
        this.name = name;
        this.originalName = name;
        this.typeSignature = typeSignature;
    }

    LocalVarMetadata(int startPc, int idx, String name, String originalName, String typeSignature) {
        this.startPc = startPc;
        this.idx = idx;
        this.name = name;
        this.originalName = originalName;
        this.typeSignature = typeSignature;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getTypeSignature() {
        return typeSignature;
    }

    @Override
    public int compareTo(LocalVarMetadata o) {
        int c1 = Integer.compare(startPc, o.startPc);
        if (c1 == 0) {
            return Integer.compare(idx, o.idx);
        } else {
            return c1;
        }
    }

}
