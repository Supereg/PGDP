import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Andi on 15.11.18.
 */
public class CRCTest {

    @Test
    public void testPolyZero() {
        try {
            new CRC(0);
            fail("CRC object did not handle polynom of '0'");
        } catch (ArithmeticException ignored) {}
    }

    @Test
    public void testDegrees() {
        int mask = 0B1;

        int poly = mask;
        int expectedDegree = 0;

        for (int i = 0; i < 31; i++) {
            CRC crc = new CRC(poly);

            int degree = crc.getDegree();
            assertEquals("Unexpected degree for poly " + Integer.toBinaryString(poly), expectedDegree, degree);

            // increas poly degree
            poly <<= 1;
            poly |= mask;

            expectedDegree++;
        }
    }

    @Test
    public void testCrcASCIISimple() {
        CRC crc = new CRC(0B100110);

        int result = crc.crcASCIIString("az");
        assertEquals("Unexpected crc result for 'az'", 0B011010, result);
    }

    @Test
    public void testCrcASCIIComplex2Arrays() {
        CRC crc = new CRC(0B1001);

        int result = crc.crcASCIIString("mymymymy");
        assertEquals("Unexpected crc result for 'mymymymy'", 0B101, result);
    }

    @Test
    public void piazzaExample() {
        CRC crc = new CRC(0B10011);

        int result = crc.crcASCIIString("by");
        assertEquals("Unexpected crc result for 'by'", 0B1001, result);
    }

    @Test
    public void testCrcASCIIComplexArray4() {
        CRC crc = new CRC(0B1110101);

        int result = crc.crcASCIIString("mymyazasjhdkhasdu");
        assertEquals("Unexpected crc result for 'mymyazasjhdkhasdu'", 0B101100, result);
    }

    @Test
    public void testCrCASCIIComplexArray9() {
        CRC crc = new CRC(0B10101110101);

        int result = crc.crcASCIIString("kjakjnsdjhbawdjhjkawdkjawdjknadwjwad");
        assertEquals("Unexpected crc result for 'kjakjnsdjhbawdjhjkawdkjawdjknadwjwad'", 0B110010100, result); // 404 not found LOL
    }

}