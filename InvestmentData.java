import java.util.List;
import java.util.ArrayList;
public class InvestmentData {
  private final double startingAmount;
  private final List<InvestmentPeriod> periods;
  public InvestmentData(double startingAmount) {
    this.startingAmount=startingAmount;
    periods = new ArrayList<>();
  }
  public void addPeriod(int years, double rate, double contribution, Frequency frequency, boolean contributeAtBeginning, boolean contributeEachMonth) {
    periods.add(new InvestmentPeriod(years, rate, contribution, frequency, contributeAtBeginning, contributeEachMonth));
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
}
