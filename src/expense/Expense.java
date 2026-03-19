package expense;

import expense.split.Split;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class Expense {

    private final String ExpenseId;
    private final String description;
    private final double ExpenseAmount;
    private final User paidByUser;
    private final ExpenseSplitType splitType;
    private final List<Split> splitDetails;

    public Expense(String expenseId, String description, double expenseAmount, User paidByUser, ExpenseSplitType splitType, List<Split> splitDetails) {
        ExpenseId = expenseId;
        this.description = description;
        ExpenseAmount = expenseAmount;
        this.paidByUser = paidByUser;
        this.splitType = splitType;
        this.splitDetails = splitDetails;
    }

    public String getExpenseId() {
        return ExpenseId;
    }

    public String getDescription() {
        return description;
    }

    public double getExpenseAmount() {
        return ExpenseAmount;
    }

    public User getPaidByUser() {
        return paidByUser;
    }

    public ExpenseSplitType getSplitType() {
        return splitType;
    }

    public List<Split> getSplitDetails() {
        return splitDetails;
    }
}
