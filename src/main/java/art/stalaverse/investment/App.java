package art.stalaverse.investment;

import art.stalaverse.investment.calculator.InvestmentPeriods;
import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.MonthlySnapshot;
import art.stalaverse.investment.model.Timing;
import art.stalaverse.investment.model.YearlySnapshot;

import java.util.Locale;

public final class App {
  private static final int LABEL_WIDTH = 8;
  private static final int AMOUNT_WIDTH = 15;
  private static final String COLUMN_SEPARATOR = " | ";
  /** Total width of a table row, so the dashed rule matches the data it underlines. */
  private static final int ROW_WIDTH = LABEL_WIDTH + 4 * (COLUMN_SEPARATOR.length() + AMOUNT_WIDTH);

  private App() {
  }

  public static void main(String[] args) {
    InvestmentData data = new InvestmentData(9790.47);
    data.addPeriod(4, 0.07, 200.0, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(4, 0.07, 650.0, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(4, 0.07, 887.5, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(4, 0.07, 1070.5, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(4, 0.07, 1183.0, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(25, 0.07, 1183.0, CompoundingFrequency.CONTINUOUS, ContributionFrequency.MONTHLY, Timing.END);

    InvestmentPeriods portfolio = new InvestmentPeriods(data);
    InvestmentResult result = portfolio.calculate();
    printYearlyReport(result);
    System.out.println();
    printMonthlyReport(result);
    printSummary(result);
  }

  private static void printYearlyReport(InvestmentResult result) {
    printHeader("Year", "Start Balance", "Contributions", "Interest", "End Balance");
    for (YearlySnapshot year : result.yearlySnapshots()) {
      printRow(String.valueOf(year.yearIndex()),
          year.startingBalance(), year.totalContributions(), year.totalInterestEarned(), year.endingBalance());
    }
    printRule();
  }

  private static void printMonthlyReport(InvestmentResult result) {
    printHeader("Month", "Start Balance", "Contribution", "Interest", "End Balance");
    for (MonthlySnapshot month : result.monthlySnapshots()) {
      printRow(String.valueOf(month.monthIndex()),
          month.startingBalance(), month.contribution(), month.interestEarned(), month.endingBalance());
    }
    printRule();
  }

  private static void printSummary(InvestmentResult result) {
    System.out.printf(Locale.US, "Total Contributions: %,.2f%n", result.totalContributions());
    System.out.printf(Locale.US, "Total Interest:      %,.2f%n", result.totalInterestEarned());
    System.out.printf(Locale.US, "Final Balance:       %,.2f%n", result.finalBalance());
  }

  private static void printHeader(String label, String... columnTitles) {
    StringBuilder header = new StringBuilder(String.format(Locale.US, "%-" + LABEL_WIDTH + "s", label));
    for (String title : columnTitles) {
      // Right-aligned so the titles sit on top of the right-aligned amounts below them.
      header.append(COLUMN_SEPARATOR).append(String.format(Locale.US, "%" + AMOUNT_WIDTH + "s", title));
    }
    System.out.println(header);
    printRule();
  }

  private static void printRow(String label, double... amounts) {
    StringBuilder row = new StringBuilder(String.format(Locale.US, "%-" + LABEL_WIDTH + "s", label));
    for (double amount : amounts) {
      row.append(COLUMN_SEPARATOR).append(String.format(Locale.US, "%," + AMOUNT_WIDTH + ".2f", amount));
    }
    System.out.println(row);
  }

  private static void printRule() {
    System.out.println("-".repeat(ROW_WIDTH));
  }
}
