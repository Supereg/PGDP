import org.junit.Ignore;
import temperature.IteratorTemperatures;
import temperature.StreamTemperatures;
import temperature.Temperatures;

import java.io.File;
import java.util.Date;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.Map.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class TemperaturesTest {

    private static final String csvFilePath = "temperaturesEurope.csv";
    private static final File csvFile = new File(csvFilePath);
    private static final StreamTemperatures streamTemperatures = new StreamTemperatures(csvFile);
    private static final Temperatures iteratorTemperatures = new IteratorTemperatures(csvFile);
    private static final int numTemperatureEntries = 251712;
    private static final String[] cities = {"Bydgoszcz", "Opole", "Tartu", "Linz", "Padova", "Urfa", "Lárisa", "Manisa", "Ulan Ude", "Botosani", "Galati", "Syracuse", "Belgorod", "Denizli", "Uzhhorod", "Radom", "Zhytomyr", "Salzburg", "Arad", "Pátrai", "Podgorica", "Santa Cruz De Tenerife", "Sivas", "Kielce", "Leipzig", "Syktyvkar", "Hanover", "Blagoveshchensk", "Pyatigorsk", "Dimitrovgrad", "Odense", "Tarsus", "Lublin", "Zabrze", "Baia Mare", "Minsk", "Forlì", "Ingolstadt", "Sochi", "Syzran", "Bryansk", "Kütahya", "Kolomna", "Yevpatoriya", "Göttingen", "Novotroitsk", "Novomoskovsk", "Smolensk", "Ust Ilimsk", "Noginsk", "Kislovodsk", "Rome", "Dzerzhinsk", "Voronezh", "Hildesheim", "Petrozavodsk", "Zhukovskiy", "Cluj Napoca", "Reggio Di Calabria", "Kazan", "Rzeszow", "Brest", "Arzamas", "Tallinn", "Kerch", "Heihe", "Engels", "Maykop", "Legnica", "Bacau", "Kursk", "Kherson", "Elektrostal", "Kemerovo", "Çorlu", "Peristérion", "Sergiyev Posad", "Kyzyl", "Iskenderun", "Elblag", "Balti", "Pécs", "Rubtsovsk", "Omsk", "Severodvinsk", "Volzhskiy", "Berezniki", "Bielsko Biala", "Samsun", "Vinnitsa", "Bucharest", "Malmö", "Nalchik", "Copenhagen", "Biysk", "Pavlohrad", "Thessaloníki", "Ulyanovsk", "Brasov", "Samara", "Obninsk", "Glazov", "Novokuznetsk", "Zlatoust", "Tampere", "Bratsk", "Taganrog", "Lvov", "Elbasan", "Parma", "Bologna", "Moscow", "Trento", "Ravenna", "Kostroma", "Magdeburg", "Gebze", "Naples", "Surgut", "Tirana", "Cherkasy", "Kaliningrad", "Kahramanmaras", "Makhachkala", "Cherkessk", "Esenyurt", "Bila Tserkva", "Stavropol", "Pervouralsk", "Tighina", "Adana", "Mezhdurechensk", "Bataysk", "Brescia", "Gaziantep", "Penza", "Volgograd", "Saratov", "Mytishchi", "Serpukhov", "Palermo", "Nizhnekamsk", "Zagreb", "Novosibirsk", "Khimki", "Neftekamsk", "Salerno", "Augsburg", "Berlin", "Prato", "Velikiy Novgorod", "Erfurt", "Iasi", "Salavat", "Debrecen", "Tokat", "Chorzow", "Terni", "Almetyevsk", "Chelyabinsk", "Nizhniy Novgorod", "Elista", "Oradea", "Komsomolsk Na Amure", "Nevinnomyssk", "Balashikha", "Dresden", "Rostov Na Donu", "Ancona", "Västerås", "Velikie Luki", "Kamyshin", "Cheboksary", "Tarnow", "Krasnodar", "Vienna", "Lipetsk", "Magnitogorsk", "Novocherkassk", "Plock", "Lemesos", "Abakan", "Osmaniye", "Krasnoyarsk", "Ordu", "Nazilli", "Durrës", "Kallithéa", "Mersin", "Antalya", "Orël", "Poltava", "Potsdam", "Suceava", "Nakhodka", "Inegol", "Chita", "Kamensk Uralskiy", "Pskov", "Koszalin", "Pinsk", "Vinnytsya", "Sibiu", "Afyonkarahisar", "Wolfsburg", "Sosnowiec", "Chisinau", "Wroclaw", "Warsaw", "Focsani", "Solikamsk", "Kecskemét", "Székesfehérvár", "Çorum", "Oulu", "Sterlitamak", "Balakovo", "Pitesti", "Venice", "Astrakhan", "Budapest", "Irkutsk", "Zelenograd", "Split", "Ratisbon", "Antakya", "Rivne", "Chernihiv", "Cracow", "Vladikavkaz", "Siverek", "Sarapul", "Eskisehir", "Gyor", "Rybinsk", "Nuremberg", "Yekaterinburg", "Leninsk Kuznetskiy", "Helsinki", "Kansk", "Miskolc", "Ivanovo", "Oktyabrskiy", "Orekhovo Zuevo", "Karaman", "Turgutlu", "Nizhnevartovsk", "Prokopyevsk", "Shakhty", "Iráklion", "Kiev", "Zonguldak", "Novocheboksarsk", "Århus", "Giugliano In Campania", "Satu Mare", "Gliwice", "Aksaray", "Kalisz", "Bytom", "Gorzow Wielkopolski", "Graz", "Barnaul", "Tambov", "Saint Petersburg", "Lyubertsy", "Bratislava", "Edirne", "Cottbus", "Mazyr", "Nefteyugansk", "Salzgitter", "Ukhta", "Bergamo", "Espoo", "Latina", "Torun", "Rimini", "Bari", "Innsbruck", "Reykjavík", "Zaporizhzhya", "Katowice", "Salihorsk", "Messina", "Tver", "Nizhniy Tagil", "Norilsk", "Achinsk", "Catania", "Chemnitz", "Kosice", "Siirt", "Ankara", "Wloclawek", "Leghorn", "Turhal", "Angarsk", "Olsztyn", "Buzau", "Van", "Bialystok", "Modena", "Florence", "Halle", "Kurgan", "Yaroslavl", "Dabrowa Gornicza", "Szczecin", "Nazran", "Novorossiysk", "Gdansk", "Braila", "Walbrzych", "Istanbul", "Kaluga", "Odintsovo", "Brunswick", "Munich", "Hrodna", "Uppsala", "Athens", "Miass", "Drobeta Turnu Severin", "Ussuriysk", "Bursa", "Perm", "Tychy", "Erzincan", "Izhevsk", "Szeged", "Usak", "Makiyivka", "Yelets", "Erzurum", "Orsha", "Tekirdag", "Arkhangelsk", "Rijeka", "Derbent", "Craiova", "Las Palmas", "Tyumen", "Czestochowa", "Malatya", "Chernivtsi", "Ferrara", "Izmir", "Petropavlovsk Kamchatskiy", "Izmit", "Constanta", "Cherepovets", "Murmansk", "Kremenchuk", "Monza", "Batman", "Kovrov", "Ruda Slaska", "Rostock", "Viransehir", "Kryvyy Rih", "Perugia", "Vologda", "Armavir", "Aalborg", "Ploiesti", "Verona", "Murom", "Zielona Gora", "Staryy Oskol", "Tolyatti", "Sumy", "Vladivostok", "Gdynia", "Podolsk", "Gera", "Rybnik", "Targu Mures", "Kayseri", "Saransk", "Zelenodolsk", "Trieste", "Trabzon", "Seversk", "Konya", "Vicenza", "Stockholm", "Timisoara", "Horlivka", "Vladimir", "Kirovohrad", "Orsk", "Pescara", "Khabarovsk", "Kirov", "Ryazan", "Alanya", "Turku", "Gomel", "Ufa", "Piatra Neamt", "Odesa", "Poznan", "Tomsk", "Ramnicu Valcea", "Foggia", "Nyíregyháza", "Kolpino", "Göteborg", "Taranto", "Ljubljana", "Tula", "Isparta", "Volgodonsk"};
    private static final String[] countries = {"Romania", "Hungary", "Cyprus", "Ukraine", "Moldova", "Belarus", "Iceland", "Russia", "Albania", "Spain", "Greece", "Sweden", "Austria", "Turkey", "Finland", "Denmark", "Poland", "Italy", "Slovakia", "Slovenia", "Germany", "Montenegro", "Croatia", "Estonia"};
    private static final Map<String, Double> countriesAvgTemp = Map.ofEntries(entry("Romania", 9.473385657894736), entry("Hungary", 10.39584283625731), entry("Cyprus", 19.543409539473686), entry("Ukraine", 8.535116981907894), entry("Moldova", 9.317747807017543), entry("Belarus", 6.846991159539473), entry("Iceland", 1.9302483552631577), entry("Russia", 3.956121647267207), entry("Albania", 15.957501644736842), entry("Spain", 20.60464967105263), entry("Greece", 16.789025140977444), entry("Sweden", 6.263319078947369), entry("Austria", 6.90260625), entry("Turkey", 13.501328624871002), entry("Finland", 4.358546381578948), entry("Denmark", 8.397770559210526), entry("Poland", 8.306966023199447), entry("Italy", 13.438546692251464), entry("Slovakia", 9.263670230263157), entry("Slovenia", 10.06564967105263), entry("Germany", 8.514698639354068), entry("Montenegro", 10.68434375), entry("Croatia", 11.136362938596491), entry("Estonia", 5.38503125));
    private static final Map<String, Double> countriesAvgTempDelta = Map.ofEntries(entry("Globally", 0.04171878743895208), entry("Cyprus", 0.02352553542009886), entry("Iceland", 0.022085667215815483), entry("Russia", 0.05496047184556245), entry("Greece", 0.030875264768180755), entry("Sweden", 0.03971004942339374), entry("Austria", 0.04367578253706754), entry("Poland", 0.05012928119309806), entry("Slovakia", 0.04919522240527183), entry("Slovenia", 0.0445667215815486), entry("Croatia", 0.043931905546403076), entry("Romania", 0.05166036243822077), entry("Hungary", 0.04938037708218927), entry("Ukraine", 0.05510056287753981), entry("Moldova", 0.05615540911587041), entry("Belarus", 0.05408916803953871), entry("Albania", 0.032042833607907745), entry("Spain", 0.012960461285008234), entry("Turkey", 0.033189747068514394), entry("Finland", 0.04378088962108732), entry("Denmark", 0.03841268533772653), entry("Italy", 0.03663783635365184), entry("Germany", 0.0433205781039389), entry("Montenegro", 0.04281383855024712), entry("Estonia", 0.049050247116968704));
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
    public void countriesIteratorTemperatures() {
        Set<String> c = iteratorTemperatures.countries();
        assertTrue(
                Arrays
                        .stream(countries)
                        .map(c::contains)
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void countriesAvgTemperatureStreamTemperatures() {
        //temperaturesByCountry() wird hier mitgetestet
        Map<String, Double> countriesAvgTemp2 = streamTemperatures.countriesAvgTemperature();
        assertEquals(countriesAvgTemp.keySet(), countriesAvgTemp2.keySet());
        assertTrue(
                countriesAvgTemp
                        .keySet()
                        .stream()
                        .map(country -> countriesAvgTemp.get(country).equals(countriesAvgTemp2.get(country)))
                        .reduce((bool1, bool2) -> bool1 && bool2)
                        .get()
        );
    }

    @org.junit.Test
    public void countriesAvgTemperatureIteratorTemperatures() {
        //temperaturesByCountry() wird hier mitgetestet
        Map<String, Double> countriesAvgTemp2 = iteratorTemperatures.countriesAvgTemperature();
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
        assertEquals(streamTemperatures.hottestCountryAvg(), "Spain");
    }

    @org.junit.Test
    public void hottestCountryAvgIteratorTemperatures() {
        assertEquals(iteratorTemperatures.hottestCountryAvg(), "Spain");
    }

    @org.junit.Test
    public void avgTemperatureDeltaPerYearPerCountryStreamTemperatures() {
        Map<String, Double> countriesAvgTempDelta2 = streamTemperatures.avgTemperatureDeltaPerYearPerCountry();
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

    @org.junit.Test
    public void mainStreamTemperatures() {
        assertTrue("Manually check console window for output of StreamTemperatures.main()", false);
        System.out.println("\n---\nStreamTemperatures.main()\n---\n");
        StreamTemperatures.main(new String[]{csvFilePath});
    }

    @org.junit.Test
    public void mainIteratorTemperatures() {
        assertTrue("Manually check console window for output of IteratorTemperatures.main()", false);
        System.out.println("\n---\nIteratorTemperatures.main()\n---\n");
        IteratorTemperatures.main(new String[]{csvFilePath});
    }
}