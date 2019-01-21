package codegen.utils;

import java.util.Objects;

public class CGDeclaration {

    private final String name;
    private boolean written;

    public CGDeclaration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGDeclaration that = (CGDeclaration) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}