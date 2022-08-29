package com.runsidekick.agent.probe.sourcecode.provider;

import com.runsidekick.agent.core.entity.Ordered;
import com.runsidekick.agent.probe.sourcecode.SourceCodeContent;
import com.runsidekick.agent.probe.sourcecode.SourceCodeType;

/**
 * @author serkan
 */
public interface SourceCodeProvider extends Ordered {

    SourceCodeContent getSourceCodeContent(
            ClassLoader classLoader,
            String className,
            SourceCodeType sourceCodeType);

}
