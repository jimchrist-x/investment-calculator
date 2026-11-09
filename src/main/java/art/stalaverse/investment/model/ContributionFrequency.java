package art.stalaverse.investment.model;

/**
 * How often the configured contribution is paid into the plan.
 *
 * <p>This is what gives {@code contribution} its unit: with {@link #MONTHLY} the amount is currency
 * per month, with {@link #ANNUALLY} it is currency per year. Keeping the unit explicit is the whole
 * point of this type - a bare boolean used to flip the same number between the two meanings, which
 * was a silent 12x error.
 */
public enum ContributionFrequency {
  /** The contribution amount is paid every month (12 payments per year). */
  MONTHLY(1),
  /** The contribution amount is paid once per year (1 payment per 12 months). */
  ANNUALLY(12);

  private final int monthsBetweenContributions;

  ContributionFrequency(int monthsBetweenContributions) {
    this.monthsBetweenContributions = monthsBetweenContributions;
  }

  /** Number of months between two contributions. */
  public int monthsBetweenContributions() {
    return monthsBetweenContributions;
  }

  /**
   * Whether a contribution is paid in the given zero-based month of a period.
   * Annual contributions are paid on the first month of every 12-month block.
   */
  public boolean isContributionMonth(int monthIndex) {
    if (monthIndex < 0) {
      throw new IllegalArgumentException("monthIndex must not be negative, was " + monthIndex);
    }
    return monthIndex % monthsBetweenContributions == 0;
  }
}
