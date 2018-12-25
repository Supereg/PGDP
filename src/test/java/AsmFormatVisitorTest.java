import de.andi.minijava.assembler.instructions.Add;
import de.andi.minijava.assembler.AsmFormatVisitor;
import de.andi.minijava.assembler.instructions.Instruction;
import de.andi.minijava.assembler.instructions.Ldi;
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

}