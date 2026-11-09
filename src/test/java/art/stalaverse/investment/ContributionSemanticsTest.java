package art.stalaverse.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import art.stalaverse.investment.calculator.Investment;
import art.stalaverse.investment.calculator.InvestmentPeriods;
import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.Timing;
import org.junit.jupiter.api.Test;

/**
 * Issue #3: the same contribution number must not mean a monthly amount in one configuration and a
 * yearly amount in another.
 */
class ContributionSemanticsTest {

  private static InvestmentResult project(ContributionFrequency contributionFrequency, Timing timing,
                                          CompoundingFrequency compounding) {
    return new Investment(0.0, 1, 0.07, compounding, 100.0, contributionFrequency, timing).calculate(0);
  }

  @Test
  void monthlyContributionIsPaidTwelveTimesPerYear() {
    InvestmentResult result = project(ContributionFrequency.MONTHLY, Timing.BEGINNING,
        CompoundingFrequency.ANNUALLY);
    assertEquals(1_200.0, result.totalContributions(), 1e-12);
  }

  @Test
  void annualContributionIsPaidOncePerYear() {
    InvestmentResult result = project(ContributionFrequency.ANNUALLY, Timing.BEGINNING,
        CompoundingFrequency.ANNUALLY);
    assertEquals(100.0, result.totalContributions(), 1e-12);
  }

  @Test
  void theUnitIsExplicitSoTheSameNumberCannotSilentlyChangeMeaning() {
    double monthly = project(ContributionFrequency.MONTHLY, Timing.BEGINNING,
        CompoundingFrequency.MONTHLY).totalContributions();
    double annual = project(ContributionFrequency.ANNUALLY, Timing.BEGINNING,
        CompoundingFrequency.MONTHLY).totalContributions();
    assertEquals(12.0, monthly / annual, 1e-12);
  }

  @Test
  void contributionsAtTheBeginningEarnMoreThanAtTheEnd() {
    InvestmentResult beginning = project(ContributionFrequency.MONTHLY, Timing.BEGINNING,
        CompoundingFrequency.MONTHLY);
    InvestmentResult end = project(ContributionFrequency.MONTHLY, Timing.END,
        CompoundingFrequency.MONTHLY);
    assertTrue(beginning.totalInterestEarned() > end.totalInterestEarned(),
        "beginning=" + beginning.totalInterestEarned() + " end=" + end.totalInterestEarned());
  }

  @Test
  void monthlyCompoundingCreditsTheFirstMonthsInterestOnABeginningContribution() {
    // One month, monthly compounding: 100 paid at the beginning earns one month of interest.
    InvestmentResult result = new Investment(0.0, 1, 0.07, CompoundingFrequency.MONTHLY,
        100.0, ContributionFrequency.ANNUALLY, Timing.BEGINNING).calculate(0);
    assertEquals(100.0 * (0.07 / 12.0), result.monthlySnapshots().get(0).interestEarned(), 1e-12);
  }

  @Test
  void periodLengthIsAlwaysWholeMonthsInBothLayers() {
    InvestmentData data = new InvestmentData(0.0);
    data.addPeriod(3, 0.07, 100.0, CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.END);
    InvestmentResult result = new InvestmentPeriods(data).calculate();
    assertEquals(36, result.monthlySnapshots().size());
    assertEquals(3, result.yearlySnapshots().size());
    assertEquals(36, data.getPeriod(0).getMonths());
  }

  @Test
  void snapshotsAreNumberedContinuouslyAcrossPeriods() {
    InvestmentData data = new InvestmentData(1_000.0);
    data.addPeriod(1, 0.05, 10.0, CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.END);
    data.addPeriod(1, 0.05, 20.0, CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.END);
    InvestmentResult result = new InvestmentPeriods(data).calculate();
    assertEquals(24, result.monthlySnapshots().size());
    assertEquals(1, result.monthlySnapshots().get(0).monthIndex());
    assertEquals(24, result.monthlySnapshots().get(23).monthIndex());
    assertEquals(360.0, result.totalContributions(), 1e-12);
  }
}
