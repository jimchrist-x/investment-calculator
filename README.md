# Investment Calculator

A small portfolio modelling engine, written in Java, that projects compound interest over a
sequence of investment periods.

## Requirements

| Tool    | Version | Notes                                                        |
|---------|---------|--------------------------------------------------------------|
| JDK     | 21+     | `build.gradle.kts` pins the toolchain with `JavaLanguageVersion.of(21)` |
| Gradle  | 8.9     | The build uses the Kotlin DSL                                      |

Building with an older JDK fails with
`No matching toolchains found for requested specification: {languageVersion=21}` - install JDK 21
(or newer) and point `JAVA_HOME` at it.

## Build & run

```bash
gradle build   # compile and run the tests
gradle test    # unit tests only
gradle run     # print the yearly report, the monthly report and the summary
```

This repository deliberately does not commit the Gradle wrapper. If you want one, generate it once
with `gradle wrapper --gradle-version 8.9`; from then on `./gradlew <task>` works as usual.

## Example plan

```java
InvestmentData data = new InvestmentData(9790.47);                 // money already invested
data.addPeriod(4, 0.07, 200.0,                                      // 4 years, 7% a year
    CompoundingFrequency.CONTINUOUS,                                // how interest is credited
    ContributionFrequency.MONTHLY,                                  // unit of the 200.0
    Timing.END);                                                    // paid after the month's interest
InvestmentResult result = new InvestmentPeriods(data).calculate();
```

## Model semantics

### Contributions

The contribution amount is always paired with a `ContributionFrequency`, because the number alone
does not say what it means:

* `ContributionFrequency.MONTHLY` - the amount is paid **every month** (`200.0` = 200 per month,
  2400 per year).
* `ContributionFrequency.ANNUALLY` - the amount is paid **once per year** (`200.0` = 200 per year).

`Timing.BEGINNING` pays the contribution before the month's interest is credited, `Timing.END` pays
it after.

### Compounding

`CompoundingFrequency` decides **when interest is credited**, and only the money that has been in
the plan since the previous compounding date earns that credit:

* `ANNUALLY`, `SEMIANNUALLY`, `QUARTERLY` - interest is credited once per period
  (`rate / periodsPerYear`). A contribution paid during the year starts earning at the year-end
  credit, not in the month after it is paid. A lump sum still grows by exactly `rate` per year.
* `MONTHLY` - interest is credited every month.
* `SEMIMONTHLY`, `BIWEEKLY`, `WEEKLY`, `DAILY` - these compound several times inside a month, which
  is below the granularity of the monthly simulation. They are credited monthly at the equivalent
  rate `(1 + rate / periodsPerYear) ^ (periodsPerYear / 12) - 1`, which is exact for a lump sum and
  differs by a fraction of a cent for contributions.
* `CONTINUOUS` - grows at `e ^ (rate / 12) - 1` per month.

Each period re-anchors the compounding calendar at its own start.

### Validation

Invalid scenarios throw instead of producing `NaN` or an empty report:

* `years <= 0`, non-finite values, `rate <= -1` and negative starting amounts or contributions throw
  `IllegalArgumentException` from `InvestmentPeriod`, `InvestmentData` and `Investment`;
* `InvestmentPeriods.calculate()` throws `IllegalStateException` if the plan has no periods.

### Results

`InvestmentResult` is immutable: `monthlySnapshots()` and `yearlySnapshots()` return unmodifiable
lists, the incoming snapshots are copied defensively, and the yearly view is computed once and
cached, so it is cheap to call from a repaint loop. The console report prints money with two
decimals and every row reconciles (`start + contributions + interest = end`).

## TODO

- [ ] Store & show yearly compounding
- [ ] UI with Swing
