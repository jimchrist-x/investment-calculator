package art.stalaverse.investment.model;

import java.util.Objects;

/**
 * One segment of a plan: a whole number of years with a fixed nominal annual rate and a fixed
 * contribution schedule. Instances are immutable and validated on construction, so an invalid
 * scenario fails immediately instead of producing {@code NaN} or an empty projection.
 */
public final class InvestmentPeriod {
  private static final double MIN_RATE_EXCLUSIVE = -1.0;

  private final int years;
  private final double rate;
  private final double contribution;
  private final CompoundingFrequency compoundingFrequency;
  private final ContributionFrequency contributionFrequency;
  private final Timing timing;

  /**
   * @param years                 length of the period in whole years, must be &gt; 0
   * @param rate                  nominal annual interest rate as a fraction (0.07 = 7%), must be finite and &gt; -1
   * @param contribution          amount paid in each contribution month, must be finite and &gt;= 0
   * @param compoundingFrequency  how often interest is credited, must not be null
   * @param contributionFrequency unit of {@code contribution} (per month or per year), must not be null
   * @param timing                beginning or end of the month, must not be null
   * @throws IllegalArgumentException if any argument is out of range
   * @throws NullPointerException     if any enum argument is null
   */
  public InvestmentPeriod(int years, double rate, double contribution,
                          CompoundingFrequency compoundingFrequency,
                          ContributionFrequency contributionFrequency,
                          Timing timing) {
    if (years <= 0) {
      throw new IllegalArgumentException("years must be positive, was " + years);
    }
    if (!Double.isFinite(rate)) {
      throw new IllegalArgumentException("rate must be finite, was " + rate);
    }
    if (rate <= MIN_RATE_EXCLUSIVE) {
      throw new IllegalArgumentException(
          "rate must be greater than -1 (-100%), otherwise the balance is zeroed or complex, was " + rate);
    }
    if (!Double.isFinite(contribution) || contribution < 0) {
      throw new IllegalArgumentException("contribution must be finite and non-negative, was " + contribution);
    }
    this.years = years;
    this.rate = rate;
    this.contribution = contribution;
    this.compoundingFrequency = Objects.requireNonNull(compoundingFrequency, "compoundingFrequency");
    this.contributionFrequency = Objects.requireNonNull(contributionFrequency, "contributionFrequency");
    this.timing = Objects.requireNonNull(timing, "timing");
  }

  public int getYears() {
    return years;
  }

  /** Length of the period in months ({@code years * 12}). */
  public int getMonths() {
    return years * 12;
  }

  public double getRate() {
    return rate;
  }

  public double getContribution() {
    return contribution;
  }

  public CompoundingFrequency getCompoundingFrequency() {
    return compoundingFrequency;
  }

  public ContributionFrequency getContributionFrequency() {
    return contributionFrequency;
  }

  public Timing getTiming() {
    return timing;
  }

  @Override
  public String toString() {
    return "InvestmentPeriod[years=" + years
        + ", rate=" + rate
        + ", contribution=" + contribution
        + ", compoundingFrequency=" + compoundingFrequency
        + ", contributionFrequency=" + contributionFrequency
        + ", timing=" + timing + "]";
  }
}
