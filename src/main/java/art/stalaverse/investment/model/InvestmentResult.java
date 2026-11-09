package art.stalaverse.investment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a projection.
 *
 * <p>The monthly snapshots are copied defensively on construction and never handed out as a mutable
 * list, so the summary figures ({@link #finalBalance()}, {@link #totalContributions()},
 * {@link #totalInterestEarned()}) always describe the snapshots this instance holds.
 *
 * <p>The yearly view is derived once, in the constructor, and cached: {@link #yearlySnapshots()}
 * is O(1) and returns the same immutable list on every call, which makes it safe to call from a
 * repaint or refresh loop.
 */
public final class InvestmentResult {
  private static final int MONTHS_PER_YEAR = 12;

  private final double finalBalance;
  private final double totalContributions;
  private final double totalInterestEarned;
  private final List<MonthlySnapshot> monthlySnapshots;
  private final List<YearlySnapshot> yearlySnapshots;

  /**
   * @param finalBalance        balance at the end of the projection
   * @param totalContributions  sum of every contribution paid in
   * @param totalInterestEarned sum of every interest credit
   * @param monthlySnapshots    one snapshot per month, in chronological order; copied defensively
   */
  public InvestmentResult(double finalBalance, double totalContributions, double totalInterestEarned,
                          List<MonthlySnapshot> monthlySnapshots) {
    this.finalBalance = finalBalance;
    this.totalContributions = totalContributions;
    this.totalInterestEarned = totalInterestEarned;
    this.monthlySnapshots = List.copyOf(Objects.requireNonNull(monthlySnapshots, "monthlySnapshots"));
    this.yearlySnapshots = buildYearlySnapshots(this.monthlySnapshots);
  }

  public double finalBalance() {
    return finalBalance;
  }

  public double totalContributions() {
    return totalContributions;
  }

  public double totalInterestEarned() {
    return totalInterestEarned;
  }

  /** Immutable, chronologically ordered monthly snapshots. */
  public List<MonthlySnapshot> monthlySnapshots() {
    return monthlySnapshots;
  }

  /** Immutable yearly summary, computed once and cached. */
  public List<YearlySnapshot> yearlySnapshots() {
    return yearlySnapshots;
  }

  private static List<YearlySnapshot> buildYearlySnapshots(List<MonthlySnapshot> months) {
    int totalMonths = months.size();
    if (totalMonths == 0) {
      return List.of();
    }
    List<YearlySnapshot> yearly = new ArrayList<>((totalMonths + MONTHS_PER_YEAR - 1) / MONTHS_PER_YEAR);
    for (int start = 0; start < totalMonths; start += MONTHS_PER_YEAR) {
      int end = Math.min(start + MONTHS_PER_YEAR, totalMonths);
      List<MonthlySnapshot> slice = months.subList(start, end);
      double yearContributions = 0.0;
      double yearInterest = 0.0;
      for (MonthlySnapshot month : slice) {
        yearContributions += month.contribution();
        yearInterest += month.interestEarned();
      }
      yearly.add(new YearlySnapshot(
          (start / MONTHS_PER_YEAR) + 1,
          slice.get(0).startingBalance(),
          yearContributions,
          yearInterest,
          slice.get(slice.size() - 1).endingBalance()));
    }
    return List.copyOf(yearly);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof InvestmentResult that)) {
      return false;
    }
    return Double.compare(finalBalance, that.finalBalance) == 0
        && Double.compare(totalContributions, that.totalContributions) == 0
        && Double.compare(totalInterestEarned, that.totalInterestEarned) == 0
        && monthlySnapshots.equals(that.monthlySnapshots)
        && yearlySnapshots.equals(that.yearlySnapshots);
  }

  @Override
  public int hashCode() {
    return Objects.hash(finalBalance, totalContributions, totalInterestEarned, monthlySnapshots, yearlySnapshots);
  }

  @Override
  public String toString() {
    return "InvestmentResult[finalBalance=" + finalBalance
        + ", totalContributions=" + totalContributions
        + ", totalInterestEarned=" + totalInterestEarned
        + ", months=" + monthlySnapshots.size()
        + ", years=" + yearlySnapshots.size() + "]";
  }
}
