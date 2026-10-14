public class InvestmentPeriod {
  private final int years;
  private final double rate, contribution;
  private final Frequency frequency;
  private final boolean contributeAtBeginning, contributeEachMonth;
  public InvestmentPeriod(int years, double rate, double contribution, Frequency frequency, boolean contributeAtBeginning, boolean contributeEachMonth) {
    this.years=years;
    this.rate=rate;
    this.contribution=contribution;
    this.frequency=frequency;
    this.contributeAtBeginning=contributeAtBeginning;
    this.contributeEachMonth=contributeEachMonth;
  }
  public int getYears() {
    return years;
  }
  public double getRate() {
    return rate;
  }
  public double getContribution() {
    return contribution;
  }
  public Frequency getFrequency() {
    return frequency;
  }
  public boolean isContributionAtBeginning() {
    return contributeAtBeginning;
  }
  public boolean isMonthlyContribution() {
    return contributeEachMonth;
  }
}
