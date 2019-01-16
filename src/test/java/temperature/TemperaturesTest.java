package temperature;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Map.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TemperaturesTest {

    private static final String csvFilePath = "temperaturesEurope.csv";
    private static final File csvFile = new File(csvFilePath);
    private static final StreamTemperatures streamTemperatures = new StreamTemperatures(csvFile);
    private static final Temperatures iteratorTemperatures = new IteratorTemperatures(csvFile);
    private static final int numTemperatureEntries = 372096;
    private static final String[] cities = {"Bordeaux", "Bydgoszcz", "Opole", "Tartu", "Linz", "Padova", "Urfa", "Bielefeld", "Lárisa", "Manisa", "Ulan Ude", "Botosani", "Galati", "Moers", "Novara", "Syracuse", "Amersfoort", "Belgorod", "Denizli", "Uzhhorod", "Jaén", "Oberhausen", "Radom", "Zhytomyr", "Neuss", "Lisbon", "Lausanne", "Tours", "Salzburg", "Arad", "Pátrai", "Podgorica", "Santa Cruz De Tenerife", "Reims", "Freiburg", "Sivas", "Clermont Ferrand", "Kielce", "Leipzig", "Offenbach", "Syktyvkar", "Hanover", "Saint Étienne", "Blagoveshchensk", "Angers", "Le Havre", "Lille", "Pyatigorsk", "Zoetermeer", "Dimitrovgrad", "Odense", "Tarsus", "Oviedo", "Basel", "Lublin", "Bottrop", "Hamm", "Zabrze", "Baia Mare", "Minsk", "Forlì", "Lyon", "Ingolstadt", "Sochi", "Syzran", "Bryansk", "Kütahya", "Kolomna", "Santander", "Brugge", "Aachen", "Yevpatoriya", "Göttingen", "Solingen", "Marseille", "Novotroitsk", "Cadiz", "Novomoskovsk", "Smolensk", "Ust Ilimsk", "Krefeld", "Noginsk", "Kislovodsk", "Rome", "Ulm", "Dzerzhinsk", "Cagliari", "Barcelona", "Zwolle", "Voronezh", "Hildesheim", "Petrozavodsk", "Zhukovskiy", "Cluj Napoca", "Reggio Di Calabria", "Heidelberg", "Kazan", "Mannheim", "Rzeszow", "Brest", "Arzamas", "Tallinn", "Granada", "Essen", "Kerch", "Heihe", "Rotterdam", "Engels", "Maykop", "Bremen", "Legnica", "Rouen", "Bacau", "Kursk", "Alcalá De Henares", "Kherson", "Elektrostal", "Kemerovo", "Çorlu", "Perpignan", "Peristérion", "Sergiyev Posad", "Groningen", "Kyzyl", "Iskenderun", "Elblag", "Balti", "Pécs", "Rubtsovsk", "Omsk", "Kassel", "Haarlemmermeer", "Severodvinsk", "Volzhskiy", "Berezniki", "Karlsruhe", "Sassari", "Bielsko Biala", "Samsun", "Vinnitsa", "Bucharest", "Queluz", "Stavanger", "Malmö", "Nalchik", "Albacete", "Copenhagen", "León", "Biysk", "Haarlem", "Pavlohrad", "Genoa", "Thessaloníki", "Ulyanovsk", "Brasov", "Samara", "Obninsk", "Boulogne Billancourt", "Glazov", "Novokuznetsk", "Zlatoust", "Tampere", "Jerez", "Bratsk", "Taganrog", "Lvov", "Elbasan", "Madrid", "Mülheim", "Cologne", "Bochum", "Parma", "Bologna", "Huelva", "Moscow", "Trento", "Mataró", "Ravenna", "Orléans", "Kostroma", "Magdeburg", "Gebze", "Naples", "Saarbrücken", "Braga", "Surgut", "Tirana", "Cherkasy", "Mönchengladbach", "Kaliningrad", "Kahramanmaras", "Makhachkala", "Cherkessk", "Esenyurt", "Liège", "Namur", "Bila Tserkva", "Stavropol", "Pervouralsk", "Tighina", "Adana", "Mezhdurechensk", "Bataysk", "Brescia", "Gaziantep", "Penza", "Volgograd", "Fürth", "Saratov", "Almere", "Mytishchi", "Serpukhov", "Dublin", "Palermo", "Nizhnekamsk", "Zagreb", "Novosibirsk", "Khimki", "Neftekamsk", "Salerno", "Augsburg", "Berlin", "Lübeck", "Prato", "Velikiy Novgorod", "Erfurt", "Mainz", "Iasi", "Salavat", "Debrecen", "Tokat", "Chorzow", "Sevilla", "Terni", "Almetyevsk", "Chelyabinsk", "Caen", "Nizhniy Novgorod", "Elista", "Bern", "Oradea", "Setúbal", "Frankfurt", "Komsomolsk Na Amure", "Nevinnomyssk", "Balashikha", "Dresden", "Rostov Na Donu", "Zaanstad", "Ancona", "Västerås", "Velikie Luki", "Kamyshin", "Cheboksary", "Tarnow", "Krasnodar", "Paderborn", "Vienna", "Lipetsk", "Magnitogorsk", "Novocherkassk", "Pforzheim", "Plock", "Lemesos", "Abakan", "Osmaniye", "Krasnoyarsk", "Ordu", "Nazilli", "Durrës", "Kallithéa", "Mersin", "Paris", "Antalya", "Turin", "Orël", "Poltava", "Potsdam", "Suceava", "Nakhodka", "Leganés", "Inegol", "Chita", "Lleida", "Kamensk Uralskiy", "Pskov", "Koszalin", "Pinsk", "Vinnytsya", "Ludwigshafen", "Metz", "Oldenburg", "Sibiu", "Logroño", "Afyonkarahisar", "Pamplona", "Wolfsburg", "Sosnowiec", "Chisinau", "Wroclaw", "Warsaw", "Focsani", "Solikamsk", "Kecskemét", "Erlangen", "Székesfehérvár", "Çorum", "Oulu", "Sterlitamak", "Balakovo", "Pitesti", "Venice", "Astrakhan", "Budapest", "Nijmegen", "Irkutsk", "Milan", "Zelenograd", "Torrejón De Ardoz", "Le Mans", "Split", "Ratisbon", "Antakya", "Rivne", "Würzburg", "Chernihiv", "Hamburg", "Geneva", "Cracow", "Vladikavkaz", "Düsseldorf", "Siverek", "Tarragona", "Valladolid", "Sarapul", "Eskisehir", "Gyor", "Rybinsk", "Nuremberg", "Yekaterinburg", "Leninsk Kuznetskiy", "Helsinki", "Kansk", "Miskolc", "Ivanovo", "Tilburg", "Oktyabrskiy", "Orekhovo Zuevo", "Heilbronn", "Karaman", "Turgutlu", "Nizhnevartovsk", "Toulon", "Prokopyevsk", "Kiel", "Shakhty", "Burgos", "Iráklion", "Kiev", "Zonguldak", "Recklinghausen", "Zurich", "Novocheboksarsk", "Århus", "Giugliano In Campania", "Bergisch Gladbach", "Villeurbanne", "Satu Mare", "Gliwice", "Aksaray", "Kalisz", "Bytom", "Gorzow Wielkopolski", "Oslo", "Graz", "Barnaul", "Vitoria", "Tambov", "Saint Petersburg", "Lyubertsy", "Bratislava", "Edirne", "Cottbus", "Eindhoven", "Zaragoza", "Charleroi", "Mazyr", "Málaga", "Nefteyugansk", "Salzgitter", "Ukhta", "Bergamo", "Espoo", "Latina", "Torun", "Münster", "Rimini", "Coimbra", "Bari", "Innsbruck", "Nancy", "Alcorcón", "Vigo", "Arnhem", "Reykjavík", "Reutlingen", "Murcia", "Zaporizhzhya", "Katowice", "Utrecht", "Bergen", "Breda", "Salihorsk", "Messina", "Enschede", "Osnabrück", "Tver", "Badalona", "Móstoles", "Dijon", "Nizhniy Tagil", "Norilsk", "Achinsk", "Catania", "Chemnitz", "Gelsenkirchen", "Kosice", "Rennes", "Bonn", "Amadora", "Siirt", "Ankara", "Wloclawek", "Leghorn", "Turhal", "Angarsk", "Olsztyn", "Buzau", "Van", "Bialystok", "Modena", "Florence", "Getafe", "Halle", "Córdoba", "Wiesbaden", "Kurgan", "Yaroslavl", "Dabrowa Gornicza", "Szczecin", "Nazran", "Novorossiysk", "Gdansk", "Grenoble", "Antwerp", "Koblenz", "Braila", "Walbrzych", "Istanbul", "Kaluga", "Odintsovo", "Brunswick", "Munich", "Hrodna", "Uppsala", "Athens", "Miass", "Drobeta Turnu Severin", "Montpellier", "Amiens", "Ussuriysk", "Bursa", "Perm", "Tychy", "Fuenlabrada", "Erzincan", "Izhevsk", "Szeged", "Usak", "Makiyivka", "Yelets", "Erzurum", "Orsha", "Herne", "Limoges", "Tekirdag", "Arkhangelsk", "Rijeka", "Derbent", "Aix En Provence", "Craiova", "Las Palmas", "Tyumen", "Marbella", "Amsterdam", "Czestochowa", "Malatya", "Chernivtsi", "Ferrara", "Izmir", "Nice", "Petropavlovsk Kamchatskiy", "Izmit", "Constanta", "Duisburg", "Cherepovets", "Murmansk", "Kremenchuk", "Monza", "Gent", "Batman", "Kovrov", "Witten", "Ruda Slaska", "Rostock", "Viransehir", "Kryvyy Rih", "The Hague", "Leiden", "Perugia", "Vologda", "Armavir", "Aalborg", "Porto", "Ploiesti", "Verona", "Murom", "Zielona Gora", "Ede", "Staryy Oskol", "Alcobendas", "Cartagena", "Darmstadt", "Remscheid", "Tolyatti", "Sumy", "Vladivostok", "Gdynia", "Podolsk", "Gera", "Sabadell", "Bilbao", "Rybnik", "Targu Mures", "Kayseri", "Saransk", "Zelenodolsk", "Leverkusen", "Gijón", "Trondheim", "Trieste", "Brussels", "Trabzon", "Seversk", "Terrassa", "Konya", "Maastricht", "Vicenza", "Badajoz", "Toulouse", "Trier", "Bremerhaven", "Stockholm", "Timisoara", "Horlivka", "Apeldoorn", "Hagen", "Vladimir", "Kirovohrad", "Orsk", "Nîmes", "A Coruña", "Pescara", "Stuttgart", "Khabarovsk", "Dortmund", "Kirov", "Ryazan", "Alanya", "Algeciras", "Turku", "Gomel", "Mulhouse", "Ufa", "Piatra Neamt", "Odesa", "Siegen", "Poznan", "Tomsk", "Palma", "Ramnicu Valcea", "Wuppertal", "Foggia", "Nyíregyháza", "Kolpino", "Göteborg", "Nantes", "Salamanca", "Besançon", "Cork", "Taranto", "Dos Hermanas", "Ljubljana", "Dordrecht", "Strasbourg", "Emmen", "Tula", "Almería", "Isparta", "Volgodonsk", "Valencia"};
    private static final String[] countries = {"Cyprus", "Portugal", "Iceland", "Russia", "Greece", "Netherlands", "Sweden", "Austria", "Ireland", "Poland", "Slovakia", "Slovenia", "France", "Croatia", "Romania", "Hungary", "Ukraine", "Moldova", "Belarus", "Switzerland", "Spain", "Albania", "Turkey", "Belgium", "Norway", "Finland", "Denmark", "Italy", "Germany", "Montenegro", "Estonia"};
    private static final Map<String, Double> countriesAvgTemp = Map.ofEntries(entry("Cyprus", 19.543409539473686), entry("Portugal", 15.23932377819549), entry("Iceland", 1.9302483552631577), entry("Russia", 3.956121647267207), entry("Greece", 16.789025140977444), entry("Netherlands", 9.687507264254386), entry("Sweden", 6.263319078947369), entry("Austria", 6.90260625), entry("Ireland", 9.60183552631579), entry("Poland", 8.306966023199447), entry("Slovakia", 9.263670230263157), entry("Slovenia", 10.06564967105263), entry("France", 10.896091602704677), entry("Croatia", 11.136362938596491), entry("Romania", 9.473385657894736), entry("Hungary", 10.39584283625731), entry("Ukraine", 8.535116981907894), entry("Moldova", 9.317747807017543), entry("Belarus", 6.846991159539473), entry("Switzerland", 8.13566480263158), entry("Spain", 15.019710986842105), entry("Albania", 15.957501644736842), entry("Turkey", 13.501328624871002), entry("Belgium", 10.299342810150375), entry("Norway", 4.169696957236842), entry("Finland", 4.358546381578948), entry("Denmark", 8.397770559210526), entry("Italy", 13.169356712092734), entry("Germany", 9.098921235380118), entry("Montenegro", 10.68434375), entry("Estonia", 5.38503125));
    private static final Map<String, Double> countriesAvgTempDelta = Map.ofEntries(entry("Globally", 0.04180089743695624), entry("Cyprus", 0.015007500000000036), entry("Portugal", 0.024267619047619014), entry("Iceland", 0.035724166666666675), entry("Russia", 0.05631454594017096), entry("Greece", 0.030444285714285754), entry("Netherlands", 0.03418624999999998), entry("Sweden", 0.03479516666666667), entry("Austria", 0.04742433333333333), entry("Ireland", 0.026464999999999995), entry("Poland", 0.049894254385964905), entry("Slovakia", 0.056125416666666664), entry("Slovenia", 0.04946666666666665), entry("France", 0.03514935185185185), entry("Croatia", 0.04781805555555554), entry("Romania", 0.05889966666666666), entry("Hungary", 0.05899083333333334), entry("Ukraine", 0.07034451388888889), entry("Moldova", 0.0655913888888889), entry("Belarus", 0.06214010416666667), entry("Switzerland", 0.03716116666666668), entry("Spain", 0.024748833333333345), entry("Albania", 0.026974166666666688), entry("Turkey", 0.03446887254901961), entry("Belgium", 0.03550976190476193), entry("Norway", 0.023666874999999997), entry("Finland", 0.043954833333333346), entry("Denmark", 0.03453041666666666), entry("Italy", 0.03246390873015873), entry("Germany", 0.03924569958847737), entry("Montenegro", 0.046323333333333314), entry("Estonia", 0.057730833333333356));
    private static final double epsilon = 0.0001;
    private static final int numDates = 608;

    @org.junit.BeforeClass
    public static void sortArrays() {
        Arrays.sort(cities);
        Arrays.sort(countries);
    }

    @org.junit.Test
    public void sizeStreamTemperatures() {
        assertEquals(streamTemperatures.size(), numTemperatureEntries);
    }

    @org.junit.Test
    public void sizeIteratorTemperatures() {
        assertEquals(iteratorTemperatures.size(), numTemperatureEntries);
    }

    @org.junit.Test
    public void datesStreamTemperatures() {
        final List<Date> dates = streamTemperatures.dates();
        assertEquals(dates.size(), numDates);
        assertTrue(
                IntStream
                        .range(0, dates.size() - 1)
                        .boxed()
                        .map(i -> dates.get(i).compareTo(dates.get(i + 1)) < 0)
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void datesIteratorTemperatures() {
        final List<Date> dates = iteratorTemperatures.dates();
        assertEquals(dates.size(), numDates);
        assertTrue(
                IntStream
                        .range(0, dates.size() - 1)
                        .boxed()
                        .map(i -> dates.get(i).compareTo(dates.get(i + 1)) < 0)
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void citiesStreamTemperatures() {
        final Object[] c = streamTemperatures
                .cities()
                .stream()
                .sorted()
                .toArray();
        assertEquals(c.length, cities.length);
        assertTrue(
                IntStream
                        .range(0, c.length)
                        .boxed()
                        .map(i -> c[i].toString().equals(cities[i]))
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void citiesIteratorTemperatures() {
        final Object[] c = iteratorTemperatures
                .cities()
                .stream()
                .sorted()
                .toArray();
        assertEquals(c.length, cities.length);
        assertTrue(
                IntStream
                        .range(0, c.length)
                        .boxed()
                        .map(i -> c[i].toString().equals(cities[i]))
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void countriesStreamTemperatures() {
        final Object[] c = streamTemperatures
                .countries()
                .stream()
                .sorted()
                .toArray();
        assertEquals(c.length, countries.length);
        assertTrue(
                IntStream
                        .range(0, c.length)
                        .boxed()
                        .map(i -> c[i].toString().equals(countries[i]))
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void countriesIteratorTemperatures() {
        final Object[] c = iteratorTemperatures
                .countries()
                .stream()
                .sorted()
                .toArray();
        assertEquals(c.length, countries.length);
        assertTrue(
                IntStream
                        .range(0, c.length)
                        .boxed()
                        .map(i -> c[i].toString().equals(countries[i]))
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void countriesAvgTemperatureStreamTemperatures() {
        //temperaturesByCountry() wird hier mitgetestet
        final Map<String, Double> countriesAvgTemp2 = streamTemperatures.countriesAvgTemperature();
        assertEquals(countriesAvgTemp2.keySet(), countriesAvgTemp.keySet());
        for (String key: countriesAvgTemp.keySet()) {
            assertEquals("Unexpected value for key " + key,countriesAvgTemp.get(key), countriesAvgTemp2.get(key), 10E-10);
        }
    }

    @org.junit.Test
    public void countriesAvgTemperatureIteratorTemperatures() {
        //temperaturesByCountry() wird hier mitgetestet
        final Map<String, Double> countriesAvgTemp2 = iteratorTemperatures.countriesAvgTemperature();
        assertEquals(countriesAvgTemp2.keySet(), countriesAvgTemp.keySet());
        countriesAvgTemp
                .keySet()
                .forEach(country ->
                        assertEquals(
                                countriesAvgTemp.get(country),
                                countriesAvgTemp2.get(country),
                                epsilon
                        )
                );
    }

    @org.junit.Test
    public void coldestCountryAbsStreamTemperatures() {
        assertEquals(streamTemperatures.coldestCountryAbs(), "Russia");
    }

    @org.junit.Test
    public void coldestCountryAbsIteratorTemperatures() {
        assertEquals(iteratorTemperatures.coldestCountryAbs(), "Russia");
    }

    @org.junit.Test
    public void hottestCountryAbsStreamTemperatures() {
        assertEquals(streamTemperatures.hottestCountryAbs(), "Turkey");
    }

    @org.junit.Test
    public void hottestCountryAbsIteratorTemperatures() {
        assertEquals(iteratorTemperatures.hottestCountryAbs(), "Turkey");
    }

    @org.junit.Test
    public void coldestCountryAvgStreamTemperatures() {
        assertEquals(streamTemperatures.coldestCountryAvg(), "Iceland");
    }

    @org.junit.Test
    public void coldestCountryAvgIteratorTemperatures() {
        assertEquals(iteratorTemperatures.coldestCountryAvg(), "Iceland");
    }

    @org.junit.Test
    public void hottestCountryAvgStreamTemperatures() {
        assertEquals(streamTemperatures.hottestCountryAvg(), "Cyprus");
    }

    @org.junit.Test
    public void hottestCountryAvgIteratorTemperatures() {
        assertEquals(iteratorTemperatures.hottestCountryAvg(), "Cyprus");
    }

    @org.junit.Test
    public void avgTemperatureDeltaPerYearPerCountryStreamTemperatures() {
        final Map<String, Double> countriesAvgTempDelta2 = streamTemperatures.avgTemperatureDeltaPerYearPerCountry();
        assertEquals(countriesAvgTempDelta2.keySet(), countriesAvgTempDelta.keySet());
        countriesAvgTempDelta
                .keySet()
                .forEach(country ->
                        assertEquals(
                                countriesAvgTempDelta.get(country),
                                countriesAvgTempDelta2.get(country),
                                epsilon
                        )
                );
    }

}