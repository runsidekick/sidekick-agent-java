package com.runsidekick.agent.tracepoint.condition;

import com.runsidekick.agent.probe.condition.Condition;
import com.runsidekick.agent.probe.condition.ConditionContext;
import com.runsidekick.agent.probe.condition.ConditionFactory;
import com.runsidekick.agent.probe.condition.VariableInfo;
import com.runsidekick.agent.probe.condition.VariableInfoProvider;
import com.runsidekick.agent.probe.condition.value.BasePlaceholderValueProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.runsidekick.agent.probe.condition.ConditionFactory.createConditionFromExpression;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author serkan
 */
public class ConditionTest {

    private static boolean parseAndEvaluateCondition(String conditionExpression,
                                                     VariableInfoProvider variableInfoProvider,
                                                     ConditionContext conditionContext) {
        Condition condition = createConditionFromExpression(conditionExpression, variableInfoProvider);
        return condition.evaluate(conditionContext);
    }

    @Test
    public void booleanTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] booleanVars = new Object[] { true, true, false };
        ConditionContext cc = new ConditionContext(booleanVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("b", new VariableInfo(boolean.class, 0));
            put("b1", new VariableInfo(boolean.class, 1));
            put("b2", new VariableInfo(boolean.class, 2));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        // Compare with constant
        assertThat(parseAndEvaluateCondition("b == true", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("b == false", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != false", vip, cc), is(true));

        // Compare with variable
        assertThat(parseAndEvaluateCondition("b == b1", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("b == b2", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != b1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != b2", vip, cc), is(true));

        // Compare with other types
        assertThat(parseAndEvaluateCondition("b == 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b == \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("b != \"hello\"", vip, cc), is(false));
    }

    @Test
    public void characterTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] charVars = new Object[] { 'x', 'x', 'Y' };
        ConditionContext cc = new ConditionContext(charVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("c", new VariableInfo(char.class, 0));
            put("c1", new VariableInfo(char.class, 1));
            put("c2", new VariableInfo(char.class, 2));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        // Compare with constant
        assertThat(parseAndEvaluateCondition("c == 'x'", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("c == 'y'", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != 'x'", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != 'y'", vip, cc), is(true));

        // Compare with variable
        assertThat(parseAndEvaluateCondition("c == c1", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("c == c2", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != c1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != c2", vip, cc), is(true));

        // Compare with other types
        assertThat(parseAndEvaluateCondition("c == 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c == \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("c != \"hello\"", vip, cc), is(false));
    }

    private void numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Class<? extends Number> numberType,
                                                                    Object[] numberVariables,
                                                                    String smallNumberConstant,
                                                                    String mediumNumberConstant,
                                                                    String bigNumberConstant) {
        ConditionContext cc = new ConditionContext(numberVariables);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("i", new VariableInfo(numberType, 0));
            put("j", new VariableInfo(numberType, 1));
            put("k", new VariableInfo(numberType, 2));
            put("t", new VariableInfo(numberType, 3));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        // Compare with constant
        assertThat(parseAndEvaluateCondition("i == " + mediumNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == " + bigNumberConstant, vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != " + mediumNumberConstant, vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != " + bigNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i > " + mediumNumberConstant, vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i > " + smallNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i >= " + mediumNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i >= " + bigNumberConstant, vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i < " + mediumNumberConstant, vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i < " + bigNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i <= " + mediumNumberConstant, vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i <= " + smallNumberConstant, vip, cc), is(false));

        // Compare with variable
        assertThat(parseAndEvaluateCondition("i == j", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == k", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != j", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != k", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i > k", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i > t", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i >= j", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i >= k", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i < k", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i < t", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i <= j", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i <= t", vip, cc), is(false));

        // Compare with other types
        assertThat(parseAndEvaluateCondition("i == true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i < true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i <= true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i > true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i >= true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i == \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i != \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i < \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i <= \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i > \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i >= \"hello\"", vip, cc), is(false));
    }

    @Test
    public void byteTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (byte) 5, (byte) 5, (byte) 10, (byte) 0 };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Byte.class, vars, "1", "5", "10");
    }

    @Test
    public void shortTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (short) 5, (short) 5, (short) 10, (short) 0 };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Short.class, vars, "1", "5", "10");
    }

    @Test
    public void integerTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (int) 5, (int) 5, (int) 10, (int) 0 };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Integer.class, vars, "1", "5", "10");
    }

    @Test
    public void longTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (long) 5l, (long) 5l, (long) 10l, (long) 0l };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Long.class, vars, "1", "5", "10");
    }

    @Test
    public void floatTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (float) 5.5f, (float) 5.5f, (float) 10.5f, (float) 0.5f };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Float.class, vars, "1.5", "5.5", "10.5");
    }

    @Test
    public void doubleTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] vars = new Object[] { (double) 5.5d, (double) 5.5d, (double) 10.5d, (double) 0.5d };
        numberTypedOperandShouldBeAbleToEvaluatedCorrectly(Double.class, vars, "1.5", "5.5", "10.5");
    }

    @Test
    public void stringTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] stringVars = new Object[] { "hello", "hello", "hi" };
        ConditionContext cc = new ConditionContext(stringVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("s", new VariableInfo(String.class, 0));
            put("s1", new VariableInfo(String.class, 1));
            put("s2", new VariableInfo(String.class, 2));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        // Compare with constant
        assertThat(parseAndEvaluateCondition("s == \"hello\"", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("s == \"hi\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != \"hi\"", vip, cc), is(true));

        // Compare with variable
        assertThat(parseAndEvaluateCondition("s == s1", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("s == s2", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != s1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != s2", vip, cc), is(true));

        // Compare with other types
        assertThat(parseAndEvaluateCondition("s == 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s == true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("s != true", vip, cc), is(false));
    }

    @Test
    public void objectTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] objectVars = new Object[] { new X(), new X() };
        ConditionContext cc = new ConditionContext(objectVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("x1", new VariableInfo(X.class, 0));
            put("x2", new VariableInfo(X.class, 1));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        // Compare with constant
        assertThat(parseAndEvaluateCondition("x1.i == 5", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.i != 5", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.i > 0", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.i > 10", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.i >= 5", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.i < 10", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.i < 0", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.i <= 5", vip, cc), is(true));

        assertThat(parseAndEvaluateCondition("x1.y.b == true", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.y.b == false", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.b != true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.b != false", vip, cc), is(true));

        assertThat(parseAndEvaluateCondition("x1.y.z.s == \"hello\"", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.y.z.s == \"hi\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.z.s != \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.z.s != \"hi\"", vip, cc), is(true));

        // Compare with variable
        assertThat(parseAndEvaluateCondition("x1.i == x2.i", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.y.b == x2.y.b", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("x1.y.z.s == x2.y.z.s", vip, cc), is(true));

        // Compare with other types
        assertThat(parseAndEvaluateCondition("x1.i == true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.i == \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.b == 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.b == \"hello\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.z.s == 1", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.z.s == true", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.i == x2.y.b", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("x1.y.b == x2.y.z.s", vip, cc), is(false));
    }

    @Test
    public void placeholderTypedOperandShouldBeAbleToEvaluatedCorrectly() {
        Object[] objectVars = new Object[] { 5, true, "hello", new X() };
        ConditionContext cc = new ConditionContext(objectVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("i", new VariableInfo(int.class, 0));
            put("b", new VariableInfo(boolean.class, 1));
            put("s", new VariableInfo(String.class, 2));
            put("x", new VariableInfo(X.class, 3));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        ConditionFactory.registerPlaceholderValueProvider(new BasePlaceholderValueProvider("myBoolean", boolean.class) {
            @Override
            public Object getValue(ConditionContext conditionContext) {
                return true;
            }
        });
        ConditionFactory.registerPlaceholderValueProvider(new BasePlaceholderValueProvider("myNumber", int.class) {
            @Override
            public Object getValue(ConditionContext conditionContext) {
                return 5;
            }
        });
        ConditionFactory.registerPlaceholderValueProvider(new BasePlaceholderValueProvider<String>("myString", String.class) {
            @Override
            public String getValue(ConditionContext conditionContext) {
                return "hello";
            }
        });
        try {
            // Compare with constant
            assertThat(parseAndEvaluateCondition("${myNumber} == 5", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myNumber} != 5", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} > 0", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myNumber} > 10", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} >= 5", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myNumber} < 10", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myNumber} < 0", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} <= 5", vip, cc), is(true));

            assertThat(parseAndEvaluateCondition("${myBoolean} == true", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myBoolean} == false", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} != true", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} != false", vip, cc), is(true));

            assertThat(parseAndEvaluateCondition("${myString} == \"hello\"", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myString} == \"hi\"", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} != \"hello\"", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} != \"hi\"", vip, cc), is(true));

            // Compare with variable
            assertThat(parseAndEvaluateCondition("${myNumber} == i", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myNumber} == x.i", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myBoolean} == b", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myBoolean} == x.y.b", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myString} == \"hello\"", vip, cc), is(true));
            assertThat(parseAndEvaluateCondition("${myString} == x.y.z.s", vip, cc), is(true));

            // Compare with other types
            assertThat(parseAndEvaluateCondition("${myNumber} == true", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} == \"hello\"", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} == 1", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} == \"hello\"", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} == 1", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} == true", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} == ${myBoolean}", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myNumber} == ${myString}", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} == ${myNumber}", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myBoolean} == ${myString}", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} == ${myNumber}", vip, cc), is(false));
            assertThat(parseAndEvaluateCondition("${myString} == ${myBoolean}", vip, cc), is(false));
        } finally {
            ConditionFactory.clearPlaceholderValueProviders();
        }
    }

    @Test
    public void conditionsShouldBeAbleToComposed() {
        Object[] objectVars = new Object[]{5, true, "hello", new X()};
        ConditionContext cc = new ConditionContext(objectVars);
        Map<String, VariableInfo> vim = new HashMap<String, VariableInfo>() {{
            put("i", new VariableInfo(int.class, 0));
            put("b", new VariableInfo(boolean.class, 1));
            put("s", new VariableInfo(String.class, 2));
            put("x", new VariableInfo(X.class, 3));
        }};
        VariableInfoProvider vip = variableName -> vim.get(variableName);

        assertThat(parseAndEvaluateCondition("i == 5 AND b == true", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == 5 && b == true", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == 5 AND b == false", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i == 5 && b == false", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i == 5 AND b == true AND s == \"hello\"", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == 5 && b == true && s == \"hello\"", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == 5 AND b == true AND s == \"hi\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i == 5 && b == true && s == \"hi\"", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("i == 5 AND b == true AND s == \"hi\" OR x.i > 0", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("i == 5 && b == true && s == \"hi\" || x.i > 0", vip, cc), is(true));

        assertThat(parseAndEvaluateCondition("(i == 5 AND b == true) AND (s == \"hello\" AND x.y.b == true)", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("(i == 5 && b == true) && (s == \"hello\" && x.y.b == true)", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("(i == 5 AND b == true) AND (s == \"hello\" AND x.y.b == false)", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("(i == 5 && b == true) && (s == \"hello\" && x.y.b == false)", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("(i == 5 AND b == true) OR (s == \"hello\" AND x.y.z.s == \"hi\")", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("(i == 5 && b == true) || (s == \"hello\" && x.y.z.s == \"hi\")", vip, cc), is(true));
        assertThat(parseAndEvaluateCondition("(i == 5 AND b == false) OR (s == \"hello\" AND x.y.z.s == \"hi\")", vip, cc), is(false));
        assertThat(parseAndEvaluateCondition("(i == 5 && b == false) || (s == \"hello\" && x.y.z.s == \"hi\")", vip, cc), is(false));
    }

    private static class X {
        int i = 5;
        Y y = new Y();
    }

    private static class Y {
        boolean b = true;
        Z z = new Z();
    }

    private static class Z {
        String s = "hello";
    }

}
