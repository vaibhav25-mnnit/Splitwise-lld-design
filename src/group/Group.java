package group;

import expense.Expense;
import expense.ExpenseController;
import user.User;

import java.util.List;

public class Group {
    String id;
    List<User> groupMembers;
    List<Expense> expenseList;
    ExpenseController expenseController;

}
