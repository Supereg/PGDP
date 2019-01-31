package asm;

public interface AsmVisitor {

    void visit(Nop nop);

    void visit(Add add);

    void visit(Sub sub);

    void visit(Mul mul);

    void visit(Mod mod);

    void visit(Div div);

    void visit(And and);

    void visit(Or or);

    void visit(Not not);

    void visit(Ldi ldi);

    void visit(Lfs lfs);

    void visit(Sts sts);

    void visit(Brc brc);

    void visit(Cmp cmp);

    void visit(Call call);

    void visit(Decl decl);

    void visit(Return returnInstruction);

    void visit(In in);

    void visit(Out out);

    void visit(Push push);

    void visit(Pop pop);

    void visit(Halt halt);

    void visit(Alloc alloc);

    void visit(Lfh lfh);

    void visit(Sth sth);

    void visit(Fork fork);

    void visit(Join join);

    void visit(Lock lock);

    void visit(Unlock unlock);

}