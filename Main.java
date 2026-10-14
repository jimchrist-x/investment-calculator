public class Main {
  public static void main(String[] args) {
    InvestmentData data = new InvestmentData(9790.47);
    data.addPeriod(4, 0.07, 200.0, Frequency.CONTINUOUS, false, true);
    data.addPeriod(4, 0.07, 650.0, Frequency.CONTINUOUS, false, true);
    data.addPeriod(4, 0.07, 887.5, Frequency.CONTINUOUS, false, true);
    data.addPeriod(4, 0.07, 1070.5, Frequency.CONTINUOUS, false, true);
    data.addPeriod(4, 0.07, 1183.0, Frequency.CONTINUOUS, false, true);
    data.addPeriod(25, 0.07, 1183.0, Frequency.CONTINUOUS, false, true);
    InvestmentPeriods portfolio = new InvestmentPeriods(data);
    System.out.printf("Final balance: %,.2f%n", portfolio.calculate());
  }
}
