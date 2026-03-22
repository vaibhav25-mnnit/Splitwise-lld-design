package group;

import expense.Expense;
import expense.ExpenseController;
import expense.ExpenseSplitType;
import expense.split.Split;
import user.User;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String groupId;
    private  final String groupName;
    private final List<User> members;
    private  final List<Expense> expenses;
    private final ExpenseController expenseController;

    public Group(String groupId, String groupName,
                 ExpenseController expenseController) {
        this.groupId           = groupId;
        this.groupName         = groupName;
        this.expenseController = expenseController;
        this.members           = new ArrayList<>();
        this.expenses          = new ArrayList<>();
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void createExpense(String expenseId, String description,
                              User paidByUser, ExpenseSplitType splitType,
                              double expenseAmount, List<Split> splitDetails) {

        Expense expense = expenseController.createExpense(
                expenseId, description,
                paidByUser, splitType,
                expenseAmount, splitDetails
        );
        expenses.add(expense);
    }
}
