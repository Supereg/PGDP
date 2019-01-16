package temperature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Temperatures implements Iterable<Temperature> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    protected final List<Temperature> temperatureList;

    public Temperatures(Iterable<Temperature> temperatures) {
        temperatureList = new ArrayList<>();
        temperatures.forEach(temperatureList::add);
    }

    public Temperatures(File csv) {
        List<Temperature> temperatures;
        try (BufferedReader in = new BufferedReader(new FileReader(csv))) {
            temperatures = parseCSV(in);
        } catch (Exception e) {
            temperatures = new ArrayList<>();
        }

        temperatureList = temperatures;
    }

    public Temperatures(URL url) {
        List<Temperature> temperatures;
        try {
            URLConnection connection = url.openConnection(); // @see docs
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                temperatures = parseCSV(in);
            }
        } catch (Exception e) {
            temperatures = new ArrayList<>();
        }

        temperatureList = temperatures;
    }

    private List<Temperature> parseCSV(BufferedReader in) {
        return in.lines().skip(1).map(line -> {
            String[] fields = line.split(",");
            if (fields.length != 7)
                return null;

            try {
                Date date = DATE_FORMAT.parse(fields[0]);
                double averageTemperature = Double.parseDouble(fields[1]);
                double averageTemperatureUncertainty = Double.parseDouble(fields[2]);
                String city = fields[3];
                String country = fields[4];

                String latitudeString = fields[5];
                String longitudeString = fields[6];

                double latitude = Double.parseDouble(latitudeString.substring(0, latitudeString.length() - 1));
                double longitude = Double.parseDouble(longitudeString.substring(0, longitudeString.length() - 1));
                return new Temperature(date, averageTemperature, averageTemperatureUncertainty, city, country, latitude, longitude);
            } catch (Exception e) {
                // "2013-09-01,,,Zhytomyr,Ukraine,50.63N,29.15E" typical line -> NumberFormatException: empty String
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SuppressWarnings("Duplicates")
    public void printSummary() {
        long sizeTime = benchmark(this::size, size -> System.out.println("Total entries: " + size));

        //for (Date date: dates())
        //    System.out.println(DATE_FORMAT.format(date));

        long citiesTime = benchmark(this::cities, cities -> System.out.println("Found " + cities.size() + " cities: " + cities));
        long countriesTime = benchmark(this::countries, countries -> System.out.println("Found " + countries.size() + " countries: " + countries));

        // temperaturesByCountry();
        System.out.println();

        long coldestCountryAbsTime = benchmark(this::coldestCountryAbs,
                coldestCountryAbs -> System.out.println("Coldest country absolute: " + coldestCountryAbs));
        long hottestCountryAbsTime = benchmark(this::hottestCountryAbs,
                hottestCountryAbs -> System.out.println("Hottest country absolute: " + hottestCountryAbs));

        System.out.println();

        long coldestCountryAvgTime = benchmark(this::coldestCountryAvg,
                coldestCountryAvg -> System.out.println("Coldest country average: " + coldestCountryAvg));
        long hottestCountryAvgTime = benchmark(this::hottestCountryAvg,
                hottestCountryAvg -> System.out.println("Hottest country average: " + hottestCountryAvg));

        System.out.println();
        System.out.println("Country\t\t\t\t\t\t\t\tAverage Temperature");
        System.out.println("-------------------------------------------------------");
        long countriesAvgTemperatureTime = benchmark(this::countriesAvgTemperature,
                countriesAvgTemperature -> countriesAvgTemperature.forEach((key, value) -> {
                    int tabs = Math.max(0, 9 - key.length() / 4);
                    System.out.println(key + "\t".repeat(tabs) + value);
                }));

        long avgTemperatureDeltaPerYearPerCountryTime = -1;
        if (this instanceof StreamTemperatures) {
            System.out.println();
            System.out.println();

            System.out.println("Country\t\t\t\t\t\t\t\tAverage temperature delta per year");
            System.out.println("----------------------------------------------------------------------");

            avgTemperatureDeltaPerYearPerCountryTime = benchmark(((StreamTemperatures) this)::avgTemperatureDeltaPerYearPerCountry,
                    avgTemperatureDeltaPerYearPerCountry -> avgTemperatureDeltaPerYearPerCountry.forEach((key, value) -> {
                        // 4 tabs should be enough for country length
                        int tabs = Math.max(0, 9 - key.length() / 4);
                        System.out.println(key + "\t".repeat(tabs) + value);
                    }));
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Timings:");
        System.out.println("--------");
        System.out.println("#size() took " + formatTime(sizeTime));
        System.out.println("#citites() took " + formatTime(citiesTime));
        System.out.println("#countries() took " + formatTime(countriesTime));
        System.out.println("#coldestCountryAbs() took " + formatTime(coldestCountryAbsTime));
        System.out.println("#hottestCountryAbs() took " + formatTime(hottestCountryAbsTime));
        System.out.println("#coldestCountryAvg() took " + formatTime(coldestCountryAvgTime));
        System.out.println("#hottestCountryAvg() took " + formatTime(hottestCountryAvgTime));
        System.out.println("#countriesAvgTemperature() took " + formatTime(countriesAvgTemperatureTime));
        if (avgTemperatureDeltaPerYearPerCountryTime >= 0)
            System.out.println("#avgTemperatureDeltaPerYearPerCountry() took " + formatTime(avgTemperatureDeltaPerYearPerCountryTime));
    }

    private <T> long benchmark(Supplier<T> function, Consumer<T> callback) {
        long start = System.currentTimeMillis();
        T t = function.get();
        long stop = System.currentTimeMillis();
        callback.accept(t);
        return stop - start;
    }

    private String formatTime(long timeMillis) {
        double seconds = timeMillis / 1000D;
        return seconds + "s";
    }

    public abstract long size();

    public abstract List<Date> dates();

    public abstract Set<String> cities();

    public abstract Set<String> countries();

    public abstract Map<String, Temperatures> temperaturesByCountry();

    public abstract String coldestCountryAbs();

    public abstract String hottestCountryAbs();

    public abstract String coldestCountryAvg();

    public abstract String hottestCountryAvg();

    public abstract Map<String, Double> countriesAvgTemperature();

    @Override
    public Iterator<Temperature> iterator() {
        return temperatureList.iterator();
    }

    public Stream<Temperature> stream() {
        return StreamSupport.stream(Spliterators.spliterator(temperatureList.iterator(), temperatureList.size(), 0), false);
    }

    public Stream<Temperature> parallelStream() {
        return StreamSupport.stream(Spliterators.spliterator(temperatureList.iterator(), temperatureList.size(), 0), true);
    }

}