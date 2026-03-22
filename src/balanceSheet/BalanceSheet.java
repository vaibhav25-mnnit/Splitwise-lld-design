package balanceSheet;

import user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceSheet {
    private final Map<String, Double> balanceSheet;
    public BalanceSheet() {
        this.balanceSheet = new HashMap<>();
    }

    public void updateBalance(String userId, double amount)
    {
        balanceSheet.merge(userId, amount, Double::sum);
    }

    public double getBalance(String userId)
    {
        return balanceSheet.getOrDefault(userId, 0.00);
    }

    public Map<String, Double> getBalanceSheet() {
        return balanceSheet;
    }
}
