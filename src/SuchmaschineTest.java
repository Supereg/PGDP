import org.junit.Before;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SuchmaschineTest {
  private Author author;

  private ArrayList<String> reqAttr;

  @Before
  public void init() {
    reqAttr = new ArrayList<String>();
    reqAttr.add("Author");
    reqAttr.add("Date");
    reqAttr.add("Review");
    reqAttr.add("Document");
  }

  @org.junit.Test
  public void NumAttr() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();

    // Test Date
    Class clazz = Date.class;
    sb.append("\n>>> Test 11: 1NumAttr");
    Field[] attr = clazz.getDeclaredFields();
    if (attr.length < 3) {
      sb.append("\n- Klasse " + clazz.getName() + " enthält evtl. nicht genügend Attribute.");
    }

    // Test Author
    clazz = Author.class;
    attr = clazz.getDeclaredFields();
    if (attr.length < 5) {
      sb.append("\n- Klasse " + clazz.getName() + " enthält evtl. nicht genügend Attribute.");
    }

    // Test Document
    clazz = Document.class;
    attr = clazz.getDeclaredFields();
    if (attr.length < 5) {
      sb.append("\n- Klasse " + clazz.getName() + " enthält evtl. nicht genügend Attribute.");
    }

    // Test Document
    clazz = Review.class;
    attr = clazz.getDeclaredFields();
    if (attr.length < 5) {
      sb.append("\n- Klasse " + clazz.getName() + " enthält evtl. nicht genügend Attribute.");
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void AttrVisibility() throws Exception {
    boolean allPrivate = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test : tAttrVisitbility");
    for (int i = 0; i < reqAttr.size(); i++) {
      Class clazz = Class.forName(reqAttr.get(i));
      Field[] attr = clazz.getDeclaredFields();
      for (Field f : attr) {
        if (!Modifier.isPrivate(f.getModifiers())) {
          allPrivate = false;
          sb.append("\n- " + clazz.getName() + "." + f.getName() + " ist nicht private");
        }
      }
    }
    assertTrue(sb.toString(), allPrivate);
  }

  @org.junit.Test
  public void CheckGetter() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test : tCheckGetter");
    for (int i = 0; i < reqAttr.size(); i++) {
      Class clazz = Class.forName(reqAttr.get(i));
      Field[] attr = clazz.getDeclaredFields();
      Method[] meth = clazz.getMethods();
      for (Field f : attr) {
        Method mhit = null;
        for (Method m : meth) {
          // find getter method
          if (m.getName().toLowerCase().startsWith("get")) {
            String methName = m.getName().toLowerCase().substring(3, m.getName().length());
            if (methName.equals(f.getName().toLowerCase())) {
              mhit = m;
              break;
            }
          }
        }
        if (mhit != null) {
          if ((!mhit.getReturnType().getName().toLowerCase().equals(f.getType().getName().toLowerCase()))
              || (mhit.getParameterTypes().length > 0)) {
            right = false;
            sb.append("\n- Falsche Signatur für Getter-Methode " + clazz.getName() + "." + mhit.getName());
          }
        } else {
          right = false;
          sb.append("\n- Keine Getter-Methode für Attribut " + clazz.getName() + "." + f.getName());
        }
      }
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckAuthorConst() throws Exception { // Check Author constructor
    Class clazz = Author.class;
    Constructor[] cs = clazz.getConstructors();
    StringBuilder sb = new StringBuilder("\n>>> Test : tCheckAuthorConst\nClass: " + clazz.getName());
    boolean right = true;
    if (cs.length != 1) {
      right = false;
      sb.append("\n- Anzahl Konstruktoren stimmt nicht.");
    } else {
      Constructor c = cs[0];
      Class[] p = c.getParameterTypes();
      if (!Modifier.isPublic(c.getModifiers())) {
        right = false;
        sb.append("\n- Konstruktor ist nicht public. ");
      } else if (p.length != 5) {
        right = false;
        sb.append("\n- Anzahl Konstruktor-Parameter stimmt nicht. ");
      } else if (!p[0].getName().toLowerCase().equals("java.lang.string")
          || !p[1].getName().toLowerCase().equals("java.lang.string")
          || !p[3].getName().toLowerCase().equals("java.lang.string")
          || !p[0].getName().toLowerCase().equals("java.lang.string")) {
        right = false;
        sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
      } else {
        try {
          Class.forName(p[2].getName());
        } catch (Exception e) {
          right = false;
          sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
        }
      }
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckDocumentConst() throws Exception {// Check Document construcotr
    Class clazz = Document.class;
    Constructor[] cs = clazz.getConstructors();
    StringBuilder sb = new StringBuilder("\n>>> Test : tCheckDocumentConst\nClass: " + clazz.getName());
    boolean right = true;
    if (cs.length != 1) {
      right = false;
      sb.append("\n- Anzahl Konstruktoren stimmt nicht.");
    } else {
      Constructor c = cs[0];
      Class[] p = c.getParameterTypes();

      if (!Modifier.isPublic(c.getModifiers())) {
        right = false;
        sb.append("\n- Konstruktor ist nicht public. ");
      } else if (p.length != 6) {
        right = false;
        sb.append("\n- Anzahl Konstruktor-Parameter stimmt nicht. ");
      } else if (!p[0].getName().toLowerCase().equals("java.lang.string")
          || !p[1].getName().toLowerCase().equals("java.lang.string")
          || !p[2].getName().toLowerCase().equals("java.lang.string")
          || !p[5].getName().toLowerCase().equals("java.lang.string")) {
        right = false;
        sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
      } else {
        try {
          Class.forName(p[3].getName());
          Class.forName(p[4].getName());
        } catch (Exception e) {
          right = false;
          sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
        }
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckReviewConst() throws Exception {// Check Review constructor
    Class clazz = Review.class;
    Constructor[] cs = clazz.getConstructors();
    StringBuilder sb = new StringBuilder("\n>>> Test : tCheckDocumentConst\nClass: " + clazz.getName());
    boolean right = true;
    if (cs.length != 1) {
      right = false;
      sb.append("\n- Anzahl Konstruktoren stimmt nicht.");
    } else {
      Constructor c = cs[0];
      Class[] p = c.getParameterTypes();

      if (!Modifier.isPublic(c.getModifiers())) {
        right = false;
        sb.append("\n- Konstruktor ist nicht public. ");
      } else if (p.length != 6) {
        right = false;
        sb.append("\n- Anzahl Konstruktor-Parameter stimmt nicht. ");
      } else if (!p[2].getName().toLowerCase().equals("java.lang.string")
          || !p[4].getName().toLowerCase().equals("int")
          || !p[5].getName().toLowerCase().equals("java.lang.string")) {
        right = false;
        sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
      } else {
        try {
          Class.forName(p[0].getName());
          Class.forName(p[1].getName());
          Class.forName(p[3].getName());
        } catch (Exception e) {
          right = false;
          sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
        }
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckDateConst() throws Exception {// Check Date constructor
    Class clazz = Date.class;
    Constructor[] cs = clazz.getConstructors();
    StringBuilder sb = new StringBuilder("\n>>> Test : tCheckDocumentConst\nClass: " + clazz.getName());
    boolean right = true;
    boolean paramConstr = false;
    boolean nonParamConstr = false;

    if (cs.length != 2) {
      right = false;
      sb.append("\n- Anzahl Konstruktoren stimmt nicht.");
    } else {
      for (Constructor c : cs) {
        Class[] p = c.getParameterTypes();
        // parametrized constructor

        if (!Modifier.isPublic(c.getModifiers())) {
          right = false;
          sb.append("\n- Konstruktor ist nicht public. ");
        } else if (p.length == 3) {
          paramConstr = true;
          if (!p[0].getName().toLowerCase().equals("int") || !p[1].getName().toLowerCase().equals("int")
              || !p[2].getName().toLowerCase().equals("int")) {
            right = false;
            sb.append("\n- Konstruktor-Parametrisierung ist nicht korrekt. ");
          }
          Date d = new Date(Terminal.TODAYS_DAY, Terminal.TODAYS_MONTH, Terminal.TODAYS_YEAR);
          assertEquals("\n- Datum nicht korrekt!", Terminal.TODAYS_DAY, d.getDay());
          assertEquals("\n- Monat nicht korrekt!", Terminal.TODAYS_MONTH, d.getMonth());
          assertEquals("\n- Jahr nicht korrekt!", Terminal.TODAYS_YEAR, d.getYear());
        } else if (p.length == 0) {
          nonParamConstr = true;
          Date d = (Date) Class.forName("Date").newInstance();
          assertEquals("\n- Datum nicht korrekt!", Terminal.TODAYS_DAY, d.getDay());
          assertEquals("\n- Monat nicht korrekt!", Terminal.TODAYS_MONTH, d.getMonth());
          assertEquals("\n- Jahr nicht korrekt!", Terminal.TODAYS_YEAR, d.getYear());
        } else {
          right = false;
          sb.append("\n- Konstruktor nicht spezifiziert mit " + p.length + " Parametern.");
        }
      }
      if (paramConstr == false) {
        right = false;
        sb.append("\n- Ein spezifizierter Kontruktor nicht vorhanden.");
      }
      if (nonParamConstr == false) {
        right = false;
        sb.append("\n- Ein spezifizierter Kontruktor nicht vorhanden.");
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void ToString() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tToString");
    for (int i = 0; i < reqAttr.size(); i++) {
      Class clazz = Class.forName(reqAttr.get(i));
      Method[] meth = clazz.getDeclaredMethods();
      Method mhit = null;
      for (Method m : meth) {
        if (m.getName().toLowerCase().equals("tostring")) {
          mhit = m;
          break;
        }
      }

      if (mhit != null) {
        Class retType = mhit.getReturnType();
        Class[] paramType = mhit.getParameterTypes();
        if (!retType.getName().toLowerCase().equals("java.lang.string")) {
          right = false;

          sb.append("\n- Rückgabewert der " + clazz.getName() + ".toString-Methode stimmt nicht!");
        } else if (paramType.length != 0) {
          right = false;
          sb.append("\n- Parametrisierung der " + clazz.getName() + ".toString-Methode stimmt nicht!");
        }
      } else {
        right = false;
        sb.append("\n- " + clazz.getName() + ".toString-Methode nicht vorhanden!");
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void ToStringContent() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : toString Inhalt von Document,Author,Review,Date");
    HashMap<String, Integer> datumData = new HashMap<String, Integer>();
    datumData.put("day", 25);
    datumData.put("month", 12);
    datumData.put("year", 1998);

    HashMap<String, String> authorData = new HashMap<String, String>();
    authorData.put("author.firstname", "Harry");
    authorData.put("author.lastname", "Potter");
    authorData.put("author.address", "Ligusterweg Nr. 4");
    authorData.put("author.email", "harry.potter@hogwarts.com");
    authorData.put("author.day", "13");
    authorData.put("author.month", "5");
    authorData.put("author.year", "2011");

    HashMap<String, String> docData = new HashMap<String, String>();
    docData.putAll(authorData);
    docData.put("doc.titel", "Mobby Dick");
    docData.put("doc.lang", "deutsch");
    docData.put("doc.desc", "Beschreibung");
    docData.put("doc.day", "12");
    docData.put("doc.month", "2");
    docData.put("doc.year", "1983");
    docData.put("doc.cont", "This is the document content.");

    HashMap<String, String> reviewData = new HashMap<String, String>();
    reviewData.putAll(docData);
    reviewData.put("review.rating", "8");
    reviewData.put("review.author.firstname", "Matthias");
    reviewData.put("review.author.lastname", "Schmidt");
    reviewData.put("review.author.address", "Alte Heide 61");
    reviewData.put("review.author.email", "Matthias.Schmidt@hogwarts.com");
    reviewData.put("review.author.day", "25");
    reviewData.put("review.author.month", "6");
    reviewData.put("review.author.year", "1970");
    reviewData.put("review.lang", "Schweiz");
    reviewData.put("review.day", "29");
    reviewData.put("review.month", "4");
    reviewData.put("review.year", "2013");
    reviewData.put("review.cont", "This is the review content.");

    // Test Date
    Date date = new Date(datumData.get("day"), datumData.get("month"), datumData.get("year"));
    String dStr = date.toString();
    if (!dStr.contains(String.valueOf(datumData.get("day")))) {
      sb.append("\n- Date.toString() enthält nicht Tag.");
      right = false;
    }
    if (!dStr.contains(String.valueOf(datumData.get("month")))) {
      sb.append("\n- Date.toString() enthält nicht Monat.");
      right = false;
    }
    if (!dStr.contains(String.valueOf(datumData.get("year")))) {
      sb.append("\n- Date.toString() enthält nicht Jahr.");
      right = false;
    }

    // Test Author
    Author author = new Author(authorData.get("author.firstname"), authorData.get("author.lastname"),
        new Date(Integer.parseInt(authorData.get("author.day")), Integer.parseInt(authorData.get("author.month")),
            Integer.parseInt(authorData.get("author.year"))),
        authorData.get("author.address"), authorData.get("author.email"));
    String authorStr = author.toString();

    if (!authorStr.toLowerCase().contains(authorData.get("author.firstname").toLowerCase())) {
      sb.append("\n- Author.toString() enthält nicht Vornamen.");
      right = false;
    }
    if (!authorStr.toLowerCase().contains(authorData.get("author.lastname").toLowerCase())) {
      sb.append("\n- Author.toString() enthält nicht Nachnamen.");
      right = false;
    }

    boolean containsAll = true;
    if (right != false) {
      for (String s : new ArrayList<String>(authorData.values())) {
        if (!authorStr.toLowerCase().contains(s.toLowerCase())) {
          containsAll = false;
          break;
        }
      }
      if (containsAll == true) {
        sb.append("\n- Author.toString() enthält alle Attribute.");
        right = false;

      }
    }

    // Test Document
    Date docDate = new Date(Integer.parseInt(docData.get("doc.day")), Integer.parseInt(docData.get("doc.month")),
        Integer.parseInt(docData.get("doc.year")));
    Document doc = new Document(docData.get("doc.titel"), docData.get("doc.lang"), docData.get("doc.desc"), docDate,
        author, docData.get("doc.cont"));
    String docStr = doc.toString();
    if (!docStr.toLowerCase().contains(docData.get("doc.titel").toLowerCase())) {
      sb.append("\n- Document.toString() enthält nicht Title.");
      right = false;
    }

    if (right != false) {
      containsAll = true;
      if (!docStr.toLowerCase().contains(docData.get("author.firstname").toLowerCase())
          || !docStr.toLowerCase().contains(docData.get("author.lastname").toLowerCase())) {
        containsAll = false;
      }
      if (!docStr.toLowerCase().contains(docDate.toString())) {
        containsAll = false;
      }
      if (!docStr.toLowerCase().contains(docData.get("doc.lang").toLowerCase())) {
        containsAll = false;
      }
      if (!docStr.toLowerCase().contains(docData.get("doc.desc").toLowerCase())) {
        containsAll = false;
      }
      if (containsAll == true) {
        sb.append("\n- Document.toString() enthält alle Attribute.");
        right = false;
      }

    }

    // Test Review
    Date revDate = new Date(Integer.parseInt(reviewData.get("review.day")),
        Integer.parseInt(reviewData.get("review.month")), Integer.parseInt(reviewData.get("review.year")));
    Author reviewAuthor = new Author(reviewData.get("review.author.firstname"),
        reviewData.get("review.author.lastname"),
        new Date(Integer.parseInt(reviewData.get("review.author.day")),
            Integer.parseInt(reviewData.get("review.author.month")),
            Integer.parseInt(reviewData.get("review.author.year"))),
        reviewData.get("review.author.address"), reviewData.get("review.author.email"));
    Review review = new Review(reviewAuthor, doc, reviewData.get("review.lang"), revDate,
        Integer.parseInt(reviewData.get("review.rating")), reviewData.get("review.cont"));
    String revStr = review.toString();
    if (!revStr.toLowerCase().contains(reviewData.get("doc.titel").toLowerCase())) {
      sb.append("\n- Review.toString() enthält nicht Document.");
      right = false;
    }
    if (!revStr.toLowerCase().contains(reviewData.get("review.rating"))) {
      sb.append("\n- Review.toString() enthält nicht Bewertung.");
      right = false;
    }

    if (right != false) {
//			for(String s : new ArrayList<String>(reviewData.values())){
//				if(!revStr.toLowerCase().contains(s.toLowerCase())){
//					containsAll = false;
//					break;
//				}
//			}		
      containsAll = true;
      if (!revStr.toLowerCase().contains(reviewData.get("review.author.firstname").toLowerCase())
          || !revStr.toLowerCase().contains(reviewData.get("review.author.lastname").toLowerCase())) {
        containsAll = false;
      }
      if (!revStr.toLowerCase().contains(reviewData.get("review.lang").toLowerCase())) {
        containsAll = false;
      }
      if (!revStr.toLowerCase().contains(revDate.toString().toLowerCase())) {
        containsAll = false;
      }

      if (containsAll == true) {
        sb.append("\n- Review.toString() enthält alle Attribute.");
        right = false;
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void ContactInfo() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tContactInfo");
    String firstname = "Firstname", lastname = "Lastname", residence = "Residence", email = "Test@org.junit.Test.mail";
    Date d = new Date(27, 11, 1999);

    try {
      Method m = Author.class.getDeclaredMethod("getContactInformation");
    } catch (Exception e) {
      sb.append("\n- Method getContactInformation nicht gefunden!");
      right = false;
    }
    assertTrue(sb.toString(), right);

    Author a = new Author(firstname, lastname, d, residence, email);
    String contact = a.getContactInformation();
    if ((contact == null) || contact.equals("")) {
      right = false;
      sb.append("\n- Kontaktinformation war leer!");
    } else {
      String[] s = contact.split(Terminal.NEWLINE);
      sb.append("\n- Author.getContactInformation() liefert \n" + contact);
      if (s.length != 3) {
        right = false;
        sb.append("\n- Kontaktinformationen enthalten nicht drei Zeilen aufgeteilt!");
      } else {
        if (!s[0].toLowerCase().contains(firstname.toLowerCase())
            || !s[0].toLowerCase().contains(lastname.toLowerCase())) {
          right = false;
          sb.append("\n- Kontaktinformationen 1. Zeile falsche Informationen!");
        }
        if (!s[1].toLowerCase().contains(email.toLowerCase())) {
          right = false;
          sb.append("\n- Kontaktinformationen 2. Zeile falsche Informationen!");
        }
        if (!s[2].toLowerCase().contains(residence.toLowerCase())) {
          right = false;
          sb.append("\n- Kontaktinformationen 2. Zeile falsche Informationen!");
        }
      }
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void DaysSince1970() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tDaysSince1970\nClass: " + Date.class.getName());

    Method m = null;
    try {
      m = Date.class.getDeclaredMethod("daysSince1970");
      if (!Modifier.isPrivate(m.getModifiers())) {
        right = false;
        sb.append("\n- Methode daysSince1970 nicht private!");
      } else if (!m.getReturnType().equals("int")) {
        sb.append("\n- Methode daysSince1970 return paramter passt nicht!");
      }
    } catch (Exception e) {
      right = false;
      sb.append("\n- Methode daysSince1970 nicht gefunden!");
    }

    if (m != null) {
      m.setAccessible(true);
      Date d = new Date();
      int ret = (int) m.invoke(d);

      int refValue = (Terminal.TODAYS_YEAR - 1970) * 30 * 12;
      refValue += (Terminal.TODAYS_MONTH - 1) * 30;
      refValue += Terminal.TODAYS_DAY - 1;

      assertEquals("\n- Methode daysSince1970 liefert falschen Wert!", refValue, ret, 5);
    }
    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void GetAgeInDays() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tGetAgeInDays\nClass: " + Date.class.getName());
    Method m = null;
    try {
      m = Date.class.getDeclaredMethod("getAgeInDaysAt", Date.class);
      if (!Modifier.isPublic(m.getModifiers())) {
        right = false;
        sb.append("\n- Sichbarkeit der Methode getAgetInDays falsch!");
      } else if (!m.getReturnType().equals("int")) {
        sb.append("\n- Methode getAgetInDays return paramter passt nicht!");
      }
    } catch (Exception e) {
      right = false;
      sb.append("\n- Methode getAgetInDays nicht gefunden!");
    }

    if (m != null) {
      int refValue = 360;
      Date d = new Date(1, 1, 2012), refdate = new Date(1, 1, 2013);
      int ret = d.getAgeInDaysAt(refdate);
      assertEquals("\n- Methode getAgeInDaysAt liefert falschen Wert!", refValue, ret, 0);
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void GetAgeInYears() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tGetAgeInYears\nClass: " + Date.class.getName());
    Method m = null;
    try {
      m = Date.class.getDeclaredMethod("getAgeInYearsAt", Date.class);
      if (!Modifier.isPublic(m.getModifiers())) {
        right = false;
        sb.append("\n- Sichbarkeit der Methode getAgeInYears falsch!");
      } else if (!m.getReturnType().equals("int")) {
        sb.append("\n- Methode getAgeInYears return paramter passt nicht!");
      }
    } catch (Exception e) {
      right = false;
      sb.append("\n- Methode getAgeInYears nicht gefunden!");
    }

    if (m != null) {
      int refValue = 3;
      Date d = new Date(1, 1, 2010), refdate = new Date(1, 1, 2013);
      int ret = d.getAgeInYearsAt(refdate);
      assertEquals("\n- Methode getAgeInYears liefert falschen Wert!", refValue, ret, 0);
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void GetAge() throws Exception {
    ArrayList<String> reqAttr = new ArrayList<String>();
    reqAttr.add("Author");
    reqAttr.add("Document");
    reqAttr.add("Review");
    boolean right = true;
    StringBuilder sb = new StringBuilder("\n>>> Test : tGetAge");
    Author auth = null;
    Review review = null;
    Document doc = null;
    for (int i = 0; i < reqAttr.size(); i++) {
      Method m = null;
      Class clazz = Class.forName(reqAttr.get(i));
      sb.append("\nClass: " + clazz.getName());
      try {
        m = clazz.getDeclaredMethod("getAgeAt", Date.class);
        if (!Modifier.isPublic(m.getModifiers())) {
          right = false;
          sb.append("\n- Sichbarkeit der Methode getAge falsch!");
        } else if (!m.getReturnType().equals("int")) {
          sb.append("\n- Methode getAge return paramter passt nicht!");
        }
      } catch (Exception e) {
        right = false;
        sb.append("\n- Methode getAge nicht gefunden!");
      }

      if (m != null) {
        Date d = new Date(1, 1, 2000), refDate = new Date(1, 1, 2013), d1 = new Date(1, 1, 1970),
            d2 = new Date(1, 12, 2007);
        int rv = 13, rv1 = d1.getAgeInDaysAt(refDate), rv2 = d2.getAgeInDaysAt(refDate), diff;

        if (clazz.getName().toLowerCase().equals("author")) {
          auth = new Author("Firstname", "Lastname", d, "Residence", "Tester@org.junit.Test.de");
          diff = auth.getAgeAt(refDate);
          assertEquals("\n- Methode " + clazz.getName() + ".getAge liefert falschen Wert!", rv, diff, 0);
        } else if (clazz.getName().toLowerCase().equals("document")) {
          doc = new Document("DocTitle", "DocLang", "DocDesc", d1, auth, "This is the document content.");
          diff = doc.getAgeAt(refDate);
          assertEquals("\n- Methode " + clazz.getName() + ".getAge liefert falschen Wert!", rv1, diff, 0);
        } else if (clazz.getName().toLowerCase().equals("review")) {
          review = new Review(auth, doc, "ReviewLang", d2, 5, "This is the review content.");
          diff = review.getAgeAt(refDate);
          assertEquals("\n- Methode " + clazz.getName() + ".getAge liefert falschen Wert!", rv2, diff, 0);
        }
      }
    }

    assertTrue(sb.toString(), right);
  }
}
