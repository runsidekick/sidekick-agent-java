package com.runsidekick.agent.probe.condition;

import com.runsidekick.agent.probe.condition.value.ConstantValueProvider;
import com.runsidekick.agent.probe.condition.value.PlaceholderValueProvider;
import com.runsidekick.agent.probe.condition.operand.BooleanOperand;
import com.runsidekick.agent.probe.condition.operand.CharacterOperand;
import com.runsidekick.agent.probe.condition.operand.NullOperand;
import com.runsidekick.agent.probe.condition.operand.NumberOperand;
import com.runsidekick.agent.probe.condition.operand.Operand;
import com.runsidekick.agent.probe.condition.operand.PlaceholderOperand;
import com.runsidekick.agent.probe.condition.operand.StringOperand;
import com.runsidekick.agent.probe.condition.operand.VariableOperand;
import com.runsidekick.agent.probe.error.ProbeErrorCodes;
import com.runsidekick.agent.probe.condition.parser.ConditionBaseListener;
import com.runsidekick.agent.probe.condition.parser.ConditionLexer;
import com.runsidekick.agent.probe.condition.parser.ConditionParser;
import com.runsidekick.agent.broker.error.CodedException;
import com.runsidekick.agent.core.instance.InstanceDiscovery;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author serkan
 */
public final class ConditionFactory {

    private static final Map<String, PlaceholderValueProvider> placeHolderValueProviderMap =
            InstanceDiscovery.instancesOf(PlaceholderValueProvider.class).
                    stream().
                    filter(p -> p.isEnabled()).
                    collect(Collectors.toMap(
                            PlaceholderValueProvider::getPlaceholderName,
                            Function.identity()));

    private ConditionFactory() {
    }

    public static void registerPlaceholderValueProvider(PlaceholderValueProvider placeholderValueProvider) {
        placeHolderValueProviderMap.put(placeholderValueProvider.getPlaceholderName(), placeholderValueProvider);
    }

    public static void deregisterPlaceholderValueProvider(String placeholderName) {
        placeHolderValueProviderMap.remove(placeholderName);
    }

    public static void deregisterPlaceholderValueProvider(PlaceholderValueProvider placeholderValueProvider) {
        placeHolderValueProviderMap.remove(placeholderValueProvider.getPlaceholderName(), placeholderValueProvider);
    }

