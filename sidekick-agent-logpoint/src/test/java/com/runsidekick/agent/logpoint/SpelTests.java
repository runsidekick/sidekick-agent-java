package com.runsidekick.agent.logpoint;

import com.runsidekick.agent.core.initialize.EnvironmentInitializerManager;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.*;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

/*
-Dsidekick.agent.broker.host=wss://broker.service.runsidekick.me
-Dsidekick.agent.broker.port=443
-Dsidekick.agent.broker.host2=ws://localhost
-Dsidekick.agent.broker.port2=7777
-Dsidekick.apikey=<API-KEY-HERE>
-Dsidekick.agent.application.name=hello
 */
public class SpelTests {

    public static void main(String[] args) throws Exception {
        EnvironmentInitializerManager.ensureInitialized();

        spelTests();

        Hello hello = new Hello();

        String logExpression = "['sb'].toString()";
        String logExpression2 = "['idx']";
        String logExpression4List = "for(t in ['testList']) t.doubleVal";
        String logExpression4List2 = "['testList'][0].doubleVal";
        String logExpression4List3 = "['t'].doubleVal";
        String logExpression4Object = "['result'] and ['item'].boolVal";

        CollectionTest.listTest();

//        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), Hello.class.getName(), 21, "yasin@thundra.io", logExpression, null, null, -1, -1, false, false);
//        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), Hello.class.getName(), 27, "yasin@thundra.io", logExpression2, null, "idx > 0", -1, -1, false, false);
//        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 14, "yasin@thundra.io", logExpression4List, null, null, -1, -1, false, false);
//        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 15, "yasin@thundra.io", logExpression4List2, null, null, -1, -1, false, false);
//        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), CollectionTest.class.getName(), 12, "yasin@thundra.io", logExpression4List3, "t.doubleVal % 3 != 0", null, -1, -1, false, false);
        LogPointSupport.putLogPoint(UUID.randomUUID().toString(), ObjectTest.class.getName(), 8, "yasin@thundra.io", logExpression4Object, null, null, -1, -1,true, "INFO", false, false);

        List<String> names = Arrays.asList("person1", "person2", "person3");
        int i = 0;
        while (true) {
            Thread.sleep(5000);
            hello.sayHello(names.get(i++ % names.size()));
            CollectionTest.listTest();
            ObjectTest.test();
        }
    }

    public static void spelTests() {
        Map<String, Object> props= new HashMap<>();
        props.put("name", "john");
        props.put("age", 20);
        props.put("item", new Item());


        StandardEvaluationContext context = new StandardEvaluationContext(props);
        context.addPropertyAccessor(new MapAccessor());

        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration());

        Expression exp = parser.parseRaw("['name']=='john1' && ['age']> 18");
        String s  = exp.getValue(context, String.class);
        System.out.println(s);

        exp = parser.parseRaw("['item']['doubleVal'] > 0 and T(Runtime).getRuntime().exec(\"pwd\").waitFor() == 0");
        s  = exp.getValue(context, String.class);
        System.out.println(s);

        exp = parser.parseRaw("T(Runtime).getRuntime().freeMemory()");
        s  = exp.getValue(context, String.class);
        System.out.println(s);
    }
}
