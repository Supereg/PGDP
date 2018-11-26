
import org.junit.Assert;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSuchmaschine5 {
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
    public void tHA21CheckGetter() throws Exception {
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
//					sb.append("\n- Kein Attribut fuer "+clazz.getName()+"."+m.getName()+" gefunden.");
//					right = false;
//				} else {
                    Class[] methParamTypes = m.getParameterTypes();
                    Class returnType = m.getReturnType();
//					getter-method has wrong signature
                    if ((methParamTypes.length > 0)
                            || !returnType.getName().toLowerCase().equals(fHit.getType().getName().toLowerCase())) {
                        right = false;
                        sb.append("\n- Falsche Signatur fuer Getter-Methode " + clazz.getName() + "." + m.getName());
                        continue;
                    }
                }
            }

//			Class contains no getter methods-> somehow strange
            if (!atLeastOneSetter) {
                sb.append("\n- Klasse " + clazz.getName() + " enthaelt keine einzige Getter Methode!");
                right = false;
            }
        }
        assertTrue(sb.toString(), right);
    }

    @org.junit.Test
    public void tHA21CheckSetter() throws Exception {
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
//					sb.append("\n- Kein Attribut fuer "+clazz.getName()+"."+m.getName()+" gefunden.");
//					right = false;
//				}else{
                    Class[] methParamTypes = m.getParameterTypes();
                    Class returnType = m.getReturnType();
                    Class cHit = null;
//					setter-method has wrong signature
                    if ((methParamTypes.length == 0) || !returnType.getName().toLowerCase().equals("void")) {
                        right = false;
                        sb.append("\n- Falsche Signatur fuer Setter-Methode " + clazz.getName() + "." + m.getName());
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
                                + " enthaelt keine passenden Paramter fuer Attribut " + clazz.getName() + "." + fHit.getName());
                        right = false;
                    }
                }
            }

