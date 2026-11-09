package art.stalaverse.investment.calculator;

import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentPeriod;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.MonthlySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Chains the periods of an {@link InvestmentData} plan, each one starting from the previous balance. */
public final class InvestmentPeriods {
  private final InvestmentData data;

  /**
   * @param data the plan to project, must not be null
   * @throws NullPointerException if {@code data} is null
   */
  public InvestmentPeriods(InvestmentData data) {
    this.data = Objects.requireNonNull(data, "data");
  }

  /**
   * Projects every period in order.
   *
   * @return an immutable result covering the whole plan
   * @throws IllegalStateException if the plan has no periods, instead of returning an empty projection
   */
  public InvestmentResult calculate() {
    int periodCount = data.getPeriodCount();
    if (periodCount == 0) {
      throw new IllegalStateException(
          "Cannot calculate a projection without investment periods; add at least one with InvestmentData.addPeriod(...)");
    }
    double currentAmount = data.getStartingAmount();
    double cumulativeContributions = 0.0;
    double cumulativeInterest = 0.0;
    List<MonthlySnapshot> allSnapshots = new ArrayList<>();
    for (int i = 0; i < periodCount; i++) {
      InvestmentPeriod period = data.getPeriod(i);
      Investment investment = new Investment(currentAmount, period);
      InvestmentResult periodResult = investment.calculate(allSnapshots.size());
      currentAmount = periodResult.finalBalance();
      cumulativeContributions += periodResult.totalContributions();
      cumulativeInterest += periodResult.totalInterestEarned();
      allSnapshots.addAll(periodResult.monthlySnapshots());
    }
    return new InvestmentResult(currentAmount, cumulativeContributions, cumulativeInterest, allSnapshots);
  }
}
