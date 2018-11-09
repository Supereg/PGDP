class StringulinaTest {

    public static void main(String[] args) {
        StringulinaTest test = new StringulinaTest();

        test.testSubstringPos();
        test.testCountSubstring();
        test.testCorrectlyBracketed();
        test.testResolvePattern();
        test.testMatches();
    }

    private void testSubstringPos() {
        boolean success = true;

        int result0 = Stringulina.substringPos("haalloal", "al");
        if (result0 != 2) {
            System.out.println("substringPos0 test failed. result was " + result0);
            success = false;
        }

        int result1 = Stringulina.substringPos("hallo", "x12");
        if (result1 != -1) {
            System.out.println("substringPos1 test failed. result was  " + result1);
            success = false;
        }

        if (success)
            System.out.println("substringPos test succeeded!");
    }

    private void testCountSubstring() {
        boolean success = true;

        int result0 = Stringulina.countSubstring("haalloal", "al");
        if (result0 != 2) {
            System.out.println("countSubstring0 test failed. result was " + result0);
            success = false;
        }

        int result1 = Stringulina.countSubstring("hallo", "x12");
        if (result1 != 0) {
            System.out.println("countSubstring1 test failed. result was " + result1);
            success = false;
        }

        if (success)
            System.out.println("countSubstring test succeeded!");
    }

    private void testCorrectlyBracketed() {
        boolean success = true;

        boolean result0 = Stringulina.correctlyBracketed("a(xx(]))");
        if (!result0) {
            System.out.println("correctlyBracketed0 test failed.");
            success = false;
        }

        boolean result1 = Stringulina.correctlyBracketed("a(xx))");
        if (result1) {
            System.out.println("correctlyBracketed1 test failed.");
            success = false;
        }

        boolean result2 = Stringulina.correctlyBracketed("a(xx)(");
        if (result2) {
            System.out.println("correctlyBracketed2 test failed.");
            success = false;
        }

        boolean result3 = Stringulina.correctlyBracketed("a)xx()(");
        if (result3) {
            System.out.println("correctlyBracketed3 test failed.");
            success = false;
        }

        if (success)
            System.out.println("correctlyBracketed test succeeded!");
    }

    private void testResolvePattern() {
        String result = Stringulina.resolvePattern("ab{4}.{3}jasd{1}");

        if (!result.equals("abbbb...jasd"))
            System.out.println("resolvePattern test failed.");
        else
            System.out.println("resolvePattern test succeeded!");
    }

    private void testMatches() {
        boolean success = true;

        boolean result0 = Stringulina.matches("Hallo123", "Hal{2}o.{3}");
        if (!result0) {
            System.out.println("matches0 test failed.");
            success = false;
        }

        boolean result1 = Stringulina.matches("Haloo123", "Hal{2}o.{3}");
        if (result1) {
            System.out.println("matches1 test failed.");
            success = false;
        }

        if (success)
            System.out.println("matches test succeeded!");
    }

}