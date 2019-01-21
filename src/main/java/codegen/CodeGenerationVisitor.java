package codegen;

import asm.*;
import codegen.exceptions.*;
import codegen.exceptions.UnsupportedOperationException;
import codegen.utils.CGFunction;
import codegen.utils.CGFunctionCall;

import java.util.*;

public class CodeGenerationVisitor implements ProgramVisitor {

    private final List<Instruction> instructionList = new ArrayList<>();

    private CGFunction currentFunction;
    private final Map<String, CGFunction> indexedFunctions;

    private final List<CGFunctionCall> generatedCalls = new ArrayList<>();
    private List<Integer> generatedBreaks; // list of indices where break will be inserted;

    public CodeGenerationVisitor() {
        this.indexedFunctions = new HashMap<>();
    }

    public CodeGenerationVisitor(Program program) {
        this.indexedFunctions = new HashMap<>(program.getFunctions().length);
        program.accept(this);
    }

    public Instruction[] getProgram() {
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

            if (function.getName().equals("main")) {
                if (function.getParameters().length != 0) // it's only the real main function if it has no parameters
                    continue;
                mainFunction = function;
            }
        }

        if (mainFunction == null) // assert that there is a main function
            throw new FunctionNotFoundException("main");

        // ADDING INSTRUCTIONS BEGINS HERE
        // program begins by jumping to main function which is always at index 3
        instructionList.add(new Ldi(3)); // 0:
        instructionList.add(new asm.Call(0)); // 1:
        instructionList.add(new Halt()); // 2:

        mainFunction.accept(this); // 3: instructions should begin with main function for more cleaner assembly

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

            // check that no parameters overlap
            for (String parameter: function.getParameters()) {
                if (!currentFunction.addParameter(parameter))
                    throw new IllegalParameterNameException(parameter);
            }
            // parse declarations
            for (Declaration declaration: function.getDeclarations()) {
                for (String name: declaration.getNames()) // check that no declaration name occurs more than one time
                    if (!currentFunction.addDeclaration(name)) // also checks that it not overlaps with parameter names
                        throw new IllegalDeclarationNameException(name);

                declaration.accept(this);
            }

            Statement statement;
            for (int i = 0; i < function.getStatements().length; i++) { // parse statements
                statement = function.getStatements()[i];
                statement.accept(this);
            }

            //if (!(statement instanceof codegen.Return)) // check if function ends with return statement
            //    throw new MissingReturnStatementException(currentFunction.getName());
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
        addNotInstruction(); // compare if condition is false -> we jump behind the THEN block
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
        addNotInstruction(); // compare if condition is false -> we jump to the ELSE block
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
        if (whileStatement.isDoWhile()) {
            int bodyIndex = instructionList.size();
            whileStatement.getBody().accept(this);

            whileStatement.getCondition().accept(this);
            instructionList.add(new Brc(bodyIndex));
        }
        else {
            int cmpIndex = instructionList.size();
            whileStatement.getCondition().accept(this);
            addNotInstruction();
            int brcIndex = instructionList.size();
            instructionList.add(new Nop()); // gets overwritten by brc instruction later

            whileStatement.getBody().accept(this);

            // jump to cmp again
            instructionList.add(new Ldi(-1)); // load true
            instructionList.add(new Brc(cmpIndex));

            // setting brc which jumps to the end
            instructionList.set(brcIndex, new Brc(instructionList.size()));
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void visit(Switch switchStatement) {
        List<Integer> oldGeneratedBreaks = this.generatedBreaks;
        this.generatedBreaks = new ArrayList<>();

        switchStatement.getSwitchExpression().accept(this);
        instructionList.add(new Pop(0)); // save result

        List<Integer> caseBrc = new ArrayList<>(switchStatement.getCases().length);
        for (SwitchCase switchCase: switchStatement.getCases()) {
            instructionList.add(new Push(0));
            switchCase.getNumber().accept(this);
            instructionList.add(new Cmp(CompareOperation.EQUALS));

            int brcIndex = instructionList.size();
            instructionList.add(new Nop());
            caseBrc.add(brcIndex);
        }
        instructionList.add(new Ldi(-1)); // load true for default case
        int defaultCaseBrc = instructionList.size();
        instructionList.add(new Nop());

        for (SwitchCase switchCase: switchStatement.getCases()) {
            instructionList.set(caseBrc.remove(0), new Brc(instructionList.size()));
            switchCase.getCaseStatement().accept(this);
        }

        instructionList.set(defaultCaseBrc, new Brc(instructionList.size()));
        if (switchStatement.getDefault() != null)
            switchStatement.getDefault().accept(this);

        for (Integer breaks: generatedBreaks)
            instructionList.set(breaks, new Brc(instructionList.size()));

        this.generatedBreaks = oldGeneratedBreaks;
    }

    @Override
    public void visit(codegen.Return returnStatement) {
        returnStatement.getExpression().accept(this); // evaluate return statement
        int variableAndArgumentCount = currentFunction.getParameterSize() +  currentFunction.getDeclarationSize();
        instructionList.add(new asm.Return(variableAndArgumentCount));
    }

    @Override
    public void visit(Break breakStatement) {
        instructionList.add(new Ldi(-1)); // load true (so we can jump)
        if (generatedBreaks == null)
            throw new BreakOutsideSwitchException();

        int breakIndex = instructionList.size();
        instructionList.add(new Nop()); // BRC will be inserted here; we jump to the end of switch statement

        generatedBreaks.add(breakIndex);
    }

    @Override
    public void visit(ExpressionStatement expressionStatement) {
        expressionStatement.getExpression().accept(this);
        instructionList.add(new Pop(0));
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
                throw new codegen.exceptions.UnsupportedOperationException(binary.getOperator().name());
        }
    }

