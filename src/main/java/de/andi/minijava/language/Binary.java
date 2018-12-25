package de.andi.minijava.language;

import de.andi.minijava.language.operations.Binop;

public class Binary extends Expression {

    private Expression lhs; // TODO not null
    private Binop operator; // TODO not null
    private Expression rhs; // TODO not null

    public Binary(Expression lhs, Binop operator, Expression rhs) {
        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public Expression getLhs() {
        return lhs;
    }

    public Binop getOperator() {
        return operator;
    }

    public Expression getRhs() {
        return rhs;
    }

    @Override
    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }

}