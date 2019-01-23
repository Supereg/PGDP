package codegen;

import java.util.Objects;

public class Parameter {

  private Type type;
  private String name;
  
  public Parameter(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Parameter parameter = (Parameter) o;
    return Objects.equals(name, parameter.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

}
