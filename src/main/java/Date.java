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
     * Returns the age in days of this date in comparison to {@code today}.
     *
     * If {@code today} is lesser than {@code this} {@link Date} the age is negative!
     *
     * @param today  The {@link Date} you want to compare to
     * @return       days between both dates
     */
    public int getAgeInDaysAt(Date today) {
        if (this.equals(today))
            return 0;
        else if (this.isBiggerThan(today))
            return - today.getAgeInDaysAt(this);

        int days = 0;

        for (int year = this.year; year <= today.year; year++) {
            int month = year == this.year? this.month: 1;
            int maxMonth = year == today.year? today.month: 12;

            if (year != this.year && year != today.year) {
                days += daysInYear(year);
                continue;
            }

            for (; month <= maxMonth; month++) {
                if ((year != this.year || month != this.month) && (year != today.year || month != today.month))
                    days += daysInMonth(month, year);
                else {
                    int day = year == this.year? this.day - 1: 1;
                    int maxDay = year == today.year ? today.day: daysInMonth(month, year);

                    days += maxDay - day;
                }
            }
        }

        return days;
    }

    /**
     * Returns the age in years of this date in comparison to {@code today}.
     *
     * If {@code today} is lesser than {@code this} {@link Date} the age is negative!
     *
     * @param today  The {@link Date} you want to compare to
     * @return       years between both dates
     */
    public int getAgeInYearsAt(Date today) {
        if (this.equals(today))
            return 0;
        else if (this.isBiggerThan(today))
            return - today.getAgeInYearsAt(this);

        int years = Math.max(today.year - this.year - 1, 0);
        if (today.month >= this.month && today.day >= this.day)
            years++;

        return years;
    }

    private boolean isBiggerThan(Date date) {
        return this.year > date.year
                || this.year >= date.year && this.month > date.month
                || this.year >= date.year && this.month >= date.month && this.day > date.day;
    }

    public boolean equals(Date date) {
        return this.equals((Object) date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return day == date.day &&
                month == date.month &&
                year == date.year;
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
     * Returns the number of days in the February of the specified year. This method
     * considers leap years.
     *
     * @param year the year
     * @return the number of days in the February of the specified year.
     */
    private int daysInFebruary(int year) {
        if (year % 4 != 0) {
            return 28;
        }

        if ((year % 100 == 0) && (year % 400 != 0)) {
            return 28;
        }

        return 29;
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
                return daysInFebruary(year);
        }

        return -1;
    }

    private int daysInYear(int year) {
        return 337 + daysInFebruary(year);
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