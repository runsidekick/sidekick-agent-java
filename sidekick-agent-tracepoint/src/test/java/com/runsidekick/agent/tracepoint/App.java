package com.runsidekick.agent.tracepoint;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
-Dsidekick.agent.broker.host=wss://broker.service.runsidekick.me
-Dsidekick.agent.broker.port=443
-Dsidekick.agent.broker.host2=ws://localhost
-Dsidekick.agent.broker.port2=7777
-Dsidekick.apikey=<API-KEY-HERE>
-Dsidekick.agent.application.name=hello
 */
public class App {

    public static void main(String[] args) throws Exception {
        EnvironmentInitializerManager.ensureInitialized();

        Hello hello = new Hello();

        String tracePointId1 = UUID.randomUUID().toString();
        String tracePointId2 = UUID.randomUUID().toString();

        TracePointSupport.putTracePoint(tracePointId1, Hello.class.getName(), 19, "serkan@thundra.io", null, "name==\"kaan\" AND i > 0", -1, -1, true, false);
        TracePointSupport.putTracePoint(tracePointId2, Hello.class.getName(), 27, "serkan@thundra.io", null, "idx > 0", -1, -1, true, false);

        List<String> names = Arrays.asList("serkan", "seyda", "kaan");
        int i = 0;
        while (true) {
            Thread.sleep(5000);
            System.out.println(hello.sayHello(names.get(i++ % names.size())));
        }
    }

}
