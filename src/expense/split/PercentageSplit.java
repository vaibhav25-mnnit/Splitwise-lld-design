package expense.split;

import user.User;

import java.util.List;

public class PercentageSplit extends Split {
    private final double percentage;

    public PercentageSplit(User user, double percentage) {
        super(user, 0);
        this.percentage = percentage;
    }

    public double getPercentage(){
        return percentage;
    }
}
