package expense.split.strategies;

import expense.split.Split;

import java.util.List;

public class UnequalExpenseSplit implements ExpenseSplit {
    @Override
    public void validateSplitRequest(List<Split> splitList, double totalAmount) {
        System.out.println("validating unequal expense split");
        double sum = splitList.stream()
                                .mapToDouble(Split::getAmount)
                                .sum();

        if (Math.abs(sum - totalAmount) > 0.01) throw new IllegalArgumentException(
                "Split amounts " + sum + " do not match total amount " + totalAmount
        );

        System.out.println("Unequal split validated. Total: ₹" + sum);
    }
}
