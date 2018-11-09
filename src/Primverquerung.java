public class Primverquerung {

    public static int querPrim(int n) {
        if (n < 0)
            return 0;

        int sum = 0;

        for (int i = 2; i < n; i++) {
            int dividerFound = 0;

            // we search for every number which divides i. However we exclude 1 and i itself.
            for (int j = i - 1; j > 1; j--) {
                if (i % j == 0) {
                    dividerFound++;
                }
            }

            // if we found additional dividers except from i and 1, i isn't a prime number and we continue with the next one
            if (dividerFound != 0)
                continue;

            int digitSum = digitSum(i);

            if (digitSum % 2 == 0) {
                sum += i;
            }
        }

        return sum;
    }

    public static int digitSum(int n) {
        int sum = 0;

        while (n > 0) {
            int lastDigit = n % 10;

            sum += lastDigit;

            n /= 10; // "remove" last digit
        }

        return sum;
    }

    public static void main(String[] args) {
        testNegativeQuerPrim();
        testQuerPrim();
        testDigitSum();
    }

    public static void testNegativeQuerPrim() {
        int result = Primverquerung.querPrim(-3);

        if (result != 0)
            System.out.println("negativeQuerPrim test failed. Result was " + result);
        else
            System.out.println("negativeQuerPrim test succeeded!");
    }

    public static void testQuerPrim() {
        int result0 = Primverquerung.querPrim(20);

        if (result0 != 62)
            System.out.println("querPrim test failed. Result was " + result0);
        else
            System.out.println("querPrim test succeeded!");
    }

    public static void testDigitSum() {
        int result = Primverquerung.digitSum(628193);

        if (result != 29)
            System.out.println("digitSum test failed. Result was " + result);
        else
            System.out.println("digitSum test succeeded!");
    }

}
