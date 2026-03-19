package expense.split.strategies;

import expense.split.PercentageSplit;
import expense.split.Split;

import java.util.List;

public class PercentageExpenseSplit implements ExpenseSplit {
    @Override
    public void validateSplitRequest(List<Split> splitList, double totalAmount) {
        System.out.println("Validation using percentage expense split");

        double percentageSum = splitList.stream()
                .map(split -> (PercentageSplit) split)
                .mapToDouble(PercentageSplit::getPercentage)
                .sum();

        if (Math.abs(percentageSum - 100.0)> 0.01)
            throw new IllegalArgumentException(
                    "Sum of percentage is "+ percentageSum
            );

        for(Split split: splitList)
        {
            PercentageSplit percentageSplit = (PercentageSplit) split;
            double amount  = totalAmount*(percentageSplit.getPercentage()/100.0);
            split.setAmount(amount);
        }

        System.out.println("Percentage split validated.");
    }
}
