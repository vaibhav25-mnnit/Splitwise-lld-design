import expense.split.*;
import expense.split.strategies.ExpenseSplit;
import expense.split.strategies.PercentageExpenseSplit;
import user.User;
import user.UserController;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("This is SplitWise LLD!!!");

//        ExpenseSplit expenseSplit = new EqualExpenseSplit();
//        ExpenseSplit expenseSplit = new UnequalExpenseSplit();
          ExpenseSplit expenseSplit = new PercentageExpenseSplit();

        List<Split> splitList = new ArrayList<>();
        User u1 = new User("vb","vaibhav");
        User u2 = new User("sm","Sumit") ;
        User u3 = new User("ab","Atish");

        UserController.createUser(u1);
        UserController.createUser(u2);
        UserController.createUser(u3);

        splitList.add(new PercentageSplit(u1,80));
        splitList.add(new PercentageSplit(u2,20));
        splitList.add(new PercentageSplit(u3,0));

        expenseSplit.validateSplitRequest(splitList, 1000);

        splitList.forEach(split -> System.out.println( split.getUser().getUserName()+" "+ split.getAmount()));

    }
}