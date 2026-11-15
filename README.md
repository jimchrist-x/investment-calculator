# Investment Calculator

A small portfolio modelling engine, written in Java, that projects compound interest over a
sequence of investment periods.

## Requirements

| Tool    | Version | Notes                                                                     |
|---------|---------|---------------------------------------------------------------------------|
| JDK     | 21+     | `build.gradle.kts` pins the toolchain with `JavaLanguageVersion.of(21)`    |
| Gradle  | 8.9     | Pinned by the committed wrapper; a local install is optional              |

The build targets Java 21 because that is the LTS the project is developed against. Nothing in the
code needs anything newer: records and pattern matching for `instanceof` only require Java 17.

## Build & run

The Gradle wrapper is committed, so no local Gradle installation is needed:

```bash
./gradlew build   # compile and run the tests
./gradlew test    # unit tests only
./gradlew run     # print the yearly report, the monthly report and the summary
```

On Windows use `gradlew.bat build` and so on. The wrapper fetches Gradle 8.9 and verifies it against
the SHA-256 checksum pinned in `gradle/wrapper/gradle-wrapper.properties`.

A JDK 21 is still required. If none is installed, the toolchain resolver in `settings.gradle.kts`
(`org.gradle.toolchains.foojay-resolver-convention`) downloads one on the first build, which needs
network access. To use a JDK you already have, point `JAVA_HOME` at it. With an older JDK and
auto-provisioning unavailable the build fails with
`No matching toolchains found for requested specification: {languageVersion=21}`.

A local Gradle 8.9 install also works (`gradle build`); other Gradle versions may work but are
untested.

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
