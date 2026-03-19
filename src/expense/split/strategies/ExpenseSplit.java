package expense.split.strategies;

import expense.split.Split;

import java.util.List;

public interface ExpenseSplit {
    void validateSplitRequest(List<Split> splitList, double totalAmount);
}
