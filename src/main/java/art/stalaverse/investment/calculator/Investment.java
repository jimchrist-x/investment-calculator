package art.stalaverse.investment.calculator;

import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentPeriod;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.MonthlySnapshot;
import art.stalaverse.investment.model.Timing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Projects a single period month by month.
 *
 * <p>Interest is credited only on the compounding dates of the period's
 * {@link CompoundingFrequency}; the money that earns a credit is the balance held since the previous
 * compounding date. Contributions paid in between therefore start earning at the next compounding
 * date, not the month after they are paid - that is what distinguishes annual from monthly
 * compounding once contributions are involved.
 *
 * <p>Each call re-anchors the compounding calendar at the start of the period.
 */
public final class Investment {
  private final double startingAmount;
  private final InvestmentPeriod period;

  /**
   * @param startingAmount money invested at the start of the period, must be finite and &gt;= 0
   * @param period         the validated period to project
   * @throws IllegalArgumentException if {@code startingAmount} is not finite or negative
   * @throws NullPointerException     if {@code period} is null
   */
  public Investment(double startingAmount, InvestmentPeriod period) {
    if (!Double.isFinite(startingAmount)) {
      throw new IllegalArgumentException("startingAmount must be finite, was " + startingAmount);
    }
    if (startingAmount < 0) {
      throw new IllegalArgumentException("startingAmount must be non-negative, was " + startingAmount);
    }
    this.startingAmount = startingAmount;
    this.period = Objects.requireNonNull(period, "period");
  }

  /**
   * Convenience constructor that validates the period values for you.
   *
   * @see InvestmentPeriod#InvestmentPeriod(int, double, double, CompoundingFrequency, ContributionFrequency, Timing)
   */
  public Investment(double startingAmount, int years, double rate, CompoundingFrequency compoundingFrequency,
                    double contribution, ContributionFrequency contributionFrequency, Timing timing) {
    this(startingAmount, new InvestmentPeriod(years, rate, contribution, compoundingFrequency,
        contributionFrequency, timing));
  }

  /**
   * Projects the period.
   *
   * @param startingMonthOffset number of months already projected before this period, used to number
   *                            the snapshots; must be &gt;= 0
   * @return an immutable result with one snapshot per month
   * @throws IllegalArgumentException if {@code startingMonthOffset} is negative
   */
  public InvestmentResult calculate(int startingMonthOffset) {
    if (startingMonthOffset < 0) {
      throw new IllegalArgumentException("startingMonthOffset must not be negative, was " + startingMonthOffset);
    }
    double rate = period.getRate();
    double contribution = period.getContribution();
    ContributionFrequency contributionFrequency = period.getContributionFrequency();
    Timing timing = period.getTiming();
    CompoundingFrequency compoundingFrequency = period.getCompoundingFrequency();

    int months = period.getMonths();
    double ratePerPeriod = compoundingFrequency.interestRatePerPeriod(rate);
    if (!Double.isFinite(ratePerPeriod)) {
      // Cannot happen with a validated rate, kept as a tripwire so a future change cannot leak NaN again.
      throw new IllegalStateException(
          "Derived interest rate is not finite (" + ratePerPeriod + ") for rate " + rate
              + " and compounding " + compoundingFrequency);
    }
    int monthsPerPeriod = compoundingFrequency.monthsPerPeriod();

    double balance = startingAmount;
    // Money that has been sitting in the plan since the last compounding date and therefore earns
    // the next interest credit. Contributions paid in between are not part of it.
    double interestBase = startingAmount;
    double totalContributions = 0.0;
    double totalInterest = 0.0;
    List<MonthlySnapshot> snapshots = new ArrayList<>(months);

    for (int month = 0; month < months; month++) {
      double monthStartBalance = balance;
      double monthContribution = 0.0;
      boolean isCompoundingDate = (month + 1) % monthsPerPeriod == 0;

      if (timing == Timing.BEGINNING && contributionFrequency.isContributionMonth(month)) {
        balance += contribution;
        monthContribution += contribution;
        if (month % monthsPerPeriod == 0) {
          // Paid exactly at the start of a compounding interval, so it earns this interval's credit.
          interestBase += contribution;
        }
      }

      double interestEarned = 0.0;
      if (isCompoundingDate) {
        interestEarned = interestBase * ratePerPeriod;
        balance += interestEarned;
        interestBase = balance;
      }

      if (timing == Timing.END && contributionFrequency.isContributionMonth(month)) {
        balance += contribution;
        monthContribution += contribution;
        if (isCompoundingDate) {
          // Paid on a compounding date, after the credit: it belongs to the next interval.
          interestBase += contribution;
        }
      }

      totalContributions += monthContribution;
      totalInterest += interestEarned;
      snapshots.add(new MonthlySnapshot(startingMonthOffset + month + 1, monthStartBalance,
          monthContribution, interestEarned, balance));
    }

    return new InvestmentResult(balance, totalContributions, totalInterest, snapshots);
  }
}
