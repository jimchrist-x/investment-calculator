import java.util.ArrayList;
public class InvestmentPeriods {
  private final ArrayList<Integer> periods;
  private final ArrayList<Double> contributions;
  private final ArrayList<Double> rates;
  private final ArrayList<Boolean> contributeAtBeginning;
  private final ArrayList<Boolean> contributeEachMonth;
  private final ArrayList<String> frequencies;
  private double startingAmount;
  public InvestmentPeriods(double startingAmount, ArrayList<Integer> periods, ArrayList<Double> rates, ArrayList<String> frequencies, ArrayList<Double> contributions, ArrayList<Boolean> contributeAtBeginning, ArrayList<Boolean> contributeEachMonth) {
    this.startingAmount=startingAmount;
    this.periods=periods;
    this.rates=rates;
    this.frequencies=frequencies;
    this.contributions=contributions;
    this.contributeAtBeginning=contributeAtBeginning;
    this.contributeEachMonth=contributeEachMonth;
  }
  public double calculate() {
    double amount=new Investment(startingAmount, periods.get(0), rates.get(0), frequencies.get(0), contributions.get(0), contributeAtBeginning.get(0), contributeEachMonth.get(0)).calculate();
    for (int i=1;i<periods.size();i++) {
      amount=new Investment(amount, periods.get(i), rates.get(i), frequencies.get(i), contributions.get(i), contributeAtBeginning.get(i), contributeEachMonth.get(i)).calculate();
    }
    return amount;
  }
}
