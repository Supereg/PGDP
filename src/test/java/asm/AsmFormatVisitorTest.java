package asm;

import asm.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsmFormatVisitorTest {

    @Test
    public void testBasic() {
        Instruction[] instructions = {
                new Ldi(33),
                new Ldi(11),
                new Add()
        };

        AsmFormatVisitor visitor = new AsmFormatVisitor(instructions);
        String format = visitor.getFormattedCode();

        assertEquals("Unexpected format for assembler",
                "0: LDI 33\n" +
                        "1: LDI 11\n" +
                        "2: ADD", format);
    }

    @Test
    public void testFac() {
        Instruction[] instructions = {
                new In(),
                new Ldi(4), // Adresse von Methode fak laden
                new Call(1),
                new Halt(),

                // Methode fak, 1 Argument (n)
                // Ist n == 1?
                new Ldi(1), // 4
                new Lfs(0),
                new Cmp(CompareOperation.EQUALS),
                new Not(),
                new Brc(11), // Sprung zum rekursiven Aufruf
                new Ldi(1),
                new Return(1),
                // Rekursiver Aufruf
                new Ldi(1), // 1 laden
                new Lfs(0), // n laden
                new Sub(), // n - 1 berechnen
                new Ldi(4), // Adresse von Methode fak laden
                new Call(1), // fak(n - 1)
                new Lfs(0), // n laden (für n *)
                new Mul(), // n * fak(n - 1) berechnen
                new Return(1),
        };

        assertEquals("Unexpected format for assembler", "0: IN\n" +
                "1: LDI 4\n" +
                "2: CALL 1\n" +
                "3: HALT\n" +
                "4: LDI 1\n" +
                "5: LFS 0\n" +
                "6: CMP EQUALS\n" +
                "7: NOT\n" +
                "8: BRC 11\n" +
                "9: LDI 1\n" +
                "10: RETURN 1\n" +
                "11: LDI 1\n" +
                "12: LFS 0\n" +
                "13: SUB\n" +
                "14: LDI 4\n" +
                "15: CALL 1\n" +
                "16: LFS 0\n" +
                "17: MUL\n" +
                "18: RETURN 1", format(instructions));
    }

    @Test
    public void testGgt() {
        Instruction[] instructions = {
                // a und b einlesen und auf dem Stack speichern
                new Ldi(6),
                new Ldi(38),
                // Methode ggt laden und mit zwei Argumenten aufrufen;
                // der Rückgabewert landet wiederum auf dem Stack.
                new Ldi(5), // Adresse von Methode ggt laden; 15->5
                new Call(2),
                // Rückgabewert des Aufrufs vom Stack nehmen und dem Nutzer ausgeben
                // new Out(),
                // Programm beenden
                //new Ldi(0),
                new Halt(), // returns result

                // Methode ggt mit 2 Argumenten (a bei -1 im Stack-Frame, b bei 0)
                // Eine lokale Variable anlegen
                new Decl(1), // 5
                // Tausch von größerer Zahl nach vorne
                // Lade Argument a auf den Stack
                new Lfs(-1),
                // Lade Argument b auf den Stack
                new Lfs(0),
                // Ist !(b >= a), also b < a, ...
                new Cmp(CompareOperation.LESS),
                new Brc(14), // ... zur Hauptschleife springen; updated 31->14
                // Sonst: Tauschen von a und b
                new Lfs(0),
                new Lfs(-1),
                new Sts(0),
                new Sts(-1),
                // Hauptschleife
                // temp = b
                new Lfs(0), // 14
                new Sts(1),
                // b = a % b
                new Lfs(0),
                new Lfs(-1),
                new Mod(),
                new Sts(0),
                // a = temp
                new Lfs(1),
                new Sts(-1),
                // Schleifenbedingung auswerten
                new Ldi(0),
                new Lfs(0),
                new Cmp(CompareOperation.EQUALS),
                new Not(),
                // Rücksprung zur Schleife, wenn Bedingung hält
                new Brc(14), // 31->14
                // Rückgabewert auf den Stack legen
                new Lfs(-1),
                // Wir geben zwei Argumente und eine lokale Variable frei
                new Nop(),
                new Return(3)
        };

        assertEquals("Unexpected format for assembler", "0: LDI 6\n" +
                "1: LDI 38\n" +
                "2: LDI 5\n" +
                "3: CALL 2\n" +
                "4: HALT\n" +
                "5: DECL 1\n" +
                "6: LFS -1\n" +
                "7: LFS 0\n" +
                "8: CMP LESS\n" +
                "9: BRC 14\n" +
                "10: LFS 0\n" +
                "11: LFS -1\n" +
                "12: STS 0\n" +
                "13: STS -1\n" +
                "14: LFS 0\n" +
                "15: STS 1\n" +
                "16: LFS 0\n" +
                "17: LFS -1\n" +
                "18: MOD\n" +
                "19: STS 0\n" +
                "20: LFS 1\n" +
                "21: STS -1\n" +
                "22: LDI 0\n" +
                "23: LFS 0\n" +
                "24: CMP EQUALS\n" +
                "25: NOT\n" +
                "26: BRC 14\n" +
                "27: LFS -1\n" +
                "28: NOP\n" +
                "29: RETURN 3", format(instructions));
    }

    @Test
    public void randomInstructions() {
        Instruction[] instructions = {
                new Div(),
                new And(),
                new Or(),
                new Out(),
                new Push(1),
                new Push(0),
                new Pop(0),
                new Pop(1)
        };

        assertEquals("Unexpected format for assembler", "0: DIV\n" +
                "1: AND\n" +
                "2: OR\n" +
                "3: OUT\n" +
                "4: PUSH 1\n" +
                "5: PUSH 0\n" +
                "6: POP 0\n" +
                "7: POP 1", format(instructions));
    }

    private String format(Instruction[] instructions) {
        AsmFormatVisitor visitor = new AsmFormatVisitor(instructions);
        return visitor.getFormattedCode();
    }

}