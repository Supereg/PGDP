/**
 * Created by Andi on 14.11.18.
 */
public class CRC {

    private int poly;

    public CRC(int poly) {
        this.poly = poly;
    }

    public int getDegree() {
        return poly;
    }

    public int crcASCIIStringTheGoodWay(String inputString) {
        int poly = this.poly;
        int polyLeadingZeros = Integer.numberOfLeadingZeros(poly);
        int polyDegree = 31 - polyLeadingZeros;

        int input = 0;

        for (byte b: inputString.getBytes()) {
            int leadingZeros = Integer.numberOfLeadingZeros(b);
            int shift = 32 - leadingZeros; // always 7 for a-z

            input = input << shift;
            input |= b;
        }

        input = input << polyDegree; // append polyDegree* 0 Bits

        int inputLeadingZeros = Integer.numberOfLeadingZeros(input);

        // align poly
        int polyDeltaLeadingZeros = polyLeadingZeros - inputLeadingZeros;
        if (polyDeltaLeadingZeros > 0) // TODO not completely though through
            poly = poly << polyDeltaLeadingZeros;
        // TODO can this be negative?!?

        System.out.println(Integer.toBinaryString(input));
        System.out.println(Integer.toBinaryString(poly));
        System.out.println();

        while (true) {
            System.out.println(Integer.toBinaryString(input));
            System.out.println(Integer.toBinaryString(poly));
            input = input ^ poly;
            System.out.println(Integer.toBinaryString(input));

            int nextLeadingZeros = Integer.numberOfLeadingZeros(input);
            if (nextLeadingZeros > polyLeadingZeros) {
                return input;
            }

            int deltaLeadingZeros = nextLeadingZeros - inputLeadingZeros;
            if (deltaLeadingZeros > 0) { // TODO deltaLeadingZeros MUST be positive, musn't it?
                inputLeadingZeros = nextLeadingZeros;
                // realign poly
                poly = poly >> deltaLeadingZeros;
            }

            System.out.println();

            try {// TODO remove
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // that's pretty ugly, I know
    public int crcASCIIString(String input) {
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
                System.out.println("RESULT0 " + result); // TODO remove
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

                if (leadingZeros > 0 && leftOverInputString.length() == 0) { // could be that this isn't necessary
                    System.out.println("RESULT1: " + result); // TODO remove
                    return Integer.parseInt(result, 2);
                }
            }

        }
    }

    private String binaryWithLeadingZeros(int n, int length) {
        String binary = Integer.toBinaryString(n);

        int leadingZeros = length - binary.length();

        while (leadingZeros > 0) {
            binary = "0" + binary;
            leadingZeros--;
        }

        return binary;
    }

    public static void main(String[] args) {
        CRC crc = new CRC(0B1001);

        int i = crc.crcASCIIStringTheGoodWay("my");
        System.out.println("\nResults:");
        System.out.println(i);
        System.out.println(Integer.toBinaryString(i));
    }

}