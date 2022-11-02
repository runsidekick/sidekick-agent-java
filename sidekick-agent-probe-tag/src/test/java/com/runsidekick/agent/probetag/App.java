package com.runsidekick.agent.probetag;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import com.runsidekick.agent.logpoint.LogPointSupport;
import com.runsidekick.agent.logpoint.domain.LogPoint;
import com.runsidekick.agent.tracepoint.TracePointSupport;
import com.runsidekick.agent.tracepoint.domain.TracePoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String client = "yasin@thundra.io";

        Set<String> tags1 = new HashSet<String>() {{
            add("tag1");
            add("tag2");
        }};

        Set<String> tags2 = new HashSet<String>() {{
            add("tag2");
            add("tag3");
        }};

        String tracePointId1 = UUID.randomUUID().toString();
        String tracePointId2 = UUID.randomUUID().toString();

        String logPointId1 = UUID.randomUUID().toString();
        String logPointId2 = UUID.randomUUID().toString();

        TracePointSupport.putTracePoint(tracePointId1, Hello.class.getName(), 19, client, null, null, -1, -1, true, false, tags1);
        TracePointSupport.putTracePoint(tracePointId2, Hello.class.getName(), 27, client, null, null, -1, -1, true, false, tags2);

        LogPointSupport.putLogPoint(logPointId1, Hello.class.getName(), 19, client, "log1", null, null, -1, -1, false, "INFO", false, tags1);
        LogPointSupport.putLogPoint(logPointId2, Hello.class.getName(), 27, client, "log2", null, null, -1, -1, false, "INFO", false, tags2);

        disableTag(client, "tag1");

        List<TracePoint> disabledTracePoints = getTracePoints(client, true);
        List<LogPoint> disabledLogPoints = getLogPoints(client, true);

        System.out.println(disabledTracePoints.size() == 1);
        System.out.println(disabledTracePoints.get(0).getId().equals(tracePointId1));
        System.out.println(disabledLogPoints.size() == 1);
        System.out.println(disabledLogPoints.get(0).getId().equals(logPointId1));

        disableTag(client, "tag2");

        disabledTracePoints = getTracePoints(client, true);
        disabledLogPoints = getLogPoints(client, true);

        System.out.println(disabledTracePoints.size() == 2);
        System.out.println(disabledLogPoints.size() == 2);

        enableTag(client, "tag1");

        disabledTracePoints = getTracePoints(client, true);
        disabledLogPoints = getLogPoints(client, true);

        System.out.println(disabledTracePoints.size() == 1);
        System.out.println(disabledTracePoints.get(0).getId().equals(tracePointId2));
        System.out.println(disabledLogPoints.size() == 1);
        System.out.println(disabledLogPoints.get(0).getId().equals(logPointId2));

        enableTag(client, "tag2");

        disabledTracePoints = getTracePoints(client, true);
        disabledLogPoints = getLogPoints(client, true);

        System.out.println(disabledTracePoints.size() == 0);
        System.out.println(disabledLogPoints.size() == 0);

    }

    private static List<TracePoint> getTracePoints(String client, boolean disabled) {
        List<TracePoint> tracePoints = TracePointSupport.listTracePoints(client);
        return tracePoints.stream().filter(tracePoint -> tracePoint.isDisabled() == disabled).collect(Collectors.toList());
    }

    private static List<LogPoint> getLogPoints(String client, boolean disabled) {
        List<LogPoint> logPoints = LogPointSupport.listLogPoints(client);
        return logPoints.stream().filter(logPoint -> logPoint.isDisabled()).collect(Collectors.toList());
    }

    private static void disableTag(String client, String tag) {
        TracePointSupport.disableTag(tag, client);
        LogPointSupport.disableTag(tag, client);
    }

    private static void enableTag(String client, String tag) {
        TracePointSupport.enableTag(tag, client);
        LogPointSupport.enableTag(tag, client);
    }

}