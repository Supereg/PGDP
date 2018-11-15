public class CRC {

    private final int poly;

    /**
     * Constructs a CRC object
     *
     * @param poly  polynom to use for calculations
     * @throws ArithmeticException  if poly = {@code 0}
     */
    public CRC(int poly) throws ArithmeticException {
        if (poly == 0)
            throw new ArithmeticException("/ by zero");

        this.poly = poly;
    }

    /**
     * Returns the degree of the polynom passed into the constructor.
     *
     * @return  the degree of the polynom passed into the constructor
     */
    public int getDegree() {
        return 31 - Integer.numberOfLeadingZeros(this.poly);
    }

    /**
     * Calculates the crc for a given {@code inputString}
     *
     * A {@code null} {@code inputString} is treated as a empty string
     *
     * @param inputString   a {@link String} used for the crc calculation
     * @return              the crc value
     */
    public int crcASCIIString(String inputString) {
        if (inputString == null || inputString.isEmpty()) // 0 XOR poly == poly (null treated as "")
            return poly;

        byte[] inputBytes = inputString.getBytes();
        int polyDegree = 31 - Integer.numberOfLeadingZeros(this.poly);

        // all asciis have 7-Bit (for variable stuff we need to calculate the total length first)
        final int totalBytesRequired = inputBytes.length * 7 + polyDegree;
        final int arraySize = (totalBytesRequired / 32) + 1;

        int[] inputArray = new int[arraySize];

        int[] polyArray = new int[arraySize];
        polyArray[0] = this.poly;

        for (byte b: inputBytes) { // fill input bytes into inputArray
            // int leadingZeros = Integer.numberOfLeadingZeros(b);
            // int shift = 32 - leadingZeros;
            int shift = 7; // always 7 for a-z A-Z

            arrayShiftLeft(inputArray, shift);
            inputArray[0] |= b;
        }

        arrayShiftLeft(inputArray, polyDegree); // append polyDegree * 0 Bits

        // align polyArray
        int inputLeadingZeros = arrayNumberOfLeadingZeros(inputArray);
        int polyLeadingZeros = arrayNumberOfLeadingZeros(polyArray);
        int polyDeltaLeadingZeros = polyLeadingZeros - inputLeadingZeros;
        // polyDeltaLeadingZeros must be > 0 since input is always "longer" than "poly" because "input" has the minimum
        // length of "polyDegree + 7"
        assert polyDeltaLeadingZeros > 0;
        arrayShiftLeft(polyArray, polyDeltaLeadingZeros);

        while (true) {
            arrayXOR(inputArray, polyArray);

            int nextLeadingZeros = arrayNumberOfLeadingZeros(inputArray);
            if (nextLeadingZeros > polyLeadingZeros)
                return inputArray[0];

            int deltaLeadingZeros = nextLeadingZeros - inputLeadingZeros;
            // deltaLeadingZeros MUST be positive: since poly is aligned with input both have a 1 at this position
            // => thus deltaLeadingZeros has a minimum value of 1 every round
            assert deltaLeadingZeros > 0;

            inputLeadingZeros = nextLeadingZeros;
            // realign poly
            arrayShiftRight(polyArray, deltaLeadingZeros);
        }
    }

    private static void arrayXOR(int[] result, int[] array) {
        if (result.length != array.length)
            throw new IllegalArgumentException("array size differ");

        for (int i = 0; i < result.length; i++)
            result[i] = result[i] ^ array[i];
    }

    private static void arrayShiftLeft(int[] array, int amount) {
        if (amount == 0)
            return;

        if (amount > 31) {
            int n = amount / 31;
            for (int i = 0; i < n; i++)
                arrayShiftLeft(array, 31);

            arrayShiftLeft(array, amount % 31);
            return;
        }

        int maskShiftAmount = 32 - amount;

        int upperBitsMask = -1 >>> maskShiftAmount;
        upperBitsMask <<= maskShiftAmount;

        int lowerBits = 0;
        for (int i = 0; i < array.length; i++) {
            int upperBits = array[i] & upperBitsMask; // saving upper bits

            array[i] <<= amount; // shift amount

            array[i] |= lowerBits; // apply lower bits (were the upper bits of last element)
            lowerBits = upperBits >>> maskShiftAmount; // move upperBits to the far right => lower bits for the next element
        }
    }

    private static void arrayShiftRight(int[] array, int amount) {
        if (amount == 0)
            return;

        if (amount > 31) {
            int n = amount / 31;
            for (int i = 0; i < n; i++)
                arrayShiftRight(array, 31);

            arrayShiftRight(array, amount % 31);
            return;
        }

        int maskShiftAmount = 32 - amount;

        int lowerBitMask = -1 << maskShiftAmount;
        lowerBitMask >>>= maskShiftAmount;

        int upperBits = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            int lowerBits = array[i] & lowerBitMask; // saving lower bits

            array[i] >>>= amount; // shift right by amount

            array[i] |= upperBits; // apply upper bits (were the lower bits of last element)
            upperBits = lowerBits << maskShiftAmount; // mover lowerBits to the far left => upperBits for the next element
        }
    }

    private static int arrayNumberOfLeadingZeros(int[] array) {
        int sum = 0;

        for (int i = array.length - 1; i >= 0; i--) {
            int zeros = Integer.numberOfLeadingZeros(array[i]);
            sum += zeros;

            if (zeros < 32)
                break;
        }

        return sum;
    }

    // that's pretty ugly, I know
    public int crcASCIIStrinCrappyWay(String input) {
        int polyDegree = 31 - Integer.numberOfLeadingZeros(poly);

        byte[] inputBytes = input.getBytes();

        StringBuilder binaryInputStringBuilder = new StringBuilder();
        for (byte b: inputBytes) {
            binaryInputStringBuilder.append(Integer.toBinaryString(b));
        }

        for (int i = 0; i < polyDegree; i++)
            binaryInputStringBuilder.append("0");


        String poly = Integer.toBinaryString(this.poly);
        String binaryInputString = binaryInputStringBuilder.toString();

        String operationString = binaryInputString.substring(0, poly.length());
        String leftOverInputString = binaryInputString.substring(poly.length());

        while (true) {
            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < poly.length(); i++) {
                if (poly.charAt(i) != operationString.charAt(i))
                    resultBuilder.append("1");
                else
                    resultBuilder.append("0");
            }

            String result = resultBuilder.toString();

            if (leftOverInputString.length() == 0) {
                return Integer.parseInt(result, 2);
            }

            int leadingZeros = 0;

            for (int i = 0; i < result.length(); i++) {
                char c = result.charAt(i);

                if (c == '0')
                    leadingZeros++;
                else
                    break;
            }

            operationString = result;

            while (leadingZeros > 0) {
                operationString = operationString.substring(1); // remove one zero
                operationString += leftOverInputString.charAt(0); // add first character
                leftOverInputString = leftOverInputString.substring(1); // remove first character which got added

                leadingZeros--;

                if (leadingZeros > 0 && leftOverInputString.length() == 0) // could be that this isn't necessary
                    return Integer.parseInt(operationString, 2);
            }

        }
    }

    /* Debug helper methods
    private static String binaryWithLeadingZeros_32(int n) {
        String binary = Integer.toBinaryString(n);

        int leadingZeros = 32 - binary.length();

        while (leadingZeros > 0) {
            binary = "0" + binary;
            leadingZeros--;
        }

        return binary;
    }

    private static void printBinaryArray(int[] array) {
        StringBuilder builder = new StringBuilder("[");

        for (int i = array.length - 1; i >= 0; i--) {
            builder.append(binaryWithLeadingZeros_32(array[i]))
                    .append(", ");
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);

        builder.append("]");

        System.out.println(builder);
    }
    */

}