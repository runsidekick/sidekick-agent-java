package com.runsidekick.agent.logpoint;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.condition.VariableInfo;
import com.runsidekick.agent.probe.condition.VariableInfoProvider;

import java.util.*;

import static com.runsidekick.agent.probe.condition.ConditionFactory.createConditionFromExpression;

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

        Object[] objectVars = new Object[] { new Item(), new Item() };
        ConditionContext cc = new ConditionContext(objectVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("x1", new VariableInfo(Item.class, 0));
            put("x2", new VariableInfo(Item.class, 1));
        }};
        ((Item)objectVars[0]).setItem(new Item());
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        System.out.println(parseAndEvaluateCondition("x1.item.doubleVal > 0", vip, cc));

        Hello hello = new Hello();

        String logExpression = "{{helloMsg}}";
        String logExpression2 = "{{idx}}";
        String logExpression4List = "{{#testList}}\n{{doubleVal}}\n{{/testList}}";
        String logExpression4List2 = "{{testList.0.doubleVal}}";
        String logExpression4List3 = "{{t.doubleVal}}";
        String logExpression4Object = "{{result}} - {{item.boolVal}}";

        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), Hello.class.getName(), 19, "yasin@thundra.io", logExpression, null, null, -1, -1, true, "INFO", false, false, null);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), Hello.class.getName(), 27, "yasin@thundra.io", logExpression2, null, "idx > 0", -1, -1,true, "INFO", false, false, null);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 12, "yasin@thundra.io", logExpression4List3, "t.doubleVal % 3 != 0", null, -1, -1,true, "INFO", false, false, null);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 14, "yasin@thundra.io", logExpression4List, null, null, -1, -1,true, "INFO", false, false, null);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 15, "yasin@thundra.io", logExpression4List2, null, null, -1, -1,true, "INFO", false, false, null);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), ObjectTest.class.getName(), 8, "yasin@thundra.io", logExpression4Object, null, null, -1, -1,true, "INFO", false, false, null);

        List<String> names = Arrays.asList("person1", "person2", "person3");
        int i = 0;
        while (true) {
            Thread.sleep(5000);
            hello.sayHello(names.get(i++ % names.size()));
            CollectionTest.listTest();
            ObjectTest.test();
        }
    }

    private static boolean parseAndEvaluateCondition(String conditionExpression,
                                                     VariableInfoProvider variableInfoProvider,
                                                     ConditionContext conditionContext) {
        Condition condition = createConditionFromExpression(conditionExpression, variableInfoProvider);
        return condition.evaluate(conditionContext);
    }

}
