package art.stalaverse.investment.model;
import java.util.List;
import java.util.ArrayList;
public record InvestmentResult(double finalBalance, double totalContributions, double totalInterestEarned, List<MonthlySnapshot> monthlySnapshots) {
  public List<YearlySnapshot> yearlySnapshots() {
    int totalMonths = monthlySnapshots.size();
    int estimatedYears = (totalMonths + 11) / 12;
    List<YearlySnapshot> yearlyList = new ArrayList<>(estimatedYears);
    for (int i=0;i<totalMonths;i+=12) {
      int endMonthIndex = Math.min(i+12, totalMonths);
      List<MonthlySnapshot> yearSlice = monthlySnapshots.subList(i, endMonthIndex);
      double yearStartBalance = yearSlice.get(0).startingBalance();
      double yearEndBalance = yearSlice.get(yearSlice.size()-1).endingBalance();
      double yearContributions = 0.0;
      double yearInterest = 0.0;

      for (MonthlySnapshot month : yearSlice) {
        yearContributions += month.contribution();
        yearInterest += month.interestEarned();
      }
      yearlyList.add(new YearlySnapshot((i / 12) + 1, yearStartBalance, yearContributions, yearInterest, yearEndBalance));
    }
    return yearlyList;
  }
}
