package expense.split;

import expense.ExpenseSplitType;
import expense.split.strategies.EqualExpenseSplit;
import expense.split.strategies.ExpenseSplit;
import expense.split.strategies.PercentageExpenseSplit;
import expense.split.strategies.UnequalExpenseSplit;

public class SplitFactory {

    public static ExpenseSplit createSplit(ExpenseSplitType splitType) {
        switch (splitType) {
            case EQUAL:
                return new EqualExpenseSplit();
            case UNEQUAL:
                return new UnequalExpenseSplit();
            case PERCENTAGE:
                return new PercentageExpenseSplit();
            default:
                throw new IllegalArgumentException("Unknown split type: " + splitType);
        }
    }

}
