package de.andi.minijava.codegen;

import de.andi.minijava.assembler.instructions.*;
import de.andi.minijava.assembler.operations.CompareOperation;
import de.andi.minijava.codegen.exceptions.UnsupportedOperationException;
import de.andi.minijava.codegen.exceptions.*;
import de.andi.minijava.codegen.utils.CGFunction;
import de.andi.minijava.codegen.utils.CGFunctionCall;
import de.andi.minijava.language.Call;
import de.andi.minijava.language.Number;
import de.andi.minijava.language.Return;
import de.andi.minijava.language.*;
import de.andi.minijava.language.operations.Bunop;
import de.andi.minijava.language.operations.Unop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerationVisitor implements ProgramVisitor {

    private final Program program;
    private final List<Instruction> instructionList = new ArrayList<>();

    private CGFunction currentFunction;
    private final Map<String, CGFunction> indexedFunctions;

    private final List<CGFunctionCall> generatedCalls = new ArrayList<>();
    private final List<Integer> generatedBreaks = new ArrayList<>(); // list of indices where break will be inserted;

    public CodeGenerationVisitor(Program program) {
        this.program = program;

        this.indexedFunctions = new HashMap<>(program.getFunctions().length);
    }

    public Instruction[] getProgram() {
        program.accept(this);
        return instructionList.toArray(new Instruction[0]);
    }

    @Override
    public void visit(Program program) {
        Function mainFunction = null;

        // indexing function names
        for (Function function: program.getFunctions()) {
            CGFunction cgFunction = new CGFunction(function.getName(), function.getParameters());
            if (indexedFunctions.put(function.getName(), cgFunction) != null) // assert that previous value is null
                throw new IllegalFunctionNameException(function.getName(), "name already exists");

            if (function.getName().equals("main")) // TODO main must not have parameters
                mainFunction = function;
        }

        if (mainFunction == null) // assert that there is a main function
            throw new FunctionNotFoundException("main");

        mainFunction.accept(this); // instructions should begin with main function for more cleaner assembly

        for (Function function: program.getFunctions()) {
            if (function.equals(mainFunction))
                continue;

            function.accept(this);
        }

        // replace call placeholders
        for (CGFunctionCall functionCall: generatedCalls) {
            CGFunction function = indexedFunctions.get(functionCall.getFunctionName());
            if (function == null) // debug
                throw new RuntimeException("function was null when replacing call placeholders: " + functionCall);

            instructionList.set(functionCall.getInstructionIndex(), new Ldi(function.getInstructionIndex()));
        }
    }

    @Override
    public void visit(Function function) {
        try {
            currentFunction = indexedFunctions.get(function.getName());
            if (currentFunction == null)
                throw new RuntimeException("currentFunction is null!"); // debug
            currentFunction.setInstructionIndex(instructionList.size()); // save index of start of function

            // TODO check double names in Decl AND parameters

            // check that no parameters overlap
            String[] parameters = function.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                // TODO j=i should work
                for (int j = i + 1; j < parameters.length; j++) {
                    if (i == j)
                        continue;

                    if (parameters[i].equals(parameters[j]))
                        throw new IllegalParameterNameException(parameters[i]);
                }
            }

            // parse declarations
            for (Declaration declaration: function.getDeclarations()) {
                for (String name: declaration.getNames()) // check that no declaration name occurs more than one time
                    if (!currentFunction.addDeclaration(name))
                        throw new IllegalDeclarationNameException(name);

                declaration.accept(this);
            }

            Statement statement = null;
            for (int i = 0; i < function.getStatements().length; i++) { // parse statements
                statement = function.getStatements()[i];
                statement.accept(this);
            }

            if (!(statement instanceof Return)) // check if function ends with return statement
                throw new MissingReturnStatementException(currentFunction.getName());
        } finally {
            currentFunction = null;
        }
    }

    @Override
    public void visit(Declaration declaration) {
        instructionList.add(new Decl(declaration.getNames().length));
    }


    @Override
    public void visit(Assignment assignment) {
        assignment.getExpression().accept(this);

        int variableNum = currentFunction.selectVariable(assignment.getName()); // throws VariableNotFoundException
        currentFunction.setDeclarationWritten(variableNum);

        instructionList.add(new Sts(variableNum));
    }

    @Override
    public void visit(Composite composite) {
        for (Statement statement: composite.getStatements())
            statement.accept(this);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(IfThen ifThen) {
        ifThen.getCond().accept(this);
        instructionList.add(new Not());// compare if condition is false -> we jump behind the THEN block
        int brcIndex = instructionList.size(); // save position at which BRC got inserted
        instructionList.add(new Nop()); // gets overwritten by brc instruction later

        ifThen.getThenBranch().accept(this);

        // this is the index which the brc will jump to
        int endIndex = instructionList.size();
        instructionList.set(brcIndex, new Brc(endIndex)); // update address for BRC
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(IfThenElse ifThenElse) {
        ifThenElse.getCond().accept(this);
        instructionList.add(new Not()); // compare if condition is false -> we jump to the ELSE block
        int brcIndex = instructionList.size(); // save position at which BRC got inserted
        instructionList.add(new Nop()); // gets overwritten by brc instruction later

        ifThenElse.getThenBranch().accept(this);
        instructionList.add(new Ldi(-1)); // load TRUE so we can use BRC to jump to the end
        int thenBrcIndex = instructionList.size();
        instructionList.add(new Nop()); // gets overwritten by brc instruction later

        // this is the index which the brc will jump to
        //  since BRC still needs to be inserted we need to add 1
        int elseBranchIndex = instructionList.size();
        instructionList.set(brcIndex, new Brc(elseBranchIndex));

        ifThenElse.getElseBranch().accept(this);
        int endIndex = instructionList.size();
        instructionList.set(thenBrcIndex, new Brc(endIndex));
    }

    @Override
    public void visit(While whileStatement) {
        int cmpIndex = instructionList.size();
        whileStatement.getCondition().accept(this);
        instructionList.add(new Ldi(0));
        int brcIndex = instructionList.size();
        instructionList.add(new Nop()); // gets overwritten by brc instruction later

        whileStatement.getBody().accept(this);
        instructionList.add(new Ldi(-1));
        instructionList.add(new Brc(cmpIndex));

        int endIndex = instructionList.size();
        instructionList.set(brcIndex, new Brc(endIndex));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Switch switchStatement) {
        switchStatement.getSwitchExpression().accept(this);
        instructionList.add(new Pop(0)); // save result

        for (SwitchCase switchCase: switchStatement.getCases()) {
            instructionList.add(new Push(0));
            instructionList.add(new Ldi(switchCase.getNumber()));
            instructionList.add(new Cmp(CompareOperation.EQUALS));
            instructionList.add(new Not()); // if not equals jump to next case

            int brcIndex = instructionList.size();
            instructionList.add(new Nop()); // BRC will be inserted here; jump to next case if number doesn't match

            switchCase.getCaseStatement().accept(this);
            instructionList.add(new Ldi(-1)); // load true (so we can jump)
            int endBrcIndex = instructionList.size();
            instructionList.add(new Nop()); // BRC will be inserted here; jump to next case at the end (if we haven't already jumped to the end)

            int nextCaseIndex = instructionList.size();
            instructionList.set(brcIndex, new Brc(nextCaseIndex));
            instructionList.set(endBrcIndex, new Brc(nextCaseIndex));
        }

        if (switchStatement.getDefault() != null)
            switchStatement.getDefault().accept(this);

        // generate breaks;
        int endIndex = instructionList.size();
        for (Integer breakIndex: generatedBreaks)
            instructionList.set(breakIndex, new Brc(endIndex));
        generatedBreaks.clear();
    }

    @Override
    public void visit(Return returnStatement) {
        returnStatement.getExpression().accept(this); // evaluate return statement

        if (currentFunction.isMain()) {
            instructionList.add(new Halt());
        }
        else {
            int variableAndArgumentCount = currentFunction.getParameterSize() +  currentFunction.getDeclarationSize();
            instructionList.add(new de.andi.minijava.assembler.instructions.Return(variableAndArgumentCount));
        }
    }

    @Override
    public void visit(Break breakStatement) {
        instructionList.add(new Ldi(-1)); // load true (so we can jump)
        int breakIndex = instructionList.size();
        instructionList.add(new Nop()); // BRC will be inserted here; we jump to the end of switch statement

        generatedBreaks.add(breakIndex);
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        expressionStatement.getExpression().accept(this);
    }


    @Override
    public void visit(Variable variable) {
        int variableNum = currentFunction.selectVariable(variable.getName()); // throws VariableNotFoundException

        if (!currentFunction.isDeclarationWritten(variableNum))
            throw new VariableNotInitiliazedException(variable.getName());

        instructionList.add(new Lfs(variableNum));
    }

    @Override
    public void visit(Number number) {
        instructionList.add(new Ldi(number.getValue()));
    }

    @Override
    public void visit(Binary binary) {
        binary.getRhs().accept(this);
        binary.getLhs().accept(this);

        switch (binary.getOperator()) {
            case Minus:
                instructionList.add(new Sub());
                break;
            case Plus:
                instructionList.add(new Add());
                break;
            case Modulo:
                instructionList.add(new Mod());
                break;
            case DivisionOperator:
                instructionList.add(new Div());
                break;
            case MultiplicationOperator:
                instructionList.add(new Mul());
                break;
            default:
                throw new UnsupportedOperationException(binary.getOperator().name());
        }
    }

    @Override
    public void visit(Unary unary) {
        unary.getOperand().accept(this);

        if (unary.getOperator().equals(Unop.Minus)) {
            instructionList.add(new Not());
            instructionList.add(new Ldi(1));
            instructionList.add(new Add());
        }
        else
            throw new UnsupportedOperationException(unary.getOperator().name());
    }

    @Override
    public void visit(Read read) {
        instructionList.add(new In());
    }

    @Override
    public void visit(Write write) {
        write.getExpression().accept(this);
        instructionList.add(new Out());
    }

    @Override
    public void visit(Call call) {
        CGFunction calledFunction = indexedFunctions.get(call.getFunctionName());

        if (calledFunction == null)
            throw new FunctionNotFoundException(call.getFunctionName());

        if (call.getArguments().length != calledFunction.getParameterSize())
            throw new BadArgumentSizeException(call.getFunctionName(), calledFunction.getParameterSize(), call.getArguments().length);

        for (Expression expression: call.getArguments())
            expression.accept(this);

        int ldiIndex = instructionList.size();
        instructionList.add(new Nop()); // will be replaced with new Ldi(functionAddress)
        instructionList.add(new de.andi.minijava.assembler.instructions.Call(call.getArguments().length));

        generatedCalls.add(new CGFunctionCall(call.getFunctionName(), ldiIndex));
    }


    @Override
    public void visit(True trueCondition) {
        instructionList.add(new Ldi(-1));
    }

    @Override
    public void visit(False falseCondition) {
        instructionList.add(new Ldi(0));
    }

    @Override
    public void visit(BinaryCondition binaryCondition) {
        binaryCondition.getRhs().accept(this);
        binaryCondition.getLhs().accept(this);

        switch (binaryCondition.getOperator()) {
            case Or:
                instructionList.add(new Or());
                break;
            case And:
                instructionList.add(new And());
                break;
            default:
                throw new UnsupportedOperationException(binaryCondition.getOperator().name());
        }
    }

    @Override
    public void visit(Comparison comparison) {
        if (comparison.getOperator().isRightToLeft()) {
            comparison.getRhs().accept(this);
            comparison.getLhs().accept(this);
        }
        else {
            comparison.getLhs().accept(this);
            comparison.getRhs().accept(this);
        }

        switch (comparison.getOperator()) {
            case Equals:
                instructionList.add(new Cmp(CompareOperation.EQUALS));
                break;
            case NotEquals:
                instructionList.add(new Cmp(CompareOperation.EQUALS));
                instructionList.add(new Not());
                break;
            case LessEqual:
            case GreaterEqual:
                instructionList.add(new Pop(0)); // save both values
                instructionList.add(new Pop(1)); // save both values

                instructionList.add(new Push(1)); // immediately restore both values for first cmp
                instructionList.add(new Push(0));

                instructionList.add(new Cmp(CompareOperation.LESS)); // order changed above depending on </>, we can use LESS
                instructionList.add(new Not()); // check if values are NOT LESS
                int additionalEqualsBrcIndex = instructionList.size();
                instructionList.add(new Nop()); // BRC will be inserted here; jump do additional check if values are NOT LESS

                instructionList.add(new Ldi(-1)); // if we reach here, o2 < o1 was TRUE, this LDI will be our "return" value
                instructionList.add(new Ldi(-1)); // value so we can jump around
                int endBrcIndex = instructionList.size();
                instructionList.add(new Nop()); // BRC will be inserted here; jump to end

                int equalsCheckIndex = instructionList.size();
                // additional equals check; push values to compare again
                instructionList.add(new Push(1));
                instructionList.add(new Push(0));
                // compare equals
                instructionList.add(new Cmp(CompareOperation.EQUALS));

                int endIndex = instructionList.size();

                // replacing BRCs
                instructionList.set(additionalEqualsBrcIndex, new Brc(equalsCheckIndex));
                instructionList.set(endBrcIndex, new Brc(endIndex));
                break;
            case Less:
            case Greater:
                instructionList.add(new Cmp(CompareOperation.LESS));
                break;
            default:
                throw new UnsupportedOperationException(comparison.getOperator().name());
        }
    }

    @Override
    public void visit(UnaryCondition unaryCondition) {
        unaryCondition.getOperand().accept(this);

        if (unaryCondition.getOperator() == Bunop.Not)
            instructionList.add(new Not());
        else
            throw new UnsupportedOperationException(unaryCondition.getOperator().name());
    }

}