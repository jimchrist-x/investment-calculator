public class Investment {
  private double startingAmount;
  private double periodInYears;
  private double rate;
  private double contribution;
  private double compoundFrequency;
  private boolean contributeAtBeginning;
  private boolean contributeEachMonth;
  private boolean isContinuous;
  public Investment(double startingAmount, double periodInYears, double rate, Frequency frequency, double contribution, boolean contributeAtBeginning, boolean contributeEachMonth) {
    this.startingAmount=startingAmount;
    this.periodInYears=periodInYears;
    this.rate=rate;
    this.isContinuous=false;
    setFrequency(frequency);
    this.contribution=contribution;
    this.contributeAtBeginning=contributeAtBeginning;
    this.contributeEachMonth=contributeEachMonth;
  }
  public double calculate() {
    double amount=startingAmount;
    double monthlyRate;
    if (isContinuous) {
      monthlyRate=Math.exp(rate/12)-1;
    } else {
      monthlyRate=Math.pow(1+(rate/compoundFrequency), compoundFrequency/12)-1;
    }
    int months=(int)Math.round(periodInYears*12);
    for (int month=0; month<months;month++) {
      if (contributeEachMonth && contributeAtBeginning) {
        amount+=contribution;
      } else if (!contributeEachMonth && contributeAtBeginning) {
        if (month%12==0) {
          amount+=contribution;
        }
      }
      amount*=(1+monthlyRate);
      if (contributeEachMonth && !contributeAtBeginning) {
        amount+=contribution;
      } else if (!contributeEachMonth && !contributeAtBeginning) {
        if ((month+1)%12==0) {
          amount+=contribution;
        }
      }
    }
    return amount;
  }
  private void setFrequency(Frequency frequency) {
    switch (frequency) {
      case ANNUALLY:
        compoundFrequency=1;
        break;
      case SEMIANNUALLY:
        compoundFrequency=2;
        break;
      case QUARTERLY:
        compoundFrequency=4;
        break;
      case MONTHLY:
        compoundFrequency=12;
        break;
      case SEMIMONTHLY: 
        compoundFrequency=24;
        break;
      case BIWEEKLY:
        compoundFrequency=365.0/14;
        break;
      case WEEKLY:
        compoundFrequency=365.0/7;
        break;
      case DAILY:
        compoundFrequency=365;
        break;
      case CONTINUOUS:
        isContinuous=true;
        break;
      default:
        compoundFrequency=1;
    }
  }
}
