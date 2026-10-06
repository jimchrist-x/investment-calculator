import java.util.Arrays;
import java.util.ArrayList;
public class Main {
  public static void main(String[] args) {
    double startingAmount=9790.47;
    ArrayList<Integer> periods = new ArrayList<>();
    ArrayList<Double> rates = new ArrayList<>(), contributions=new ArrayList<>();
    ArrayList<String> frequencies = new ArrayList<>();
    ArrayList<Boolean> contributeAtBeginning = new ArrayList<>(), contributeEachMonth = new ArrayList<>();
    periods.addAll(Arrays.asList(4, 4, 4, 4, 4, 25));
    rates.addAll(Arrays.asList(0.07, 0.07, 0.07, 0.07, 0.07, 0.07));
    frequencies.addAll(Arrays.asList("continuous", "continuous", "continuous", "continuous", "continuous", "continuous"));
    contributions.addAll(Arrays.asList(200.0, 650.0, 887.5, 1070.5, 1183.0, 1183.0));
    contributeAtBeginning.addAll(Arrays.asList(false, false, false, false, false, false));
    contributeEachMonth.addAll(Arrays.asList(true, true, true, true, true, true));
    InvestmentPeriods portfolio = new InvestmentPeriods(startingAmount, periods, rates, frequencies, contributions, contributeAtBeginning, contributeEachMonth);
    System.out.println(portfolio.calculate());
  }
}
