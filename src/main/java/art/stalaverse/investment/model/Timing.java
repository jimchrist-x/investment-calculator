package art.stalaverse.investment.model;

/** Whether a contribution is paid at the beginning or at the end of its month. */
public enum Timing {
  /** Paid into the plan before the month's interest is credited. */
  BEGINNING,
  /** Paid into the plan after the month's interest is credited. */
  END
}
