import java.util.ArrayList;
import java.util.List;
public class InvestmentPeriods {
    private final InvestmentData data;
    public InvestmentPeriods(InvestmentData data) {
        this.data = data;
    }
    public InvestmentResult calculate() {
        double currentAmount = data.getStartingAmount();
        double cumulativeContributions = 0.0;
        double cumulativeInterest = 0.0;
        List<MonthlySnapshot> allSnapshots = new ArrayList<>();
        for (int i = 0; i < data.getPeriodCount(); i++) {
            InvestmentPeriod period = data.getPeriod(i);
            Investment investment = new Investment(currentAmount, period.getYears(), period.getRate(), period.getFrequency(), period.getContribution(), period.isContributionAtBeginning(), period.isMonthlyContribution());
            InvestmentResult periodResult = investment.calculate(allSnapshots.size());
            currentAmount = periodResult.finalBalance();
            cumulativeContributions += periodResult.totalContributions();
            cumulativeInterest += periodResult.totalInterestEarned();
            allSnapshots.addAll(periodResult.monthlySnapshots());
        }
        return new InvestmentResult(
            currentAmount,
            cumulativeContributions,
            cumulativeInterest,
            allSnapshots
        );
    }
}
