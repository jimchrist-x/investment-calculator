package art.stalaverse.investment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A plan: a starting amount plus an ordered list of {@link InvestmentPeriod}s.
 *
 * <p>Instances are mutable builders (periods can be added until {@code calculate()} is called) but
 * every value that is accepted is validated: the starting amount must be finite and non-negative,
 * and each period validates itself.
 */
public final class InvestmentData {
  private final double startingAmount;
  private final List<InvestmentPeriod> periods = new ArrayList<>();

  /**
   * @param startingAmount money already invested when the plan starts, must be finite and &gt;= 0
   * @throws IllegalArgumentException if {@code startingAmount} is not finite or negative
   */
  public InvestmentData(double startingAmount) {
    if (!Double.isFinite(startingAmount)) {
      throw new IllegalArgumentException("startingAmount must be finite, was " + startingAmount);
    }
    if (startingAmount < 0) {
      throw new IllegalArgumentException("startingAmount must be non-negative, was " + startingAmount);
    }
    this.startingAmount = startingAmount;
  }

  /**
   * Appends a period.
   *
   * @param years                 length in whole years, must be &gt; 0
   * @param rate                  nominal annual rate as a fraction, must be finite and &gt; -1
   * @param contribution          amount paid in each contribution month; its unit is defined by
   *                              {@code contributionFrequency}, must be finite and &gt;= 0
   * @param compoundingFrequency  how often interest is credited
   * @param contributionFrequency whether {@code contribution} is a monthly or an annual amount
   * @param timing                whether contributions are paid at the beginning or the end of the month
   * @throws IllegalArgumentException if any value is out of range
   * @throws NullPointerException     if any enum argument is null
   */
  public void addPeriod(int years, double rate, double contribution,
                        CompoundingFrequency compoundingFrequency,
                        ContributionFrequency contributionFrequency,
                        Timing timing) {
    periods.add(new InvestmentPeriod(years, rate, contribution, compoundingFrequency,
        contributionFrequency, timing));
  }

  /**
   * Appends an already validated period.
   *
   * @throws NullPointerException if {@code period} is null
   */
  public void addPeriod(InvestmentPeriod period) {
    periods.add(Objects.requireNonNull(period, "period"));
  }

  public double getStartingAmount() {
    return startingAmount;
  }

  public int getPeriodCount() {
    return periods.size();
  }

  public InvestmentPeriod getPeriod(int index) {
    return periods.get(index);
  }

  /** Immutable snapshot of the configured periods. */
  public List<InvestmentPeriod> getPeriods() {
    return List.copyOf(periods);
  }
}
