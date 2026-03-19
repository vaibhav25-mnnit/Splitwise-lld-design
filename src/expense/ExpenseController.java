package expense;

import balanceSheet.BalanceSheetController;
import expense.split.strategies.ExpenseSplit;
import expense.split.Split;
import expense.split.SplitFactory;
import user.User;

import java.util.List;

public class ExpenseController {
    BalanceSheetController  balanceSheetController;

    public ExpenseController() {
        balanceSheetController = new BalanceSheetController();
    }

    public Expense createExpense(String expenseId, String description, User paidByUser, ExpenseSplitType splitType,double expenseAmount, List<Split> splitDetails)
    {
        ExpenseSplit expenseSplit =  SplitFactory.createSplit(splitType);
        Expense expense = new Expense(expenseId, description, expenseAmount, paidByUser, splitType, splitDetails);

        balanceSheetController.updateUserExpenseBalanceSheet(paidByUser, splitDetails, expenseAmount);
        return expense;
    }


}
