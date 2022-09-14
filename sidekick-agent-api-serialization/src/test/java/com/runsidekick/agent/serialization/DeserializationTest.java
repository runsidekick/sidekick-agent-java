package com.runsidekick.agent.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeserializationTest {

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
        String json = SerializationHelper.serializeValue(new X(), null);
        System.out.println(json);
        DeserializationHelper.Variable var = DeserializationHelper.parseVariable(json);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(var));
    }

}
