public class Stringulina {

    public static int substringPos(String haystack, String needle) {
        int max = haystack.length() - needle.length(); // this is the maximum index we need to reach

        char first = needle.charAt(0);

        for (int i = 0; i <= max; i++) {
            char c = haystack.charAt(i);

            if (c == first) {
                // j is index for haystack, n is index for needle
                int n = 1;
                for (int j = i + 1; j <= haystack.length() && n < needle.length(); j++, n++) {
                    if (haystack.charAt(j) != needle.charAt(n)) {
                        break;
                    }
                }

                if (n == needle.length())
                    return i;
            }
        }

        return -1;
    }

    public static int countSubstring(String haystack, String needle) {
        int count = 0;

        int max = haystack.length() - needle.length();
        char first = needle.charAt(0);

        for (int i = 0; i <= max; i++) {
            char c = haystack.charAt(i);

            if (c == first) {
                int n = 1;
                for (int j = i + 1; j <= haystack.length() && n < needle.length(); j++, n++) {
                    if (haystack.charAt(j) != needle.charAt(n)) {
                        break;
                    }
                }

                if (n == needle.length())
                    count++;
            }
        }

        return count;
    }

    public static boolean correctlyBracketed(String str) {
        boolean bracketWasOpened = false;

        int openedBrackets = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '(') {
                bracketWasOpened = true;
                openedBrackets++;
            }
            else if (c == ')') {
                if (!bracketWasOpened)
                    return false; // we found a closing bracket though no bracket was opened

                openedBrackets--;
            }
        }

        return openedBrackets == 0;
    }

    public static boolean matches(String str, String pattern) {
        String resolvedPattern = resolvePattern(pattern);

        if (str.length() != resolvedPattern.length())
            return false;

        for (int i = 0; i < str.length(); i++) {
            char p = resolvedPattern.charAt(i);
            char s = str.charAt(i);

            if (p == '.') // exercise doesn't require to check if character 's' is actually valid
                continue;

            if (p != s) // found an invalid character => aborting
                return false;
        }

        return true;
    }

    static String resolvePattern(String pattern) {
        String resolvedPattern = "";

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            // we probably should check that the pattern has valid characters however we don't need to

            /*
                string concatenation in a loop is bad. However I don't know if we are allowed to use a class
                like StringBuilder in this exercise. Are we???
                But because this isn't explicitly allowed we use the bad method :(
             */
            //noinspection StringConcatenationInLoop
            resolvedPattern += c;

            if (pattern.charAt(i + 1) == '{') { // check if the next char is an bracket expression
                String digits = "";

                for (int d = i + 2; d < pattern.length(); d++) {
                    char digit = pattern.charAt(d);

                    if (digit == '}')
                        break;
                    else {
                        // we should probably check if this is indeed a digit.
                        // However exercise tells us we can expect those

                        // same as above
                        //noinspection StringConcatenationInLoop
                        digits += digit;
                    }
                }

                int multiplier = 0;
                try {
                    multiplier = Integer.valueOf(digits);
                } catch (NumberFormatException ignored) {} // cannot happen, as we can expect correct patterns
                multiplier--; // we subtract one from the multiplier since we already added one 'c' above

                while (multiplier > 0) {
                    //noinspection StringConcatenationInLoop
                    resolvedPattern += c;

                    multiplier--;
                }

                i += 2 + digits.length();
            }
        }

        return resolvedPattern;
    }

}
