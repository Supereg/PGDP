package utils;

import org.junit.Test;
import utils.CRC;

import static org.junit.Assert.assertEquals;

public class CRCTest {

    @Test
    public void testPolyZero() {
        CRC crc = new CRC(0);

        assertEquals("CRC object did not handle pylnom '0' correctly", 0, crc.getDegree());
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
    public void testCrcASCIIEmptyString() {
        CRC crc = new CRC(0B101011);

        int result = crc.crcASCIIString("");
        assertEquals("Unexpected crc result for empty string", 0B101011, result);
    }

    @Test
    public void testCrcASCIINullString() {
        CRC crc = new CRC(0B101011);

        int result = crc.crcASCIIString(null);
        assertEquals("Unexpected crc result for null string", 0B101011, result);
    }

    @Test
    public void testCrcASCIIaz() {
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
    public void testCrcASCIIby() {
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
    public void testCrcASCIISensei() {
        CRC crc = new CRC(0B100110);

        int result = crc.crcASCIIString("SenseiZeigMirDenWeg");
        assertEquals("Unexpected crc result for 'SenseiZeigMirDenWeg'", 0B1100, result);
    }

    @Test
    public void testCrCcSCIIComplexArray9() {
        CRC crc = new CRC(0B10101110101);

        int result = crc.crcASCIIString("kjakjnsdjhbawdjhjkawdkjawdjknadwjwad");
        assertEquals("Unexpected crc result for 'kjakjnsdjhbawdjhjkawdkjawdjknadwjwad'", 0B110010100, result); // 404 not found LOL
    }

    @Test
    public void testCrcASCIIComplexPolynom() {
        CRC crc = new CRC(0B1001101010101010101111100);

        int result = crc.crcASCIIString("ab");
        assertEquals("Unexpected crc result for 'ab'", 0B111001110010100111101100, result);
    }

}