package utils;

public class Stringulina {

    /**
     * Returns the index at which the String {@code needle} occurs the first time in the String {@code haystack}.
     *
     * @param haystack  the {@code String} to search in
     * @param needle    the {@code String} to search for in {@code haystack}
     * @return          index of {@code needle} in {@code haystack}.
     *                  If {@code needle} is empty or not found {@code -1} is returned
     */
    public static int substringPos(String haystack, String needle) {
        if (needle.length() == 0)
            return -1;

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

    /**
     * Counts how many times {@code needle} appears in {@code haystack}
     *
     * @param haystack  the {@code String} to search in
     * @param needle    the {@code String} to search for
     * @return          how often {@code needle} appeared in {@code haystack}.
     *                  If {@code needle} is empty {@code -1} is returned
     */
    public static int countSubstring(String haystack, String needle) {
        if (needle.length() == 0)
            return -1;

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

    /**
     * Checks if {@code str} is correctly bracketed
     *
     * @param str   the {@code String} to check
     * @return      if {@code str} is correctly bracketed {@code true} is returned otherwise {@code false} is returned.
     *              A empty {@code str} is considered correctly bracketed!
     */
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

    private static String resolvePattern(String pattern) {
        StringBuilder resolvedPattern = new StringBuilder();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            // we probably should check that the pattern has valid characters however we don't need to

            if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '{') { // check if the next char is an bracket expression
                StringBuilder digits = new StringBuilder();

                for (int d = i + 2; d < pattern.length(); d++) {
                    char digit = pattern.charAt(d);

                    if (digit == '}')
                        break;

                    // we should probably check if this is indeed a digit.
                    // However exercise tells us we can expect those

                    // same as above
                    digits.append(digit);
                }

                int multiplier = 0;
                try {
                    multiplier = Integer.valueOf(digits.toString());
                } catch (NumberFormatException ignored) {} // cannot happen, as we can expect correct patterns

                // directly ensure enough space is there (useful for long patterns)
                resolvedPattern.ensureCapacity(resolvedPattern.length() + multiplier);

                while (multiplier > 0) {
                    resolvedPattern.append(c);

                    multiplier--;
                }

                i += 2 + digits.length();
            }
            else
                resolvedPattern.append(c);
        }

        return resolvedPattern.toString();
    }

}
