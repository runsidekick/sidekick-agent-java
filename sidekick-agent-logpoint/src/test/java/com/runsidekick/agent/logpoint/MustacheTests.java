package com.runsidekick.agent.logpoint;

import com.runsidekick.agent.api.dataredaction.DataRedactionContext;
import com.runsidekick.agent.api.dataredaction.SidekickDataRedactionAPI;
import com.runsidekick.agent.logpoint.expression.execute.impl.MustacheExpressionExecutor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yasin.kalafat
 */
public class MustacheTests implements SidekickDataRedactionAPI {
    static class X {

        Y y = new Y();
        Y[] array = new Y[] {new Y()};
        List<Y> list = Arrays.asList(new Y());
        Map<String, Y> map = new HashMap<String, Y>() {{
            put("y", new Y());
        }};

    }

    static class Y {

        boolean b1 = true;
        byte y1 = 0;
        int i1 = 1;
        float f1 = 2.0f;
        long l1 = 3l;
        double d1 = 4.0;

        Boolean b2 = true;
        Byte y2 = 0;
        Integer i2 = 1;
        Float f2 = 2.0f;
        Long l2 = 3l;
        Double d2 = 4.0;

        String s  = "hello";
        E e = E.A;
        Z z = new Z();

        boolean[] ba1 = new boolean[] {true};
        byte[] ya1 = new byte[] {0};
        int[] ia1 = new int[] {1};
        float[] fa1 = new float[] {2.0f};
        long[] la1 = new long[] {3l};
        double[] da1 = new double[] {4.0d};

        Boolean[] ba2 = new Boolean[] {true};
        Byte[] ya2 = new Byte[] {0};
        Integer[] ia2 = new Integer[] {1};
        Float[] fa2 = new Float[] {2.0f};
        Long[] la2 = new Long[] {3l};
        Double[] da2 = new Double[] {4.0d};

        String[] sa = new String[] {"hello"};
        Z[] za = new Z[] {new Z()};

        List<Boolean> bl = Arrays.asList(true);
        List<Byte> yl = Arrays.asList((byte) 0);
        List<Integer> il = Arrays.asList(1);
        List<Float> fl = Arrays.asList(2.0f);
        List<Long> ll = Arrays.asList(3l);
        List<Double> dl = Arrays.asList(4.0d);
        List<String> sl = Arrays.asList("hello");
        List<Z> zl = Arrays.asList(new Z());

        Map<String, Boolean> bm = new HashMap<String, Boolean>() {{ put("key", true); }};
        Map<String, Byte> ym = new HashMap<String, Byte>() {{ put("key", (byte) 0); }};
        Map<String, Integer> im = new HashMap<String, Integer>() {{ put("key", 1); }};
        Map<String, Float> fm = new HashMap<String, Float>() {{ put("key", 2.0f); }};
        Map<String, Long> lm = new HashMap<String, Long>() {{ put("key", 3l); }};
        Map<String, Double> dm = new HashMap<String, Double>() {{ put("key", 4.0d); }};
        Map<String, String> sm = new HashMap<String, String>() {{ put("key", "hello"); }};
        Map<String, Z> zm = new HashMap<String, Z>() {{ put("key", new Z()); }};

    }

    static class Z {

        int iVal1 = 1;
        Integer iVal2 = 2;
        String strVal = "val";
        ByteBuffer bbVal = ByteBuffer.wrap(new byte[] { 0, 1, 2 });

    }

    static enum E {

        A,
        B,
        C,

    }
    public static void main(String[] args) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        variables.put("x", new X());
        String[] expressions = new String[]{
                "Test {{x.y.s}}",
                "Test {{x.y.e}}",
                "Test {{x.y.z.iVal1}}",
                "Test {{x.y.z.iVal2}}",
                "Test {{x.y.z.strVal}}",
                "Test {{x.y.sa.0}}",
                "Test {{x.y.za.0.iVal1}}",

                "Test {{x.y.b1}}",
                "Test {{x.y.y1}}",
                "Test {{x.y.i1}}",
                "Test {{x.y.f1}}",
                "Test {{x.y.l1}}",
                "Test {{x.y.d1}}",

                "Test {{x.y.b2}}",
                "Test {{x.y.y2}}",
                "Test {{x.y.i2}}",
                "Test {{x.y.f2}}",
                "Test {{x.y.l2}}",
                "Test {{x.y.d2}}",

                "Test {{x.y.ba1.0}}",
                "Test {{x.y.ya1.0}}",
                "Test {{x.y.ia1.0}}",
                "Test {{x.y.fa1.0}}",
                "Test {{x.y.la1.0}}",
                "Test {{x.y.da1.0}}",

                "Test {{x.y.ba2.0}}",
                "Test {{x.y.ya2.0}}",
                "Test {{x.y.ia2.0}}",
                "Test {{x.y.fa2.0}}",
                "Test {{x.y.la2.0}}",
                "Test {{x.y.da2.0}}",

/*                "Test {{x.y.bl.0}}",
                "Test {{x.y.yl.0}}",
                "Test {{x.y.il.0}}",
                "Test {{x.y.fl.0}}",
                "Test {{x.y.ll.0}}",
                "Test {{x.y.dl.0}}",
                "Test {{x.y.sl.0}}",
                "Test {{x.y.zl.0.iVal1}}",

                "Test {{x.y.bm.key}}",
                "Test {{x.y.ym.key}}",
                "Test {{x.y.im.key}}",
                "Test {{x.y.fm.key}}",
                "Test {{x.y.lm.key}}",
                "Test {{x.y.dm.key}}",
                "Test {{x.y.sm.key}}",
                "Test {{x.y.zm.key.iVal1}}",*/
        };

        MustacheExpressionExecutor mustache = new MustacheExpressionExecutor();
        for (int i = 0; i < expressions.length; i++) {
            String res = mustache.execute(null, expressions[i], variables);
            System.out.println(res);
        }
    }

    @Override
    public String redactLogMessage(DataRedactionContext dataRedactionContext, String logExpression, String logMessage) {
        return logMessage;
    }

    @Override
    public boolean shouldRedactVariable(DataRedactionContext dataRedactionContext, String fieldName) {
        return fieldName.equals("iVal1");
    }

}
