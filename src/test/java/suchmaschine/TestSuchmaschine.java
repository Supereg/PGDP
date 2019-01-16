package suchmaschine;

import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class TestSuchmaschine {
  private ArrayList<String> reqAttr;

  @Before
  public void init() {
    reqAttr = new ArrayList<>();
    reqAttr.add("suchmaschine.Author");
    reqAttr.add("suchmaschine.Date");
    reqAttr.add("suchmaschine.Review");
    reqAttr.add("suchmaschine.Document");
  }

  @org.junit.Test
  public void CheckGetter() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test HA2.1: tCheckGetter");
    for (int i = 0; i < reqAttr.size(); i++) {
      Class clazz = Class.forName(reqAttr.get(i));
      Field[] fields = clazz.getDeclaredFields();
      Method[] methods = clazz.getMethods();
      boolean atLeastOneSetter = false;
      for (Method m : methods) {
//				consider only protected or public setter methods
        if (Modifier.isPrivate(m.getModifiers())) {
          continue;
        } else if (!m.getName().toLowerCase().startsWith("get")) {
          continue;
        }
        atLeastOneSetter = true;
        String fieldNameInMethod = m.getName().toLowerCase().substring(3);
        Field fHit = null;
//				Find field for setter-Method
        for (Field f : fields) {
          if (f.getName().toLowerCase().equals(fieldNameInMethod)) {
            fHit = f;
            break;
          }
        }

//				Class contains no proper field for setter method
        if (fHit != null) {
//					sb.append("\n- Kein Attribut für "+clazz.getName()+"."+m.getName()+" gefunden.");
//					right = false;
//				} else {
          Class[] methParamTypes = m.getParameterTypes();
          Class returnType = m.getReturnType();
//					getter-method has wrong signature
          if ((methParamTypes.length > 0)
              || !returnType.getName().toLowerCase().equals(fHit.getType().getName().toLowerCase())) {
            right = false;
            sb.append("\n- Falsche Signatur für Getter-Methode " + clazz.getName() + "." + m.getName());
            continue;
          }
        }
      }

//			Class contains no getter methods-> somehow strange
      if (!atLeastOneSetter) {
        sb.append("\n- Klasse " + clazz.getName() + " enthält keine einzige Getter Methode!");
        right = false;
      }
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckSetter() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test HA2.1: tCheckSetter");
    for (int i = 0; i < reqAttr.size(); i++) {
      Class clazz = Class.forName(reqAttr.get(i));
      Field[] fields = clazz.getDeclaredFields();
      Method[] methods = clazz.getMethods();
      boolean atLeastOneSetter = false;
      for (Method m : methods) {
//				consider only protected or public setter methods
        if (Modifier.isPrivate(m.getModifiers())) {
          continue;
        } else if (!m.getName().toLowerCase().startsWith("set")) {
          continue;
        }
        atLeastOneSetter = true;
        String fieldNameInMethod = m.getName().toLowerCase().substring(3);
        Field fHit = null;
//				Find field for setter-Method
        for (Field f : fields) {
          if (f.getName().toLowerCase().equals(fieldNameInMethod)) {
            fHit = f;
            break;
          }
        }

//				Class contains no proper field for setter method
        if (fHit != null) {
//					sb.append("\n- Kein Attribut für "+clazz.getName()+"."+m.getName()+" gefunden.");
//					right = false;
//				}else{
          Class[] methParamTypes = m.getParameterTypes();
          Class returnType = m.getReturnType();
          Class cHit = null;
//					setter-method has wrong signature
          if ((methParamTypes.length == 0) || !returnType.getName().toLowerCase().equals("void")) {
            right = false;
            sb.append("\n- Falsche Signatur für Setter-Methode " + clazz.getName() + "." + m.getName());
            continue;
          }

//					Look if a paramter exists that has the same data type as the corresponding class attribute
          for (Class c : methParamTypes) {
            if (c.getName().toLowerCase().equals(fHit.getType().getName().toLowerCase())) {
              cHit = c;
              break;
            }
          }
          if (cHit == null) {
            sb.append("\n- Methode " + clazz.getName() + "." + m.getName()
                + " enthält keine passenden Paramter für Attribut " + clazz.getName() + "." + fHit.getName());
            right = false;
          }
        }
      }

//			Class contains no setter methods-> somehow strange
      if (!atLeastOneSetter) {
        sb.append("\n- Klasse " + clazz.getName() + " enthält keine einzige Setter Methode!");
        right = false;
      }
    }
    assertTrue(sb.toString(), right);
  }

//	@org.junit.Test
@SuppressWarnings({"deprecation", "unchecked"})
  public void CheckSetterGetter() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test HA2.1: tCheckSetterGetter Kombination");
    // Check Klasse Date
    Class clazz = Class.forName("suchmaschine.Date");
    Field[] fields = clazz.getDeclaredFields();
    Method[] methods = clazz.getMethods();
    for (Field f : fields) {
      Method setter = null, getter = null;
      for (Method m : methods) {
        if (m.getName().toLowerCase().startsWith("set")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if (m.getReturnType().getName().toLowerCase().equals("void")) {
            Class[] methParamTypes = m.getParameterTypes();
            if ((methParamTypes.length == 1)
                && methParamTypes[0].getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
              setter = m;
            }
          }
        }
        if (m.getName().toLowerCase().startsWith("get")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if ((m.getParameterTypes().length <= 0)
              && m.getReturnType().getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
            getter = m;
          }
        }
        // Class has setter and getter methods
        if ((setter != null) && (getter != null)) {
          Object obj = clazz.newInstance();
          Object param = this.setVal(f.getType().getName().toLowerCase());

          if (param != null) {
            setter.invoke(obj, param);
            if (!getter.invoke(obj).equals(param)) {
              sb.append(
                  "\n- Setter- und Getter für " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
              right = false;
            }
          }
          break;
        }
      }
    }

    // Check class Author
    clazz = Class.forName("suchmaschine.Author");
    fields = clazz.getDeclaredFields();
    methods = clazz.getMethods();
    for (Field f : fields) {
      Method setter = null, getter = null;
      for (Method m : methods) {
        if (m.getName().toLowerCase().startsWith("set")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if (m.getReturnType().getName().toLowerCase().equals("void")) {
            Class[] methParamTypes = m.getParameterTypes();
            if ((methParamTypes.length == 1)
                && methParamTypes[0].getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
              setter = m;
            }
          }
        }
        if (m.getName().toLowerCase().startsWith("get")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if ((m.getParameterTypes().length <= 0)
              && m.getReturnType().getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
            getter = m;
          }
        }
        // Class has setter and getter methods
        if ((setter != null) && (getter != null)) {
          Object obj = clazz.getConstructor(String.class, String.class, Date.class, String.class, String.class)
              .newInstance("", "", null, "", "");
          Object param = this.setVal(f.getType().getName().toLowerCase());

          if (param != null) {
            setter.invoke(obj, param);
            if (!getter.invoke(obj).equals(param)) {
              sb.append(
                  "\n- Setter- und Getter für " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
              right = false;
            }
          }
          break;
        }
      }
    }

    // Check class Document
    clazz = Class.forName("suchmaschine.Document");
    fields = clazz.getDeclaredFields();
    methods = clazz.getMethods();
    for (Field f : fields) {
      Method setter = null, getter = null;
      for (Method m : methods) {
        if (m.getName().toLowerCase().startsWith("set")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if (m.getReturnType().getName().toLowerCase().equals("void")) {
            Class[] methParamTypes = m.getParameterTypes();
            if ((methParamTypes.length == 1)
                && methParamTypes[0].getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
              setter = m;
            }
          }
        }
        if (m.getName().toLowerCase().startsWith("get")
            && m.getName().toLowerCase().contains(f.getName().toLowerCase())) {
          if ((m.getParameterTypes().length <= 0)
              && m.getReturnType().getName().toLowerCase().equals(f.getType().getName().toLowerCase())) {
            getter = m;
          }
        }
        // Class has setter and getter methods
        if ((setter != null) && (getter != null)) {
          Object obj = clazz.getConstructor(String.class, String.class, String.class, Date.class, Author.class)
              .newInstance("", "", "", null, null);
          Object param = this.setVal(f.getType().getName().toLowerCase());

          if (param != null) {
            setter.invoke(obj, param);
            if (!getter.invoke(obj).equals(param)) {
              sb.append(
                  "\n- Setter- und Getter für " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
              right = false;
            }
          }
          break;
        }
      }
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckWordCount() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test HA2.2: tCheckWordCount");
    WordCount wc = null;
    try {
      wc = new WordCount("Wort", 0);
    } catch (NoSuchMethodError e) {
      try {
        wc = new WordCount("Wort", 0);
      } catch (NoSuchMethodError e1) {
        right = false;
        sb.append("\n>>> Falsche Signatur bei dem WordCount Konstruktor");
      }
    }

    int value = wc.incrementCount(), diff = 20;
    int value2 = wc.incrementCount(diff);
    if ((value2 - value) != 20) {
      sb.append("\n>>> Häufigkeit wird falsch gezählt.");
      right = false;
    }
    value2 = wc.incrementCount(-5);
    if ((value2 - value) != 20) {
      sb.append("\n>>> Häufigkeit wird falsch gezählt.");
      right = false;
    }
    assertTrue(sb.toString(), right);
  }
  //@org.junit.Test
  //no Test.java present in this case
  public void PrintOutput() throws Exception {
    Class clazz = Class.forName("suchmaschine.Test");
    StringBuilder sb = new StringBuilder("\n>>> Test HA15: Ausgabe von Klasse " + clazz.getName()
        + ".\nHinweis an Tutoren: Dieser Test schlägt immer fehl! Ausagbe daher auf Spezifikation prüfen.\n**** Begin der Ausgabe. ****\n");

    PrintStream defaultOut = System.out;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(baos);
    System.setOut(out);

    String[] args = {};
    //Test.main(args);

    String output = baos.toString(StandardCharsets.UTF_8);
    sb.append(output);
    sb.append("\n**** Ende der Ausgabe. ****");
    System.setOut(defaultOut);

    org.junit.Assert.fail(sb.toString());
  }
  
  private Object setVal(String typeName) {
    Object param = null;
    switch (typeName) {
    case "int":
    case "char":
    case "byte":
    case "short":
    case "long":
      param = 5;
      break;
    case "boolean":
      param = 1;
      break;
    case "float":
      param = 5f;
      break;
    case "double":
      param = 5d;
      break;
    case "java.lang.string":
      param = "XZUEE";
      break;
    }
    return param;
  }
}
