/**
 * The class {@code Date} represents a date.
 *
 * This class ensures, that the represented date is always a valid date. For
 * example, the method {@link Date#toString()} will never return Date{day=31, month=2, year=2010}.
 *
 */
public class Date {

    private int day;
    private int month;
    private int year;

    public Date() {
        this(Terminal.TODAYS_DAY, Terminal.TODAYS_MONTH, Terminal.TODAYS_YEAR);
    }

    /**
     * Constructs a date with the given values.
     *
     * @param day   the day
     * @param month the month
     * @param year  the year
     */
    public Date(int day, int month, int year) {
        this.day = 1;
        // We choose a month with 31 days so that setter for days won't fail
        this.month = 1;
        // We choose a leapyear so that the day setter won't fail
        this.year = 2020;
        this.setDay(day);
        this.setMonth(month);
        this.setYear(year);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    /**
     * Sets the day of this date.
     *
     * If the specified day is < 1, then the day is set to 1. If the specified day
     * is greater than the number of days in the month of this date, then it is set
     * to the maximum value of days in the month of this date.
     *
     * @see Date#daysInMonth(int, int)
     * @param day the new day
     */
    public void setDay(int day) {
        if (day < 1) {
            this.day = 1;
        } else if (day > daysInMonth(this.month, this.year)) {
            this.day = daysInMonth(this.month, this.year);
        } else {
            this.day = day;
        }
    }

    /**
     * Sets the month of this date.
     *
     * If the specified month is < 1, the month is set to 1. If the specified month
     * is > 12, the month is set to 12. If the new month has less days than the
     * current month, it may happen that the day of this date gets invalid. In this
     * case, the day of this date is set to the maximum value of the specified
     * month.
     *
     * @param month the new month
     */
    public void setMonth(int month) {
        if (month < 1) {
            this.month = 1;
        } else if (month > 12) {
            this.month = 12;
        } else {
            this.month = month;
        }

        /*
         * To avoid that the day of this date gets invalid, execute the setDay() method
         * with the current day
         */
        this.setDay(this.day);
    }

    /**
     * Sets the year of this date.
     *
     * If the specified year is < 1900, then the year is set to 1900. If the
     * specified year is > 2100, then the year is set to 2100. In case the current
     * month of this date is February it may happen that the day of this date gets
     * invalid (a value of 29 in a leap year). In this case the day is set to 28.
     *
     * @param year the new year
     */
    public void setYear(int year) {
        if (year < 1900) {
            this.year = 1900;
        } else if (year > 2100) {
            this.year = 2100;
        } else {
            this.year = year;
        }

        /*
         * To avoid that the day of this date gets invalid, execute the setDay() method
         * with the current day
         */
        this.setDay(this.day);
    }

    private int daysSince1970() {
        return new Date(1, 1, 1970).getAgeInDaysAt(this);
    }

    /**
     *
     * @param today  Date you want to compare to
     * @return       days between both dates
     *                   CAUTION: can produce negative values, since no check is done to ensure <code>today</code> is
     *                   bigger than this date, as no specifications are given how to handle those instances
     */
    public int getAgeInDaysAt(Date today) {
        int yearDelta = today.year - year;
        int monthDelta = today.month - month;
        int dayDelta = today.day - day; // could be negative

        return yearDelta * 12 * 30 + monthDelta * 30 + dayDelta;
    }

    /**
     *
     * @param today  Date you want to compare to
     * @return       years between both dates
     *                   CAUTION: can produce negative values, see {@link #getAgeInDaysAt(Date)}
     */
    public int getAgeInYearsAt(Date today) {
        return this.getAgeInDaysAt(today) / (12 * 30);
    }

    @Override
    public String toString() {
        return "Date{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }

    /**
     * Returns the number of the days in the specified month in the specified year.
     * For a <code>month</code> value of either 1, 3, 5, 7, 8, 10 or 12 this method
     * returns 31. For a <code>month</code> value of either 4, 6, 9 or 11 this
     * method returns 30. For a <code>month</code> value of 2 (February) the
     * returned value is either 28 or 29, depending on the specified year.
     *
     * @param month the month
     * @param year  the year
     * @return the number of the days in the specified month in the specified year.
     */
    private int daysInMonth(int month, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return daysinFebruary(year);
        }

        return -1;
    }

    /**
     * Returns the number of days in the February of the specified year. This method
     * considers leap years.
     *
     * @param year the year
     * @return the number of days in the February of the specified year.
     */
    private int daysinFebruary(int year) {
        if (year % 4 != 0) {
            return 28;
        }

        if ((year % 100 == 0) && (year % 400 != 0)) {
            return 28;
        }

        return 29;
    }

    /**
     * Bzgl Aufgabe 2.9 Punkt 6:
     *      Eine Dokumentation ist notwendig, da sich aus dem Methodenkopf die genaue Funktionsweise der Methode nicht
     *      eindeutig ergibt. Zum einen sollte angeben werden, dass hier vereinfacht ein Monat mit 30 Tagen dargestellt.
     *      Ebenso werden Schaltjahre ignoriert.
     *      Des Weiteren, wie ich bereits übereifrig im Kommentar der beiden Methoden erwähne, sollte klargestellt werden,
     *      was passiert, wenn das Datum des Eingabeparameters kleiner ist als das Datum des eigenen Objekts.
     *
     *      Für {@link #daysSince1970()} wäre es außerdem noch sinnvoll klar zu stellen, dass die Methode die Tage seit
     *      der Unix Epoch zurück gibt.
     *
     *      Bzgl der Methoden aus Punkt 4 und 5: Beide Methoden haben den selben Namen mit dem selben Eingabeparameter.
     *      Daraus könnte man folgern, dass sie wohl das selbe bewirken. Allerdings gibt die Methode in der Klasse
     *      {@link Author} den Unterschied in Jahren aus und die Methode in der Klasse {@link Document} und {@link Review}
     *      den Unterschied in Tagen. Dies sollte klar dokumentiert werden. Außerdem finden die bereits oben genannten
     *      Punkte hier auch Anwendung.
     */
}