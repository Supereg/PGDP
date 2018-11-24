public class CRC {

    private final int poly;
    private final int degree;

    /**
     * Constructs a CRC object
     *
     * If {@code poly} is zero a default value of {@code 1} is used
     *
     * @param poly  polynom to use for calculations
     */
    public CRC(int poly) throws ArithmeticException {
        //if (poly == 0)
        //    throw new ArithmeticException("/ by zero");

        this.poly = poly != 0? poly: 0B1;
        this.degree = 31 - Integer.numberOfLeadingZeros(this.poly);
    }

    /**
     * Returns the degree of the polynom passed into the constructor.
     *
     * @return  the degree of the polynom passed into the constructor
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Verarbeitet ein weiteres Bit in der Berechnung des CRC-Wertes
     *
     * @param crc der bisherige CRC-Wert
     * @param bit das zu verarbeitende Bit
     * @return der neue CRC-Wert
     */
    private int crcBit(int crc, int bit) {
        // Wir hängen das Bit vorne an den bisherigen CRC-Wert
        crc = (crc << 1) | bit;
        // Ist der CRC-Wert groß genug geworden (genug Zahlen 'heruntergeholt' in der
        // der schriftlichen Division)...
        if (crc >>> degree != 0)
            // ... wenden wir die XOR-Operation an, ...
            return crc ^ poly;
        // ... sonst nicht.
        return crc;
    }

    /**
     * Erweitert die Berechnung des CRC-Wertes um 'degree' viele 0-Bits
     *
     * @param crc der bisherige CRC-Wert
     * @return der neue CRC-Wert
     */
    private int crcEnd(int crc) {
        for (int i = 0; i < degree; i++)
            crc = crcBit(crc, 0);
        return crc;
    }

    private int crcASCII(int crc, int symbol) {
        // Innerhalb eines Zeichens verarbeiten wir Bit für Bit
        for (int i = 0; i < 7; i++)
            crc = crcBit(crc, (symbol >>> (6 - i)) & 1);
        return crc;
    }

    public int crcASCIIString(String s) {
        if (s == null || s.isEmpty())
            return poly;

        int crc = 0;
        // Wir berechnen den CRC-Wert Zeichen für Zeichen
        for (int i = 0; i < s.length(); i++)
            crc = crcASCII(crc, s.charAt(i));
        return crcEnd(crc);
    }

}