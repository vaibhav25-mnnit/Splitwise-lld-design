package balanceSheet;

import expense.split.Split;
import user.User;

import java.util.*;

/*
    This is a central graph which where balance sheet is it's node
 */
public class BalanceSheetController {

    private final Map<String,BalanceSheet> balanceSheetMap;

    public BalanceSheetController() {
        this.balanceSheetMap = new HashMap<>();
    }

    public void initUser(String userId)
    {
        balanceSheetMap.put(userId, new BalanceSheet());
    }

    public void updateBalanceSheet(User paidBy, List<Split> splits) {
        for (Split split : splits) {
            if (split.getUser().getUserId().equals(paidBy.getUserId())) continue;

            User owingUser = split.getUser();
            double amount = split.getAmount();


            // Edge 1: paidBy → owingUser
            // owingUser owes paidBy → positive
            balanceSheetMap.get(paidBy.getUserId())
                    .updateBalance(owingUser.getUserId(), +amount);

            // Edge 2: owingUser → paidBy
            // owingUser owes paidBy → negative
            balanceSheetMap.get(owingUser.getUserId())
                    .updateBalance(paidBy.getUserId(), -amount);
        }
    }

    public void showBalance(User user, Map<String, User> userDirectory) {

        // Get this user's edge list from the graph
        BalanceSheet balanceSheet = balanceSheetMap.get(user.getUserId());

        System.out.println("\n📊 Balance for " + user.getUserName() + ":");

        boolean isSettled = true;

        for (Map.Entry<String, Double> entry : balanceSheet.getBalanceSheet().entrySet()) {

            String otherUserId = entry.getKey();
            double amount      = entry.getValue();

            // Skip near zero → already settled
            if (Math.abs(amount) < 0.01) continue;

            isSettled = false;

            // Get other user's name from directory
            String otherUserName = userDirectory.get(otherUserId).getUserName();

            if (amount > 0) {
                // positive → other person owes YOU
                System.out.println("  " + otherUserName +
                        " owes you ₹" + amount);
            } else {
                // negative → YOU owe other person
                System.out.println("  You owe " + otherUserName +
                        " ₹" + Math.abs(amount));
            }
        }

        if (isSettled) System.out.println("  All settled up! ✅");
    }

    public void settleUp(User payer, User payee, double amount) {

        // Edge 1: payer → payee
        // payer is paying → their negative balance reduces
        // i.e. debt to payee decreases
        balanceSheetMap.get(payer.getUserId())
                .updateBalance(payee.getUserId(), +amount);

        // Edge 2: payee → payer
        // payee is receiving → their positive balance reduces
        // i.e. amount owed by payer decreases
        balanceSheetMap.get(payee.getUserId())
                .updateBalance(payer.getUserId(), -amount);

        System.out.println("\n💸 " + payer.getUserName() +
                " paid ₹" + amount +
                " to " + payee.getUserName());
    }

    public void simplifyDebts(Map<String, User> userDirectory) {
        System.out.println("\n🔄 SIMPLIFIED TRANSACTIONS:");

        // Step 1: compute net per node
        Map<String, Double> netBalanceMap = new HashMap<>();
        for (Map.Entry<String, BalanceSheet> entry : balanceSheetMap.entrySet()) {
            double net = entry.getValue()
                    .getBalanceSheet()
                    .values()
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            netBalanceMap.put(entry.getKey(), net);
        }

        // Step 2: separate into heaps
        List<String> userIds = new ArrayList<>(netBalanceMap.keySet());

        PriorityQueue<double[]> creditors = new PriorityQueue<>(
                (a, b) -> Double.compare(b[1], a[1])
        );
        PriorityQueue<double[]> debtors = new PriorityQueue<>(
                (a, b) -> Double.compare(b[1], a[1])
        );

        for (int i = 0; i < userIds.size(); i++) {
            double net = netBalanceMap.get(userIds.get(i));
            if (net > 0.01)       creditors.offer(new double[]{i, net});
            else if (net < -0.01) debtors.offer(new double[]{i, -net});
        }

        // Step 3: greedy matching
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            double[] creditor = creditors.poll();
            double[] debtor   = debtors.poll();

            double amount = Math.min(creditor[1], debtor[1]);
            amount = Math.round(amount * 100.0) / 100.0;

            String creditorName = userDirectory.get(userIds.get((int) creditor[0])).getUserName();
            String debtorName   = userDirectory.get(userIds.get((int) debtor[0])).getUserName();

            System.out.println("  " + debtorName +
                    " →₹" + amount +
                    "→ " + creditorName);

            if (creditor[1] - amount > 0.01)
                creditors.offer(new double[]{creditor[0], creditor[1] - amount});

            if (debtor[1] - amount > 0.01)
                debtors.offer(new double[]{debtor[0], debtor[1] - amount});
        }
    }

}
