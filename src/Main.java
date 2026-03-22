import balanceSheet.BalanceSheetController;
import expense.ExpenseController;
import expense.ExpenseSplitType;
import expense.split.*;
import expense.split.strategies.ExpenseSplit;
import expense.split.strategies.PercentageExpenseSplit;
import group.Group;
import user.User;
import user.UserController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("This is SplitWise LLD!!!");

        //1. Central split graph
        BalanceSheetController balanceSheetController = new BalanceSheetController();


        //2.Creating Expense controller
        ExpenseController expenseController = new ExpenseController(balanceSheetController);


        //Creating Users

        User tony = new User("Iron Man","Tony Stark");
        User steve = new User("Captain  America","Steve Roggers");
        User bruce = new User("HULK", "Bruce Banner");
        User thor = new User("Thor","Thor");
        User natasha = new User("Black Widow", "Natasha romanoff");
        User clint = new User("Hawkeye", "Client Barton");

        //add nodes in central graph
        balanceSheetController.initUser(tony);
        balanceSheetController.initUser(steve);
        balanceSheetController.initUser(bruce);
        balanceSheetController.initUser(thor);
        balanceSheetController.initUser(natasha);
        balanceSheetController.initUser(clint);

        // User directory for balance display
        Map<String, User> userDirectory = new HashMap<>();
        userDirectory.put("Iron Man", tony);
        userDirectory.put("Captain  America", steve);
        userDirectory.put("HULK",  bruce);
        userDirectory.put("Thor", thor);
        userDirectory.put("Black Widow", natasha);
        userDirectory.put("Hawkeye", clint);

        //Create Group for expense
        Group avengers = new Group("A6","Avengers central expenses", expenseController);

        //adding members to group
        avengers.addMember(tony);
        avengers.addMember(steve);
        avengers.addMember(bruce);
        avengers.addMember(thor);
        avengers.addMember(natasha);
        avengers.addMember(clint);

        // ─────────────────────────────────────────
        // Expense 1: EQUAL SPLIT
        // Tony pays ₹12000 for Shawarma Party
        // Split equally among all 6 Avengers
        // Each share = 12000/6 = ₹2000
        // ─────────────────────────────────────────
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌮 Expense 1: Shawarma Party — EQUAL SPLIT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<Split> equalSplits = new ArrayList<>();
        equalSplits.add(new Split(tony,    0));  // amount=0, computed by strategy
        equalSplits.add(new Split(steve,   0));
        equalSplits.add(new Split(bruce,   0));
        equalSplits.add(new Split(thor,    0));
        equalSplits.add(new Split(natasha, 0));
        equalSplits.add(new Split(clint,   0));

        avengers.createExpense("e1", "Shawarma Party after Battle",
                tony, ExpenseSplitType.EQUAL,
                12000, equalSplits);

        // ─────────────────────────────────────────
        // Expense 2: UNEQUAL SPLIT
        // Steve pays ₹10000 for Quinjet Fuel
        // Each person pays based on usage
        // Must sum to 10000!
        // ─────────────────────────────────────────
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✈️  Expense 2: Quinjet Fuel — UNEQUAL SPLIT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<Split> unequalSplits = new ArrayList<>();
        unequalSplits.add(new Split(tony,    3000));
        unequalSplits.add(new Split(steve,   2000));
        unequalSplits.add(new Split(bruce,   2000));
        unequalSplits.add(new Split(thor,    1000));
        unequalSplits.add(new Split(natasha, 1000));
        unequalSplits.add(new Split(clint,   1000));
        // 3000+2000+2000+1000+1000+1000 = 10000 ✅

        avengers.createExpense("e2", "Quinjet Fuel",
                steve, ExpenseSplitType.UNEQUAL,
                10000, unequalSplits);

        // ─────────────────────────────────────────
        // Expense 3: PERCENTAGE SPLIT
        // Bruce pays ₹6000 for Lab Equipment
        // Split by percentage based on usage
        // Must sum to 100!
        // ─────────────────────────────────────────
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔬 Expense 3: Lab Equipment — PERCENTAGE SPLIT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        List<Split> percentageSplits = new ArrayList<>();
        percentageSplits.add(new PercentageSplit(tony,    30)); // 30% = ₹1800
        percentageSplits.add(new PercentageSplit(steve,   20)); // 20% = ₹1200
        percentageSplits.add(new PercentageSplit(bruce,   20)); // 20% = ₹1200 (skipped, he paid)
        percentageSplits.add(new PercentageSplit(thor,    10)); // 10% = ₹600
        percentageSplits.add(new PercentageSplit(natasha, 10)); // 10% = ₹600
        percentageSplits.add(new PercentageSplit(clint,   10)); // 10% = ₹600
        // 30+20+20+10+10+10 = 100 ✅

        avengers.createExpense("e3", "Lab Equipment",
                bruce, ExpenseSplitType.PERCENTAGE,
                6000, percentageSplits);

        // ─────────────────────────────────────────
        // Show Individual Balances
        // ─────────────────────────────────────────
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 INDIVIDUAL BALANCES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        balanceSheetController.showBalance(tony,    userDirectory);
        balanceSheetController.showBalance(steve,   userDirectory);
        balanceSheetController.showBalance(bruce,   userDirectory);
        balanceSheetController.showBalance(thor,    userDirectory);
        balanceSheetController.showBalance(natasha, userDirectory);
        balanceSheetController.showBalance(clint,   userDirectory);

        // ─────────────────────────────────────────
        // Simplify Debts
        // ─────────────────────────────────────────
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 SIMPLIFIED TRANSACTIONS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        balanceSheetController.simplifyDebts(userDirectory);

        // ─────────────────────────────────────────
        // Settle Up — Thor pays Tony ₹2000
        // ─────────────────────────────────────────
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💸 SETTLEMENT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        balanceSheetController.settleUp(thor, tony, 2000);

        // Show Thor and Tony balance after settlement
        balanceSheetController.showBalance(thor, userDirectory);
        balanceSheetController.showBalance(tony, userDirectory);






    }
}