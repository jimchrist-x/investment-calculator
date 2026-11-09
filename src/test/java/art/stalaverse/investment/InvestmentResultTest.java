package art.stalaverse.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import art.stalaverse.investment.calculator.Investment;
import art.stalaverse.investment.calculator.InvestmentPeriods;
import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.MonthlySnapshot;
import art.stalaverse.investment.model.Timing;
import art.stalaverse.investment.model.YearlySnapshot;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Issue #5: {@link InvestmentResult} must be immutable and must not redo the yearly work on each call. */
class InvestmentResultTest {

  private static MonthlySnapshot month(int index, double start, double contribution, double interest) {
    return new MonthlySnapshot(index, start, contribution, interest, start + contribution + interest);
  }

  /** A consistent 25-month result: 100 paid in every month, 5 of interest earned on top of it. */
  private static InvestmentResult sample() {
    List<MonthlySnapshot> months = new ArrayList<>();
    double balance = 1_000.0;
    for (int i = 1; i <= 25; i++) {
      MonthlySnapshot snapshot = month(i, balance, 100.0, 5.0);
      months.add(snapshot);
      balance = snapshot.endingBalance();
    }
    return new InvestmentResult(balance, 25 * 100.0, 25 * 5.0, months);
  }

  @Test
  void monthlySnapshotsCannotBeMutatedThroughTheResult() {
    InvestmentResult result = sample();
    assertThrows(UnsupportedOperationException.class, () -> result.monthlySnapshots().clear());
    assertThrows(UnsupportedOperationException.class, () -> result.monthlySnapshots().add(month(1, 0, 0, 0)));
  }

  @Test
  void yearlySnapshotsCannotBeMutatedThroughTheResult() {
    InvestmentResult result = sample();
    assertThrows(UnsupportedOperationException.class, () -> result.yearlySnapshots().clear());
  }

  @Test
  void theResultIsNotAffectedByLaterChangesToTheCallersList() {
    List<MonthlySnapshot> source = new ArrayList<>();
    source.add(month(1, 1_000.0, 0.0, 5.0));
    InvestmentResult result = new InvestmentResult(1_005.0, 0.0, 5.0, source);

    source.clear();

    assertEquals(1, result.monthlySnapshots().size());
    assertEquals(1, result.yearlySnapshots().size());
    assertEquals(1_005.0, result.yearlySnapshots().get(0).endingBalance(), 0.0);
    assertEquals(1_005.0, result.finalBalance(), 0.0);
  }

  @Test
  void yearlySnapshotsAreCachedAndStable() {
    InvestmentResult result = sample();
    assertSame(result.yearlySnapshots(), result.yearlySnapshots());
    assertEquals(result.yearlySnapshots(), result.yearlySnapshots());
  }

  @Test
  void resultsWithTheSameDataAreEqual() {
    assertEquals(sample(), sample());
    assertNotEquals(sample(), new InvestmentResult(0.0, 0.0, 0.0, List.of()));
  }

  @Test
  void yearlyViewSplitsMonthsIntoYears() {
    List<YearlySnapshot> years = sample().yearlySnapshots();
    assertEquals(3, years.size());
    assertEquals(25 * 100.0, years.get(0).totalContributions() + years.get(1).totalContributions()
        + years.get(2).totalContributions(), 1e-9);
    assertEquals(12 * 100.0, years.get(0).totalContributions(), 1e-9);
    assertEquals(12 * 100.0, years.get(1).totalContributions(), 1e-9);
    assertEquals(100.0, years.get(2).totalContributions(), 1e-9);
    assertEquals(1_000.0, years.get(0).startingBalance(), 1e-9);
  }

  @Test
  void yearlyViewReconcilesWithTheTotals() {
    InvestmentResult result = new Investment(9_790.47, 29, 0.07, CompoundingFrequency.CONTINUOUS,
        1_000.0, ContributionFrequency.MONTHLY, Timing.END).calculate(0);

    double yearlyContributions = 0.0;
    double yearlyInterest = 0.0;
    for (YearlySnapshot year : result.yearlySnapshots()) {
      yearlyContributions += year.totalContributions();
      yearlyInterest += year.totalInterestEarned();
      assertEquals(year.startingBalance() + year.totalContributions() + year.totalInterestEarned(),
          year.endingBalance(), 1e-9, "year " + year.yearIndex());
    }
    assertEquals(result.totalContributions(), yearlyContributions, 1e-9);
    assertEquals(result.totalInterestEarned(), yearlyInterest, 1e-9);
    assertEquals(result.finalBalance(),
        result.yearlySnapshots().get(result.yearlySnapshots().size() - 1).endingBalance(), 1e-9);
  }

  @Test
  void anEmptyResultHasAnEmptyYearlyView() {
    InvestmentResult result = new InvestmentResult(0.0, 0.0, 0.0, List.of());
    assertTrue(result.monthlySnapshots().isEmpty());
    assertTrue(result.yearlySnapshots().isEmpty());
  }

  @Test
  void aPlanWithPeriodsProducesTheDocumentedYearlyView() {
    InvestmentData data = new InvestmentData(0.0);
    data.addPeriod(2, 0.0, 100.0, CompoundingFrequency.ANNUALLY,
        ContributionFrequency.MONTHLY, Timing.BEGINNING);
    InvestmentResult result = new InvestmentPeriods(data).calculate();

    assertEquals(2, result.yearlySnapshots().size());
    assertEquals(1_200.0, result.yearlySnapshots().get(0).totalContributions(), 1e-9);
    assertEquals(2_400.0, result.finalBalance(), 1e-9);
  }
}
