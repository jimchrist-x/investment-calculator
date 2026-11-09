package art.stalaverse.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import art.stalaverse.investment.calculator.Investment;
import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentResult;
import art.stalaverse.investment.model.Timing;
import org.junit.jupiter.api.Test;

/**
 * Issue #2: the compounding frequency must control when interest is credited, not only how the
 * nominal annual rate is converted into an equivalent monthly rate.
 */
class CompoundingTest {

  private static InvestmentResult project(double startingAmount, int years, double rate,
                                          CompoundingFrequency compounding, double contribution,
                                          ContributionFrequency contributionFrequency, Timing timing) {
    return new Investment(startingAmount, years, rate, compounding, contribution, contributionFrequency, timing)
        .calculate(0);
  }

  @Test
  void annualCompoundingOnALumpSumGrowsByExactlyTheAnnualRate() {
    InvestmentResult result = project(10_000, 1, 0.07, CompoundingFrequency.ANNUALLY,
        0.0, ContributionFrequency.MONTHLY, Timing.END);
    assertEquals(10_700.0, result.finalBalance(), 1e-9);
    assertEquals(700.0, result.totalInterestEarned(), 1e-9);
  }

  @Test
  void monthlyCompoundingMatchesTheNominalMonthlyRate() {
    InvestmentResult result = project(10_000, 1, 0.07, CompoundingFrequency.MONTHLY,
        0.0, ContributionFrequency.MONTHLY, Timing.END);
    assertEquals(10_000 * Math.pow(1.0 + 0.07 / 12.0, 12), result.finalBalance(), 1e-9);
  }

  @Test
  void quarterlyCompoundingMatchesTheNominalQuarterlyRate() {
    InvestmentResult result = project(10_000, 1, 0.07, CompoundingFrequency.QUARTERLY,
        0.0, ContributionFrequency.MONTHLY, Timing.END);
    assertEquals(10_000 * Math.pow(1.0 + 0.07 / 4.0, 4), result.finalBalance(), 1e-9);
  }

  @Test
  void continuousCompoundingGrowsByEToTheRate() {
    InvestmentResult result = project(10_000, 1, 0.07, CompoundingFrequency.CONTINUOUS,
        0.0, ContributionFrequency.MONTHLY, Timing.END);
    assertEquals(10_000 * Math.exp(0.07), result.finalBalance(), 1e-6);
  }

  @Test
  void annualCompoundingCreditsInterestOncePerYear() {
    InvestmentResult result = project(1_000, 2, 0.07, CompoundingFrequency.ANNUALLY,
        0.0, ContributionFrequency.MONTHLY, Timing.END);
    for (int month = 0; month < 11; month++) {
      assertEquals(0.0, result.monthlySnapshots().get(month).interestEarned(), 1e-12);
    }
    assertEquals(70.0, result.monthlySnapshots().get(11).interestEarned(), 1e-9);
    assertEquals(74.9, result.monthlySnapshots().get(23).interestEarned(), 1e-9);
  }

  /**
   * The example from issue #2: with annual compounding, contributions paid during the year are not
   * part of the compounding balance until the year-end credit, so the first year earns no interest.
   */
  @Test
  void contributionsWaitForTheNextCompoundingDateUnderAnnualCompounding() {
    InvestmentResult result = project(0, 1, 0.07, CompoundingFrequency.ANNUALLY,
        100.0, ContributionFrequency.MONTHLY, Timing.END);
    assertEquals(0.0, result.totalInterestEarned(), 1e-12);
    assertEquals(1_200.0, result.totalContributions(), 1e-12);
    assertEquals(1_200.0, result.finalBalance(), 1e-12);
  }

  @Test
  void contributionsDoEarnInterestUnderMonthlyCompounding() {
    InvestmentResult result = project(0, 1, 0.07, CompoundingFrequency.MONTHLY,
        100.0, ContributionFrequency.MONTHLY, Timing.END);
    // 1200 paid in, each instalment earning monthly interest for the remainder of the year:
    // 100 * [((1 + r)^12 - 1) / r - 12] with r = 7% / 12.
    double monthlyRate = 0.07 / 12.0;
    double expectedInterest = 100.0 * ((Math.pow(1.0 + monthlyRate, 12) - 1.0) / monthlyRate - 12.0);
    assertEquals(expectedInterest, result.totalInterestEarned(), 1e-9);
    assertEquals(1_200.0, result.totalContributions(), 1e-12);
    assertEquals(1_200.0 + result.totalInterestEarned(), result.finalBalance(), 1e-9);
  }

  @Test
  void aContributionPaidOnTheCompoundingDateEarnsThatPeriod() {
    InvestmentResult result = project(0, 1, 0.07, CompoundingFrequency.ANNUALLY,
        1_000.0, ContributionFrequency.ANNUALLY, Timing.BEGINNING);
    assertEquals(70.0, result.totalInterestEarned(), 1e-9);
    assertEquals(1_070.0, result.finalBalance(), 1e-9);
  }

  @Test
  void everyMonthReconciles() {
    InvestmentResult result = project(9_790.47, 6, 0.07, CompoundingFrequency.QUARTERLY,
        200.0, ContributionFrequency.MONTHLY, Timing.END);
    for (var month : result.monthlySnapshots()) {
      assertEquals(month.startingBalance() + month.contribution() + month.interestEarned(),
          month.endingBalance(), 1e-9, "month " + month.monthIndex());
    }
  }

  @Test
  void subMonthlyFrequenciesCreditInterestEveryMonth() {
    for (CompoundingFrequency frequency : new CompoundingFrequency[] {
        CompoundingFrequency.SEMIMONTHLY, CompoundingFrequency.BIWEEKLY,
        CompoundingFrequency.WEEKLY, CompoundingFrequency.DAILY}) {
      InvestmentResult result = project(1_000, 1, 0.07, frequency, 0.0,
          ContributionFrequency.MONTHLY, Timing.END);
      for (var month : result.monthlySnapshots()) {
        assertTrue(month.interestEarned() > 0.0, frequency + " month " + month.monthIndex());
      }
    }
  }
}
