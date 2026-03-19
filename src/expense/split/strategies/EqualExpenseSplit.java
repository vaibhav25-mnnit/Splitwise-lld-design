package expense.split.strategies;

import expense.split.Split;

import java.util.List;

public class EqualExpenseSplit implements ExpenseSplit {
    @Override
    public void validateSplitRequest(List<Split> splitList, double totalAmount) {
        System.out.println("No validation is required as this is a equal expense.split");

        double splitAmount = Math.round((totalAmount/ splitList.size())*100.0) / 100.0;

        splitList.forEach(split ->  split.setAmount(splitAmount));
    }
}
