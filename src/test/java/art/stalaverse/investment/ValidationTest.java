package art.stalaverse.investment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import art.stalaverse.investment.calculator.Investment;
import art.stalaverse.investment.calculator.InvestmentPeriods;
import art.stalaverse.investment.model.CompoundingFrequency;
import art.stalaverse.investment.model.ContributionFrequency;
import art.stalaverse.investment.model.InvestmentData;
import art.stalaverse.investment.model.InvestmentPeriod;
import art.stalaverse.investment.model.Timing;
import org.junit.jupiter.api.Test;

/** Issue #1: invalid scenarios must fail fast instead of producing NaN or silent zero results. */
class ValidationTest {

  private static InvestmentPeriod period(double rate) {
    return new InvestmentPeriod(10, rate, 0.0, CompoundingFrequency.ANNUALLY,
        ContributionFrequency.MONTHLY, Timing.BEGINNING);
  }

  @Test
  void rejectsRateBelowMinusOneHundredPercent() {
    assertThrows(IllegalArgumentException.class, () -> period(-2.0));
  }

  @Test
  void rejectsRateOfExactlyMinusOneHundredPercent() {
    assertThrows(IllegalArgumentException.class, () -> period(-1.0));
  }

  @Test
  void rejectsNonFiniteRate() {
    assertThrows(IllegalArgumentException.class, () -> period(Double.NaN));
    assertThrows(IllegalArgumentException.class, () -> period(Double.POSITIVE_INFINITY));
  }

  @Test
  void acceptsZeroAndPositiveRates() {
    assertEquals(0.0, period(0.0).getRate());
    assertEquals(0.07, period(0.07).getRate());
  }

  @Test
  void rejectsNonPositivePeriodLength() {
    assertThrows(IllegalArgumentException.class, () -> new InvestmentPeriod(0, 0.07, 100.0,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.BEGINNING));
    assertThrows(IllegalArgumentException.class, () -> new InvestmentPeriod(-3, 0.07, 100.0,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.BEGINNING));
  }

  @Test
  void rejectsNegativeOrNonFiniteContribution() {
    assertThrows(IllegalArgumentException.class, () -> new InvestmentPeriod(1, 0.07, -1.0,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.BEGINNING));
    assertThrows(IllegalArgumentException.class, () -> new InvestmentPeriod(1, 0.07, Double.NaN,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.BEGINNING));
  }

  @Test
  void rejectsNegativeStartingAmount() {
    assertThrows(IllegalArgumentException.class, () -> new InvestmentData(-1.0));
    assertThrows(IllegalArgumentException.class, () -> new InvestmentData(Double.NaN));
  }

  @Test
  void rejectsNegativeStartingAmountOnInvestment() {
    assertThrows(IllegalArgumentException.class, () -> new Investment(-5.0,
        new InvestmentPeriod(1, 0.07, 100.0, CompoundingFrequency.MONTHLY,
            ContributionFrequency.MONTHLY, Timing.BEGINNING)));
  }

  @Test
  void rejectsNullScheduleEnums() {
    assertThrows(NullPointerException.class, () -> new InvestmentPeriod(1, 0.07, 100.0,
        null, ContributionFrequency.MONTHLY, Timing.BEGINNING));
    assertThrows(NullPointerException.class, () -> new InvestmentPeriod(1, 0.07, 100.0,
        CompoundingFrequency.MONTHLY, null, Timing.BEGINNING));
    assertThrows(NullPointerException.class, () -> new InvestmentPeriod(1, 0.07, 100.0,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, null));
  }

  @Test
  void rejectsEmptyPlan() {
    InvestmentPeriods portfolio = new InvestmentPeriods(new InvestmentData(10_000));
    IllegalStateException error = assertThrows(IllegalStateException.class, portfolio::calculate);
    assertTrue(error.getMessage().contains("addPeriod"), error.getMessage());
  }

  @Test
  void rejectsNegativeMonthOffset() {
    Investment investment = new Investment(1_000.0, new InvestmentPeriod(1, 0.07, 0.0,
        CompoundingFrequency.MONTHLY, ContributionFrequency.MONTHLY, Timing.END));
    assertThrows(IllegalArgumentException.class, () -> investment.calculate(-1));
  }

  @Test
  void rejectsNullData() {
    assertThrows(NullPointerException.class, () -> new InvestmentPeriods(null));
  }
}
