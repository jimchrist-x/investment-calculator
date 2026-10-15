package art.stalaverse.investment;
import art.stalaverse.investment.calculator.InvestmentPeriods;
import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.MonthlySnapshot;
import art.stalaverse.investment.model.Frequency;
public class App {
    public static void main(String[] args) {
        InvestmentData data = new InvestmentData(9790.47);
        data.addPeriod(4, 0.07, 200.0, Frequency.CONTINUOUS, false, true);
        data.addPeriod(4, 0.07, 650.0, Frequency.CONTINUOUS, false, true);
        data.addPeriod(4, 0.07, 887.5, Frequency.CONTINUOUS, false, true);
        data.addPeriod(4, 0.07, 1070.5, Frequency.CONTINUOUS, false, true);
        data.addPeriod(4, 0.07, 1183.0, Frequency.CONTINUOUS, false, true);
        data.addPeriod(25, 0.07, 1183.0, Frequency.CONTINUOUS, false, true);
        InvestmentPeriods portfolio = new InvestmentPeriods(data);
        InvestmentResult result = portfolio.calculate();
        System.out.printf("%-8s | %-15s | %-15s | %-15s | %-15s%n",
                "Month", "Start Balance", "Contribution", "Interest", "End Balance");
        System.out.println("-".repeat(78));
        for (MonthlySnapshot snapshot : result.monthlySnapshots()) {
            System.out.printf("%-8d | %,15.0f | %,15.0f | %,15.0f | %,15.0f%n", snapshot.monthIndex(), snapshot.startingBalance(), snapshot.contribution(), snapshot.interestEarned(), snapshot.endingBalance());
        }
        System.out.println("-".repeat(78));
        System.out.printf("Total Contributions: %,.0f%n", result.totalContributions());
        System.out.printf("Total Interest:      %,.0f%n", result.totalInterestEarned());
        System.out.printf("Final Balance:       %,.0f%n", result.finalBalance());
    }
}
