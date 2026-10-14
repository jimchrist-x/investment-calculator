public class InvestmentPeriods {
  private final InvestmentData data;
  public InvestmentPeriods(InvestmentData data) {
    this.data=data;
  }
  public double calculate() {
    double amount=data.getStartingAmount();
    for (int i=0;i<data.getPeriodCount();i++) {
      InvestmentPeriod period=data.getPeriod(i);
      amount=new Investment(amount, period.getYears(), period.getRate(), period.getFrequency(), period.getContribution(), period.isContributionAtBeginning(), period.isMonthlyContribution()).calculate();
    }
    return amount;
  }
}