    public static void clearPlaceholderValueProviders() {
        placeHolderValueProviderMap.clear();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Condition createConditionFromExpression(String conditionExpression,
                                                          VariableInfoProvider variableInfoProvider) {
        return createConditionFromExpressionByANTLR(conditionExpression, variableInfoProvider);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Condition createConditionFromExpressionByANTLR(String conditionExpression,
                                                                  VariableInfoProvider variableInfoProvider) {
        ANTLRErrorListener syntaxErrorFilingErrorListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                    int charPositionInLine, String msg, RecognitionException e) {
                throw new CodedException(ProbeErrorCodes.CONDITION_EXPRESSION_SYNTAX_CHECK_FAILED,
                        conditionExpression, msg);
            }
        };
        ConditionLexer lexer = new ConditionLexer(new ANTLRInputStream(conditionExpression));
        ConditionParser parser = new ConditionParser(new CommonTokenStream(lexer));
        parser.addErrorListener(syntaxErrorFilingErrorListener);
        ParseTreeWalker walker = new ParseTreeWalker();
        ConditionExpressionVisitor conditionExpressionVisitor = new ConditionExpressionVisitor(variableInfoProvider);
        walker.walk(conditionExpressionVisitor, parser.parse());
        return conditionExpressionVisitor.build();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Operand createNullOperand(String nullOperandExpression) {
        return new NullOperand();
    }

    private static Operand createBooleanOperand(String booleanOperandExpression) {
        return new BooleanOperand(new ConstantValueProvider<>(Boolean.parseBoolean(booleanOperandExpression)));
    }

    private static Operand createCharacterOperand(String characterOperandExpression) {
        // Skip 0th (start quote: ') and 2nd (end quote: ') characters
        return new CharacterOperand(new ConstantValueProvider<>(characterOperandExpression.charAt(1)));
    }

    private static Operand createVariableOperand(String variableOperandExpression,
                                                 VariableInfoProvider variableInfoProvider) {
        int propStartIdx = variableOperandExpression.indexOf('.');
        String variableName = null;
        String propPath = null;
        if (propStartIdx > 0) {
            variableName = variableOperandExpression.substring(0, propStartIdx);
            propPath = variableOperandExpression.substring(propStartIdx + 1);
        } else {
            variableName = variableOperandExpression;
        }
        return new VariableOperand(variableName, variableInfoProvider, propPath);
    }

    private static Operand createStringOperand(String stringOperandExpression) {
        String strValue = stringOperandExpression.substring(1, stringOperandExpression.length() - 1);
        return new StringOperand(new ConstantValueProvider(strValue));
    }

    private static Operand createNumberOperand(String numberOperandExpression) {
        return new NumberOperand(new ConstantValueProvider<>(Double.parseDouble(numberOperandExpression)));
    }

    private static Operand createPlaceholderOperand(String placeHolderOperandExpression) {
        String placeholderName = placeHolderOperandExpression.trim();
        PlaceholderValueProvider placeholderValueProvider = placeHolderValueProviderMap.get(placeholderName);
        if (placeholderValueProvider == null) {
            throw new CodedException(
                    ProbeErrorCodes.UNKNOWN_PLACEHOLDER_FOR_CONDITION, placeholderName);
        }
        return new PlaceholderOperand(placeholderName, placeholderValueProvider);
    }

    private static ComparisonOperator createComparisonOperator(String comparisonOperatorExpression) {
        return ComparisonOperator.fromExpression(comparisonOperatorExpression);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private interface ConditionBuilder {

        Condition build();
        void add(ConditionBuilder conditionBuilder);
        void add(BinaryOperator binaryOperator);

    }

    private static class SingleConditionBuilder implements ConditionBuilder {

        private Operand leftOperand;
        private Operand rightOperand;
        private ComparisonOperator comparisonOperator;

        @Override
        public Condition build() {
            return new SingleCondition(leftOperand, rightOperand, comparisonOperator);
        }

        @Override
        public void add(ConditionBuilder conditionBuilder) {
            throw new UnsupportedOperationException("Single conditions cannot include other conditions");
        }

        @Override
        public void add(BinaryOperator binaryOperator) {
            throw new UnsupportedOperationException("Single conditions cannot have binary operators");
        }

    }

    private static class CompositeConditionBuilder implements ConditionBuilder {

        private List<ConditionBuilder> conditionBuilders = new ArrayList<>();
        private List<BinaryOperator> binaryOperators = new ArrayList<>();

        @Override
        public Condition build() {
            if (conditionBuilders.size() == 1) {
                return conditionBuilders.get(0).build();
            }
            List<Condition> conditions = new ArrayList<>(conditionBuilders.size());
            for (ConditionBuilder conditionBuilder : conditionBuilders) {
                conditions.add(conditionBuilder.build());
            }
            return new CompositeCondition(conditions, binaryOperators);
        }

        @Override
        public void add(ConditionBuilder conditionBuilder) {
            conditionBuilders.add(conditionBuilder);
        }

        @Override
        public void add(BinaryOperator binaryOperator) {
            binaryOperators.add(binaryOperator);
        }

    }

    private static class ConditionExpressionVisitor extends ConditionBaseListener {

        private final Stack<ConditionBuilder> conditionBuilderStack = new Stack<ConditionBuilder>() {{
            push(new CompositeConditionBuilder());
        }};
        private final VariableInfoProvider variableInfoProvider;

        private ConditionExpressionVisitor(VariableInfoProvider variableInfoProvider) {
            this.variableInfoProvider = variableInfoProvider;
        }

        @Override
        public void enterParenExpression(ConditionParser.ParenExpressionContext ctx) {
            conditionBuilderStack.push(new CompositeConditionBuilder());
        }

        @Override
        public void exitParenExpression(ConditionParser.ParenExpressionContext ctx) {
            ConditionBuilder conditionBuilder = conditionBuilderStack.pop();
            ConditionBuilder parentConditionBuilder = conditionBuilderStack.peek();
            if (parentConditionBuilder != null) {
                parentConditionBuilder.add(conditionBuilder);
            }
        }

        @Override
        public void enterBinary(ConditionParser.BinaryContext ctx) {
            ConditionBuilder activeConditionBuilder = conditionBuilderStack.peek();
            if (activeConditionBuilder != null) {
                if (ctx.AND() != null) {
                    activeConditionBuilder.add(BinaryOperator.AND);
                } else if (ctx.OR() != null) {
                    activeConditionBuilder.add(BinaryOperator.OR);
                } else {
                    throw new UnsupportedOperationException("Unsupported binary operator: " + ctx.getText());
                }
            } else {
                throw new IllegalStateException("There is no active condition to add binary operator: " + ctx.getText());
            }
        }

        @Override
        public void enterComparatorExpression(ConditionParser.ComparatorExpressionContext ctx) {
            conditionBuilderStack.push(new SingleConditionBuilder());
            SingleConditionBuilder conditionBuilder = (SingleConditionBuilder) conditionBuilderStack.peek();
            if (ctx.op.EQ() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.EQ;
            } else if (ctx.op.NE() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.NE;
            } else if (ctx.op.LT() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.LT;
            } else if (ctx.op.LE() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.LE;
            } else if (ctx.op.GT() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.GT;
            } else if (ctx.op.GE() != null) {
                conditionBuilder.comparisonOperator = ComparisonOperator.GE;
            } else {
                throw new UnsupportedOperationException("Unsupported comparison operator: " + ctx.getText());
            }
        }

        @Override
        public void exitComparatorExpression(ConditionParser.ComparatorExpressionContext ctx) {
            ConditionBuilder conditionBuilder = conditionBuilderStack.pop();
            ConditionBuilder parentConditionBuilder = conditionBuilderStack.peek();
            if (parentConditionBuilder != null) {
                parentConditionBuilder.add(conditionBuilder);
            } else {
                throw new IllegalStateException("There is no active condition to add sub-condition: " + ctx.getText());
            }
        }

        @Override
        public void enterOperand(ConditionParser.OperandContext ctx) {
            SingleConditionBuilder conditionBuilder = (SingleConditionBuilder) conditionBuilderStack.peek();
            Operand operand = null;
            if (ctx.BOOLEAN() != null) {
                operand = createBooleanOperand(ctx.getText());
            } else if (ctx.CHARACTER() != null) {
                operand = createCharacterOperand(ctx.getText());
            } else if (ctx.NUMBER() != null) {
                operand = createNumberOperand(ctx.getText());
            } else if (ctx.STRING() != null) {
                operand = createStringOperand(ctx.getText());
            } else if (ctx.VARIABLE() != null) {
                operand = createVariableOperand(ctx.getText(), variableInfoProvider);
            } else if (ctx.PLACEHOLDER() != null) {
                String placeholderExpr = ctx.getText();
                // Trim "${" from head and "}" from tail
                placeholderExpr = placeholderExpr.substring(2, placeholderExpr.length() - 1);
                operand = createPlaceholderOperand(placeholderExpr);
            } else if (ctx.NULL() != null) {
                operand = createNullOperand(ctx.getText());
            } else {
                throw new IllegalStateException("Unsupported operand: " + ctx.getText());
            }
            if (conditionBuilder.leftOperand == null) {
                conditionBuilder.leftOperand = operand;
            } else {
                conditionBuilder.rightOperand = operand;
            }
        }

        public Condition build() {
            ConditionBuilder conditionBuilder = conditionBuilderStack.peek();
            if (conditionBuilder != null) {
                return conditionBuilder.build();
            } else {
                throw new IllegalStateException("There is no built condition");
            }
        }

    }

}
