package com.runsidekick.agent.core.util.thread;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author serkan
 */
public class ManagedThread extends Thread {

    public ManagedThread() {
    }

    public ManagedThread(Runnable target) {
        super(target);
    }

    public ManagedThread(@Nullable ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public ManagedThread(@NotNull String name) {
        super(name);
    }

    public ManagedThread(@Nullable ThreadGroup group, @NotNull String name) {
        super(group, name);
    }

    public ManagedThread(Runnable target, String name) {
        super(target, name);
    }

    public ManagedThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name) {
        super(group, target, name);
    }

    public ManagedThread(@Nullable ThreadGroup group, Runnable target, @NotNull String name, long stackSize) {
        super(group, target, name, stackSize);
    }

}
