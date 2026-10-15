package art.stalaverse.investment.model;
import java.util.List;
public record InvestmentResult(double finalBalance, double totalContributions, double totalInterestEarned, List<MonthlySnapshot> monthlySnapshots) {}