//			Class contains no setter methods-> somehow strange
            if (!atLeastOneSetter) {
                sb.append("\n- Klasse " + clazz.getName() + " enthaelt keine einzige Setter Methode!");
                right = false;
            }
        }
        assertTrue(sb.toString(), right);
    }

    //	@org.junit.Test
    public void tHA21CheckSetterGetter() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\n>>> Test HA2.1: tCheckSetterGetter Kombination");
        // Check Klasse Date
        Class clazz = Class.forName("Date");
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
                                    "\n- Setter- und Getter fuer " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
                            right = false;
                        }
                    }
                    break;
                }
            }
        }

        // Check class Author
        clazz = Class.forName("Author");
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
                                    "\n- Setter- und Getter fuer " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
                            right = false;
                        }
                    }
                    break;
                }
            }
        }

        // Check class Document
        clazz = Class.forName("Document");
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
                                    "\n- Setter- und Getter fuer " + clazz.getName() + "." + f.getName() + " liefern falsche Werte!");
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
    public void tHA22CheckWordCount() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\n>>> Test HA2.2: tCheckWordCount");
        WordCount wc = null;
        try {
            wc = new WordCount("Wort");
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
            sb.append("\n>>> Haeufigkeit wird falsch gezaehlt.");
            right = false;
        }
        value2 = wc.incrementCount(-5);
        if ((value2 - value) != 20) {
            sb.append("\n>>> Haeufigkeit wird falsch gezaehlt.");
            right = false;
        }
        assertTrue(sb.toString(), right);
    }

    @org.junit.Test
    public void tHA23CheckWordCountsArray() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
        // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
        sb.append("\n>>> Test HA2.3: tCheckWordCountsArray");
        WordCountsArray wca = new WordCountsArray(3);
        wca.add("hello", 10);
        wca.add("world", 5);
        wca.add("test", 3);
        if (wca.size() != 3) {
            sb.append("\n>>> WordCountsArray.size() liefert falschen wert. SOLL 3, IST " + wca.size());
            right = false;
        }
        if (!wca.getWord(1).toLowerCase().equals("world")) {
            sb.append(
                    "\n>>> WordCountsArray.getWord(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Word falsch ab.");
            right = false;
        }
        if (wca.getCount(1) != 5) {
            sb.append(
                    "\n>>> WordCountsArray.getCount(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Haeufigkeit falsch ab.");
            right = false;
        }

        wca.setCount(1, 10);
        if (wca.getCount(1) != 10) {
            sb.append("\n>>> WordCountsArray.setCount(index,count) setzt Haeufigkeit flasch.");
            right = false;
        }

        assertTrue(sb.toString(), right);
    }

    @org.junit.Test
    public void tHA24CheckDocument() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
        // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
        sb.append("\n>>> Test HA2.4: tCheckDocument");
        for (String s : mySUFFICES) {
            boolean contains = false;
            for (String s1 : Document.SUFFICES) {
                if (s.toLowerCase().equals(s1.toLowerCase())) {
                    contains = true;
                    break;
                }
            }
            if (contains == false) {
                sb.append("\n>>> Document.SUFFICE enthaelt nicht alle Elemente.");
                right = false;
                break;
            }
        }
        String content = "Harry versuchte uebrigens nicht zum ersten Mal die Sache zu erklaeren";
        Document doc = new Document("Harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998),
                new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), content);
        WordCountsArray wca = doc.getWordCounts();
        if (wca.size() != 11) {
            sb.append("\n>>> Document.getWordCounts liefert falsche anzahl an Woertern.");
            right = false;
        }

        Method[] methods = Document.class.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().toLowerCase().equals("tokenize")) {
                m.setAccessible(true);
                String[] str = (String[]) m.invoke(doc, "Er wechselte finstere Blicke mit seiner Gattin Petunia.");
                if (str.length != 8) {
                    sb.append("\n>>> Document.tokenize zerlegt Saetze falsch.");
                    right = false;
                }
            } else if (m.getName().toLowerCase().equals("sufficesequal")) {
                m.setAccessible(true);
                boolean ret = (boolean) m.invoke(doc, "gehen", "gehen", 3);
                boolean ret2 = (boolean) m.invoke(doc, "schlafen", "gehen", 3);
                if (!ret || ret2) {
                    sb.append("\n>>> Document.sufficesequal berechnet Suffix falsch.");
                    right = false;
                }
            } else if (m.getName().toLowerCase().equals("findsuffix")) {
                m.setAccessible(true);
                String str = (String) m.invoke(doc, "fakfaksdj");
                if (!str.toLowerCase().equals("")) {
                    sb.append("\n>>> Document.findsuffix findet falschen Suffix.");
                    right = false;
                }
                str = (String) m.invoke(doc, "nachhaltig");
                if (!str.toLowerCase().equals("haltig")) {
                    sb.append("\n>>> Document.findsuffix findet falschen Suffix.");
                    right = false;
                }
            } else if (m.getName().toLowerCase().equals("cutsuffix")) {
                m.setAccessible(true);
                String str = (String) m.invoke(doc, "freundschaft", "schaft");
                if (!str.toLowerCase().equals("freund")) {
                    sb.append("\n>>> Document.cutsuffix findet falschen Suffix.");
                    right = false;
                }
                str = (String) m.invoke(doc, "freundschaft", "los");
                if (!str.toLowerCase().equals("freundschaft")) {
                    sb.append("\n>>> Document.cutsuffix findet falschen Suffix.");
                    right = false;
                }
            } else if (m.getName().toLowerCase().equals("addtext")) {
                m.setAccessible(true);
                String str = (String) m.invoke(doc, "hello world");
                wca = doc.getWordCounts();
                if (wca.size() != 2) {
                    sb.append("\n>>> Document.addtext nicht nach Spezifikation.");
                    right = false;
                }
            }
        }

        assertTrue(sb.toString(), right);
    }

    //@org.junit.Test
    public void tHA25PrintOutput() throws Exception {
        Class clazz = Class.forName("Test");
        StringBuilder sb = new StringBuilder("\n>>> Test HA15: Ausgabe von Klasse " + clazz.getName()
                + ".\nHinweis an Tutoren: Dieser Test schlaegt immer fehl! Ausagbe daher auf Spezifikation pruefen.\n**** Begin der Ausgabe. ****\n");

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

        Assert.fail(sb.toString());
    }

    @org.junit.Test
    public void tHA26CheckDatum() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
        // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
        sb.append("\n>>> Zusatzaufgabe!! Test HA2.6: tHA26CheckDatum");
        Date datum = new Date(1, 1, 1970);
        Date today = new Date(8, 11, 2013);
        int tage = datum.getAgeInDaysAt(today);

        sb.append("\n>>> Date.getAgeInDaysAt berechnet die Tage falsch.");
        assertEquals(sb.toString(), 16017, tage);
    }

    @org.junit.Test
    public void tHA27CheckWordCountsArray() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
        // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
        sb.append("\n>>> Zusatzaufgabe!! Test HA2.7: tCheckWordCountsArrayZusatz");
        WordCountsArray wca = new WordCountsArray(3);
        wca.add("hello", 10);
        wca.add("world", 5);
        wca.add("test", 3);
        if (wca.size() != 3) {
            sb.append("\n>>> WordCountsArray.size() liefert falschen wert. SOLL 3, IST " + wca.size());
            right = false;
        }
        wca.add("my", 8);
        wca.add("kdkdk", 0);
        if (wca.size() != 5) {
            sb.append("\n>>> WordCountsArray.size() liefert falschen wert. SOLL 5, IST " + wca.size());
            right = false;
        }
        if (wca.getCount(3) != 8) {
            sb.append(
                    "\n>>> WordCountsArray.getCount(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Haeufigkeit falsch ab.");
            right = false;
        }

        wca.setCount(4, 1);
        if (wca.getCount(4) != 1) {
            sb.append("\n>>> WordCountsArray.setCount(index,count) setzt Haeufigkeit flasch.");
            right = false;
        }

        assertTrue(sb.toString(), right);
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

    @org.junit.Test
    public void tHA31equals() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\n>>> Test HA3.1: tequals");
        // Teste Date.equals
        Date d1 = new Date();
        Date d2 = new Date();
        boolean value = d1.equals(d2);
        if (!value) {
            sb.append("\n>>> equals(Date date) is Falsch implementiert.");
            right = false;
        }
        Date d3 = new Date(1, 1, 2000);
        boolean value2 = d1.equals(d3);
        if (value2) {
            sb.append("\n>>> equals(Date date) is Falsch implementiert.");
            right = false;
        }
        // Teste Author.equals
        Author a1 = new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com");
        Author a2 = new Author("adasddasdas", "qweqweqwe", new Date(1, 10, 1980), "dasdasd", "asdsadas");
        Author a3 = new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com");
        value2 = a1.equals(a2);
        if (value2) {
            sb.append("\n>>> equals(Author author)0 is Falsch implementiert.");
            right = false;
        }
        value2 = a1.equals(a3);
        if (!value2) {
            sb.append("\n>>> equals(Author author)1 is Falsch implementiert.");
            right = false;
        }

        // Teste WordCount.equals

        WordCount wc = null;
        WordCount wc2 = null;
        try {
            wc = new WordCount("Wort");
            wc2 = new WordCount("Wort");
        } catch (NoSuchMethodError e) {
            try {
                wc = new WordCount("Wort", 0);
                wc2 = new WordCount("Wort", 0);
            } catch (NoSuchMethodError e1) {
                right = false;
                sb.append("\n>>> Falsche Signatur bei dem WordCount Konstruktor");
            }
        }
        value = wc.equals(wc2);
        if (!value) {
            sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
            right = false;
        }
        wc.incrementCount(2);
        value = wc.equals(wc2);
        if (value) {
            sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
            right = false;
        }

        // Teste WordCountsArray.equals

        WordCountsArray wca = new WordCountsArray(3);
        wca.add("hello", 10);
        wca.add("test", 3);
        WordCountsArray wca2 = new WordCountsArray(3);
        wca2.add("hello", 10);
        wca2.add("test", 3);
        value = wca.equals(wca2);
        if (!value) {
            sb.append("\n>>> equals(WordCountsArray wca) is Falsch implementiert.");
            right = false;
        }
        wca.add("tes2", 0);
        value = wca.equals(wca2);
        if (value) {
            sb.append("\n>>> equals(WordCountsArray wca) is Falsch implementiert.");
            right = false;
        }

        // Teste Document.equals
        String text = "harry versuchte uebrigens nicht zum ersten mal die sache zu erklaeren";
        String text2 = "harry versuchte uebrigenss nichtt zumm erstenn mal2 diee sachee zuu erklaerenn";
        Document doc1 = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998), a1, text);
        Document doc2 = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998), a1, text);
        Document doc3 = new Document("harry Potter 3", "Englisch", "blae", new Date(5, 6, 2001), a2, text2);

        value = doc1.equals(doc2);
        if (!value) {
            sb.append("\n>>> equals(Document document) is Falsch implementiert.");
            right = false;
        }
        value2 = doc1.equals(doc3);
        if (value2) {
            sb.append("\n>>> equals(Document document) is Falsch implementiert.");
            right = false;
        }
        // Teste Review.equals
        String content = "das ist ein mieses review";
        Review rev1 = new Review(a1, doc1, text, d1, 1, content);
        Review rev2 = new Review(a1, doc1, text, d1, 1, content);
        Review rev3 = new Review(a2, doc2, text2, d2, 3, content);

        value = rev1.equals(rev2);
        if (!value) {
            sb.append("\n>>> equals(Review review) is Falsch implementiert.");
            right = false;
        }
        value = rev1.equals(rev3);
        if (value) {
            sb.append("\n>>> equals(Review review) is Falsch implementiert.");
            right = false;
        }
        assertTrue(sb.toString(), right);
    }

    @org.junit.Test
    public void tHA32WordCountsArray() throws Exception {
        boolean right = true;
        StringBuilder sb = new StringBuilder();
        sb.append("\n>>> Test HA3.2: Erweiterung der Klasse WordCountsArray");
        WordCountsArray wca = new WordCountsArray(3);
        wca.add("hello", 10);
        wca.add("test", 3);
        // Teste getWord(index)
        if (!wca.getWord(0).equals("hello")) {
            sb.append(
                    "\n>>> WordCountsArray.getWord(index) liefert falschen wert oder add(word,count) ist falsch implementiert");
            right = false;
        }
        if (wca.getWord(-1) != null) {
            sb.append("\n>>> WordCountsArray.getWord(index) liefert falschen wert. SOLL null sein");
            right = false;
        }
        if (wca.getWord(-10) != null) {
            sb.append("\n>>> WordCountsArray.getWord(index) liefert falschen wert. SOLL null sein");
            right = false;
        }
        // Teste getIndexOfWord(word)
        if (wca.getIndexOfWord("test") != 1) {
            sb.append("\n>>> WordCountsArray.getIndexOfWord(word) liefert falschen wert. SOLL 1 sein ist " + wca.getIndexOfWord("test"));
            right = false;
        }
        if (wca.getIndexOfWord("bla") != -1) {
            sb.append("\n>>> WordCountsArray.getIndexOfWord(word) liefert falschen wert. SOLL -1 sein ist " + wca.getIndexOfWord("test"));
            right = false;
        }

        // Teste getCount(index) und add(word,count)
        wca.add("hello", 10);
        wca.add("hello", -1);
        wca.add("", 10);
        wca.add(null, 10);
        if (wca.getCount(0) != 20 || wca.size() != 2) {
            sb.append(
                    "\n>>> WordCountsArray.getCount(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Haeufigkeit falsch ab.");
            right = false;
        }

        // Teste wordsEqual

        WordCountsArray wca2 = new WordCountsArray(3);
        wca2.add("hello", 10);
        wca2.add("test", 3);
        WordCountsArray wca3 = new WordCountsArray(3);
        wca3.add("test", 3);
        wca3.add("hello", 10);

        Method[] methods = WordCountsArray.class.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().toLowerCase().equals("wordsequal")) {
                m.setAccessible(true);
                boolean value = (boolean) m.invoke(wca, wca2);
                if (!value) {
                    sb.append("\n>>> WordCountsArray.wordsEqual ist falsch implementiert.");
                    right = false;
                }
                value = (boolean) m.invoke(wca, wca3);
                if (value) {
                    sb.append("\n>>> WordCountsArray.wordsEqual ist falsch implementiert.");
                    right = false;
                }
            }
        }

        // Teste scalarProduct

        for (Method m : methods) {
            if (m.getName().toLowerCase().equals("scalarproduct")) {
                m.setAccessible(true);
                double value = (double) m.invoke(wca, wca2);
                if (value != 209) {
                    sb.append("\n>>> WordCountsArray.scalarProduct ist falsch implementiert. SOLL 209 ist " + value);
                    right = false;
                }
                value = (double) m.invoke(wca, wca3);
                if (value != 0) {
                    sb.append("\n>>> WordCountsArray.scalarProduct ist falsch implementiert. SOLL 0 ist " + value);
                    right = false;
                }
            }
        }

        // Teste sort

        WordCountsArray wca5 = new WordCountsArray(5);
        wca5.add("e", 5);
        wca5.add("d", 4);
        wca5.add("c", 3);
        wca5.add("b", 2);
        wca5.add("a", 1);
        wca5.sort();
        // Ein bisschen unelegant, aber wollte vermeiden wca.equals falls das falsch
        // implementiert ist
        if (wca5.getWord(0) != "a" || wca5.getWord(1) != "b" || wca5.getWord(2) != "c" || wca5.getWord(3) != "d"
                || wca5.getWord(4) != "e") {
            sb.append("\n>>> WordCountsArray.sort ist falsch implementiert (reihenfolge von Woerter passt nicht)");
            right = false;
        }
        if (wca5.getCount(0) != 1 || wca5.getCount(1) != 2 || wca5.getCount(2) != 3 || wca5.getCount(3) != 4
                || wca5.getCount(4) != 5) {
            sb.append("\n>>> WordCountsArray.sort ist falsch implementiert (reihenfolge von Haeufigkeiten passt nicht)");
            right = false;
        }

        // Teste similarity

        double value = wca5.computeSimilarity(wca5);
        if (value != 1) {
            sb.append("\n>>> WordCountsArray.similarity ist falsch implementiert SOLL 1 sein ist " + value);
            right = false;
        }
        WordCountsArray wca6 = new WordCountsArray(5);
        wca6.add("e", 1);
        wca6.add("d", 0);
        wca6.add("c", 0);
        wca6.add("b", 0);
        wca6.add("a", 0);
        wca6.sort();
        value = wca5.computeSimilarity(wca6);
        if (value != 0.674199862463242) {
            sb.append("\n>>> WordCountsArray.similarity ist falsch implementiert SOLL 0.674199862463242 sein ist " + value);
            right = false;
        }
        value = wca5.computeSimilarity(wca);
        if (value != 0) {
            sb.append("\n>>> WordCountsArray.similarity ist falsch implementiert SOLL 0 sein ist " + value);
            right = false;
        }

        assertTrue(sb.toString(), right);
    }

    /**
     * Most common german suffices
     */
    public static final String[] mySUFFICES = { "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent",
            "er", "fach", "fikation", "fizieren", "faehig", "gemaeß", "gerecht", "haft", "haltig", "heit", "ie", "ieren", "ig", "in",
            "ion", "iren", "isch", "isieren", "isierung", "ismus", "ist", "itaet", "iv", "keit", "kunde", "legen", "lein",
            "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll", "wert",
            "wuerdig" };
}
