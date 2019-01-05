import de.andi.minijava.assembler.Interpreter;
import de.andi.minijava.assembler.exceptions.*;
import de.andi.minijava.assembler.exceptions.ArithmeticException;
import de.andi.minijava.assembler.instructions.*;
import de.andi.minijava.assembler.operations.CompareOperation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalProgram() {
        run(null);
    }

    @Test
    public void testLdi() {
        Instruction[] instructions = {
                new Ldi(12),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(12, out);
    }

    @Test
    public void testAdd() {
        Instruction[] instructions = {
                new Ldi(12),
                new Ldi(13),
                new Add(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(25, out);
    }

    @Test
    public void testSub() {
        Instruction[] instructions = {
                new Ldi(2),
                new Ldi(3),
                new Sub(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(1, out);
    }

    @Test
    public void testMul() {
        Instruction[] instructions = {
                new Ldi(2),
                new Ldi(3),
                new Mul(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(6, out);
    }

    @Test
    public void testMod() {
        Instruction[] instructions = {
                new Ldi(3),
                new Ldi(32),
                new Mod(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(2, out);
    }

    @Test
    public void testDiv() {
        Instruction[] instructions = {
                new Ldi(3),
                new Ldi(32),
                new Div(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(10, out);
    }

    @Test(expected = ArithmeticException.class)
    public void testDivByZero() {
        Instruction[] instructions = {
                new Ldi(0),
                new Ldi(120),
                new Div(),
                new Halt()
        };

        run(instructions);
    }

    @Test
    public void testAnd() {
        Instruction[] instructions = {
                new Ldi(0B0100111),
                new Ldi(0B1101010),
                new And(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(0B0100010, out);
    }

    @Test
    public void testOr() {
        Instruction[] instructions = {
                new Ldi(0B0100111),
                new Ldi(0B1101010),
                new Or(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(0B1101111, out);
    }

    @Test
    public void testNot() {
        Instruction[] instructions = {
                new Ldi(0B0100111),
                new Not(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(0B11111111111111111111111111011000, out);
    }

    @Test
    public void testPushAndPop() {
        Instruction[] instructions = {
                new Ldi(12),
                new Ldi(23),
                new Pop(1), // r1 = 23
                new Push(0), // r0=0 push to stack
                new Add(), // 12 + 0
                new Pop(0), // r1 = 12
                new Push(1),
                new Push(0),
                new Mod(),
                new Halt()
        };

        int out = run(instructions);
        assertEquals(12, out);
    }

    @Test(expected = IllegalRegisterException.class)
    public void testPopToIllegalRegister() {
        Instruction[] instructions = {
                new Ldi(1),
                new Pop(3),
                new Halt()
        };

        run(instructions);
    }

    @Test(expected = IllegalRegisterException.class)
    public void testPushFromIllegalRegister() {
        Instruction[] instructions = {
                new Push(3),
                new Halt()
        };

        run(instructions);
    }

    @Test
    public void testCmp() {
        Instruction[] instructions0 = {
                new Ldi(123),
                new Ldi(100),
                new Cmp(CompareOperation.LESS),
                new Halt()
        };

        assertEquals(-1, run(instructions0));

        Instruction[] instructions1 = {
                new Ldi(100),
                new Ldi(100),
                new Cmp(CompareOperation.EQUALS),
                new Halt()
        };

        assertEquals(-1, run(instructions0));
    }

    @Test
    public void testBrc() {
        Instruction[] instructions = {
                new Ldi(734), // 0
                new Ldi(28), // 1
                new Ldi(14), // 2
                new Cmp(CompareOperation.LESS), // 3
                new Brc(7), // 4
                new Ldi(1), // 5
                new Add(), // 6
                new Halt() // 7
        };

        assertEquals(734, run(instructions));
    }

    @Test
    public void testBasicCall() {
        Instruction[] instructions = {
                new Ldi(42),
                new Ldi(99),
                new Ldi(5),
                new Call(2),
                new Halt(),
                new Lfs(-1),
                new Lfs(0),
                new Add(),
                new Return(2)
        };

        assertEquals(141, run(instructions));
    }

    @Test(expected = IllegalDeclarationException.class)
    public void testIllegalDecl() {
        Instruction[] instructions = {
                new Ldi(22),
                new Ldi(11),
                new Ldi(5),
                new Call(2),
                new Halt(),
                new Decl(2),
                new Decl(1),
                new Lfs(1),
                new Decl(1),
                new Lfs(2),
                new Add(),
                new Return(6)
        };

        run(instructions);
    }

    @Test
    public void testGGT() {
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
                new Return(3)
        };

        assertEquals(2, run(instructions));
    }

    @Test
    public void testFAC() {
        int[] expected = {
                1,
                1,
                2,
                6,
                24,
                120
        };

        for (int i = 1; i < 6; i++) { // fac not made for fac(0)
            Instruction[] instructions = {
                    new Ldi(i),
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

            assertEquals("Unexpected result for fac(" + i + ")", expected[i], run(instructions));
        }
    }

    @Test(expected = IllegalProgramCounterException.class)
    public void illegalJumpNegative() {
        Instruction[] instructions = {
                new Ldi(-1), // load true
                new Brc(-1)
        };

        run(instructions);
    }

    @Test(expected = IllegalProgramCounterException.class)
    public void illegalJumpPositive() {
        Instruction[] instructions = {
                new Ldi(-1), // load true
                new Brc(128)
        };

        run(instructions);
    }

    @Test(expected = StackEmptyException.class)
    public void testStackEmpty() {
        Instruction[] instructions = {
                new Pop(0)
        };

        run(instructions);
    }

    @Test(expected = StackOverflowException.class)
    public void testStackOverflow() {
        Instruction[] instructions = new Instruction[129];
        for (int i = 0; i < instructions.length; i++) {
            instructions[i] = new Ldi(1);
        }

        run(instructions);
    }

    @Test(expected = IllegalProgramCounterException.class)
    public void testMissingHalt() {
        Instruction[] instructions = {
                new Ldi(3),
                new Ldi(2),
                new Add()
        };

        run(instructions);
    }

    @Test(expected = IllegalVariableException.class)
    public void testIllegaleSAS() {
        Instruction[] instructions = {
                new Decl(1),
                new Ldi(23),
                new Sts(0),
                new Halt()
        };

        run(instructions);
    }

    @Test(expected = IllegalVariableException.class)
    public void testIllegalLFS() {
        Instruction[] instructions = {
                new Decl(1),
                new Ldi(1),
                new Sts(1),
                new Lfs(0),
                new Halt()
        };

        run(instructions);
    }

    private int run(Instruction[] instructions) {
        Interpreter interpreter = new Interpreter(instructions);
        return interpreter.execute();
    }

}