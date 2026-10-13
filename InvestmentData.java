import java.util.List;
import java.util.ArrayList;
public class InvestmentData {
  private final double startingAmount;
  private final List<Integer> periods;
  private final List<Double> rates;
  private final List<Double> contributions;
  private final List<Frequency> frequencies;
  private final List<Boolean> contributeAtBeginning;
  private final List<Boolean> contributeEachMonth;
  public InvestmentData(double startingAmount) {
    this.startingAmount=startingAmount;
    periods=new ArrayList<>();
    rates=new ArrayList<>();
    contributions=new ArrayList<>();
    frequencies=new ArrayList<>();
    contributeAtBeginning=new ArrayList<>();
    contributeEachMonth=new ArrayList<>();
  }
  public void addPeriod(int period, double rate, double contribution, Frequency frequency, boolean contributeAtBeginning, boolean contributeEachMonth) {
    periods.add(period);
    rates.add(rate);
    contributions.add(contribution);
    frequencies.add(frequency);
    this.contributeAtBeginning.add(contributeAtBeginning);
    this.contributeEachMonth.add(contributeEachMonth);
  }
  public double getStartingAmount() {
    return startingAmount;
  }
  public List<Integer> getPeriods() {
    return periods;
  }
  public List<Double> getRates() {
    return rates;
  }
  public List<Double> getContributions() {
    return contributions;
  }
  public List<Frequency> getFrequencies() {
    return frequencies;
  }
  public List<Boolean> getContributeAtBeginning() {
    return contributeAtBeginning;
  }
  public List<Boolean> getContributeEachMonth() {
    return contributeEachMonth;
  }
}