    @Override
    public void visit(Unary unary) {
        unary.getOperand().accept(this);

        if (unary.getOperator().equals(Unop.Minus)) {
            instructionList.add(new Not());
            instructionList.add(new Ldi(1));
            instructionList.add(new Add());
            /* Musterlösung:
            instructionList.add(new Ldi(0));
            instructionList.add(new Sub());
             */
        }
        else
            throw new codegen.exceptions.UnsupportedOperationException(unary.getOperator().name());
    }

    @Override
    public void visit(Read read) {
        instructionList.add(new In());
    }

    @Override
    public void visit(Write write) {
        write.getExpression().accept(this);
        instructionList.add(new Out());
        instructionList.add(new Ldi(0));
    }

    @Override
    public void visit(codegen.Call call) {
        CGFunction calledFunction = indexedFunctions.get(call.getFunctionName());

        if (calledFunction == null)
            throw new FunctionNotFoundException(call.getFunctionName());

        if (call.getArguments().length != calledFunction.getParameterSize())
            throw new BadArgumentSizeException(call.getFunctionName(), calledFunction.getParameterSize(), call.getArguments().length);

        for (Expression expression: call.getArguments())
            expression.accept(this);

        int ldiIndex = instructionList.size();
        instructionList.add(new Nop()); // will be replaced with new Ldi(functionAddress)
        instructionList.add(new asm.Call(call.getArguments().length));

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
                throw new codegen.exceptions.UnsupportedOperationException(binaryCondition.getOperator().name());
        }
    }

    @Override
    public void visit(Comparison comparison) {
        if (comparison.getOperator().isSwitchInput()) {
            comparison.getLhs().accept(this);
            comparison.getRhs().accept(this);
        }
        else {
            comparison.getRhs().accept(this);
            comparison.getLhs().accept(this);
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
                instructionList.add(new Cmp(CompareOperation.LESS));
                instructionList.add(new Not());
                break;
            case Less:
            case Greater:
                instructionList.add(new Cmp(CompareOperation.LESS));
                break;
            default:
                throw new codegen.exceptions.UnsupportedOperationException(comparison.getOperator().name());
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

    private void addNotInstruction() {
        Instruction lastOne = instructionList.get(instructionList.size() - 1);

        if (lastOne instanceof Not)
            instructionList.remove(instructionList.size() - 1);
        else
            instructionList.add(new Not());
    }

}