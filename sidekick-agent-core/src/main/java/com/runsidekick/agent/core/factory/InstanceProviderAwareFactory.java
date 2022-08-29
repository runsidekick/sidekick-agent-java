package com.runsidekick.agent.core.factory;

import com.runsidekick.agent.core.instance.InstanceProvider;
import com.runsidekick.agent.core.instance.InstanceScope;

/**
 * {@link Factory} implementation which creates instances
 * through {@link InstanceProvider} by their {@link InstanceScope scope}s.
 *
 * @author serkan
 */
public class InstanceProviderAwareFactory<T> implements Factory<T> {

    private final Class<T> type;
    private final InstanceScope scope;

    public InstanceProviderAwareFactory(Class<T> type) {
        this(type, InstanceScope.GLOBAL);
    }

    public InstanceProviderAwareFactory(Class<T> type, InstanceScope scope) {
        this.type = type;
        this.scope = scope;
    }

    @Override
    public T create() {
        return InstanceProvider.getInstance(type, scope);
    }

}
