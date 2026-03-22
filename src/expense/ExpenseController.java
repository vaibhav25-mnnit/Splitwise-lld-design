package expense;

import balanceSheet.BalanceSheetController;
import expense.split.strategies.ExpenseSplit;
import expense.split.Split;
import expense.split.SplitFactory;
import user.User;

import java.util.List;

public class ExpenseController {
    private  final BalanceSheetController  balanceSheetController;

    public ExpenseController(BalanceSheetController balanceSheetController) {
        this.balanceSheetController = balanceSheetController;
    }

    public Expense createExpense(String expenseId, String description, User paidByUser, ExpenseSplitType splitType,double expenseAmount, List<Split> splitDetails)
    {
        //Step 1 create split using split factory
        ExpenseSplit expenseSplit =  SplitFactory.createSplit(splitType);


        //validate split
        expenseSplit.validateSplitRequest(splitDetails, expenseAmount);

        //create expense amount
        Expense expense = new Expense(expenseId, description, expenseAmount, paidByUser, splitType, splitDetails);

        //update balance sheet
       balanceSheetController.updateBalanceSheet(paidByUser, splitDetails);
        return expense;
    }


}
