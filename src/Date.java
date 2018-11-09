/**
 * The class {@code Date} represents a date.
 * 
 * @author Florian Kelbert, Andreas Bauer
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
     * Instantiates a new Date object.
     * If day, month or year are invalid values day is set to 1, month to 1 and year to the current year!
     *
     * @param day                           an {@code int} representing the day of month
     * @param month                         an {@code int} representing the month of year
     * @param year                          an {@code int} representing the year
     */
    public Date(int day, int month, int year) {
        // using setter methods to check for illegal values
        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);

        if (day == 0)
            this.day = 1;

        if (month == 0)
            this.month = 1;
        if (year == 0)
            this.year = Terminal.TODAYS_YEAR;
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
     * Sets the day of the {@code Date} object.
     * If the {@code day} is not a valid day for the current {@code month}
     * and {@code year} no change is done.
     *
     * @param day                           an {@code int} representing the day of month
     */
    public void setDay(int day) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day < 1 || day > 31)
                    return; //throw new IllegalArgumentException("'day' must be in range of 1 to 31 for month '" + month + "'!");
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day < 1 || day > 30)
                    return; //throw new IllegalArgumentException("'day' must be in range of 1 to 30 for month '" + month + "'!");
                break;
            case 2:
                int maxDay = year % 4 == 0? 29: 28;

                if (day < 1 || day > maxDay)
                    return; //throw new IllegalArgumentException("'day' must be in rage of 1 to " + maxDay + " for month '" +
                            //month + "' and year '" + year + "'!");
                break;
        }

        this.day = day;
    }

    /**
     * Sets the month of the {@code Date} object.
     * If the {@code month} is not in the range of 1 to 12 no change is done.
     *
     * @param month                         an {@code int} representing the month of year
     */
    public void setMonth(int month) {
        if (month < 1 || month > 12)
            return; //throw new IllegalArgumentException("'month' must be in of 1 to 12!");

        this.month = month;
    }

    /**
     * Sets the year of the {@code Date} object.
     * If the year is negative or zero no change is done.
     *
     * @param year  an {@code int} representing the year
     */
    public void setYear(int year) {
        if (this.year <= 0)
            return;

        this.year = year;
    }

    private int daysSince1970() {
        return getAgeInDaysAt(new Date(1, 1, 1970));
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