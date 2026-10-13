public class InvestmentPeriods {
  private final InvestmentData data;
  public InvestmentPeriods(InvestmentData data) {
    this.data=data;
  }
  public double calculate() {
    double amount=new Investment(data.getStartingAmount(), data.getPeriods().get(0), data.getRates().get(0), data.getFrequencies().get(0), data.getContributions().get(0), data.getContributeAtBeginning().get(0), data.getContributeEachMonth().get(0)).calculate();
    for (int i=1;i<data.getPeriods().size();i++) {
      amount=new Investment(amount, data.getPeriods().get(i), data.getRates().get(i), data.getFrequencies().get(i), data.getContributions().get(i), data.getContributeAtBeginning().get(i), data.getContributeEachMonth().get(i)).calculate();
    }
    return amount;
  }
}
