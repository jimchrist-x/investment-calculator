package art.stalaverse.investment.model;

/**
 * How often interest is credited to the balance, i.e. the compounding period.
 *
 * <p>The simulation steps once per month. Frequencies that divide a year into whole months
 * ({@link #ANNUALLY}, {@link #SEMIANNUALLY}, {@link #QUARTERLY}, {@link #MONTHLY}) credit interest
 * exactly on their compounding dates: the balance held since the previous compounding date earns
 * {@code rate / periodsPerYear}, and contributions made between two compounding dates earn nothing
 * until the next one. A lump sum therefore still grows by exactly {@code rate} per year.
 *
 * <p>Frequencies finer than a month ({@link #SEMIMONTHLY}, {@link #BIWEEKLY}, {@link #WEEKLY},
 * {@link #DAILY}) compound several times inside a single month, which is below the granularity of
 * this model. They are credited monthly at the equivalent monthly rate
 * {@code (1 + rate / periodsPerYear) ^ (periodsPerYear / 12) - 1}, which is arithmetically identical
 * to the nominal frequency for a lump sum and differs by a fraction of a cent for contributions.
 *
 * <p>{@link #CONTINUOUS} grows at {@code e^(rate / 12) - 1} per month, which is exactly continuous
 * compounding sampled monthly.
 */
public enum CompoundingFrequency {
  ANNUALLY(1, 12),
  SEMIANNUALLY(2, 6),
  QUARTERLY(4, 3),
  MONTHLY(12, 1),
  SEMIMONTHLY(24, 1),
  BIWEEKLY(365.0 / 14.0, 1),
  WEEKLY(365.0 / 7.0, 1),
  DAILY(365, 1),
  CONTINUOUS(Double.POSITIVE_INFINITY, 1);

  private final double periodsPerYear;
  private final int monthsPerPeriod;

  CompoundingFrequency(double periodsPerYear, int monthsPerPeriod) {
    this.periodsPerYear = periodsPerYear;
    this.monthsPerPeriod = monthsPerPeriod;
  }

  /** Number of compounding periods in a year ({@link Double#POSITIVE_INFINITY} for {@link #CONTINUOUS}). */
  public double periodsPerYear() {
    return periodsPerYear;
  }

  /** Number of months between two compounding dates; always 1 or more. */
  public int monthsPerPeriod() {
    return monthsPerPeriod;
  }

  public boolean isContinuous() {
    return this == CONTINUOUS;
  }

  /**
   * The rate credited at the end of each monthly step for a nominal annual {@code rate}.
   *
   * @throws IllegalArgumentException if {@code rate} is not greater than {@code -1}
   */
  public double interestRatePerPeriod(double rate) {
    if (!Double.isFinite(rate)) {
      throw new IllegalArgumentException("rate must be finite, was " + rate);
    }
    if (rate <= -1.0) {
      throw new IllegalArgumentException("rate must be greater than -1 (-100%), was " + rate);
    }
    if (isContinuous()) {
      return Math.exp(rate / 12.0) - 1.0;
    }
    if (monthsPerPeriod > 1) {
      return rate / periodsPerYear;
    }
    return Math.pow(1.0 + rate / periodsPerYear, periodsPerYear / 12.0) - 1.0;
  }
}
