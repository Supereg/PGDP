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
import java.util.Map.Entry;
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
            System.out.println("Parsing file...");
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
                System.out.println("Downloading file...");
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

    public void printSummary() {
        System.out.println("Total entries: " + size());

        //for (Date date: dates())
        //    System.out.println(DATE_FORMAT.format(date));

        Set<String> cities = cities();
        System.out.println("Found " + cities.size() + " cities: " + cities);

        Set<String> countries = countries();
        System.out.println("Found " + countries.size() + " countries: " + countries);

        // temperaturesByCountry();
        System.out.println();

        System.out.println("Coldest country absolute: " + coldestCountryAbs());
        System.out.println("Hottest country absolute: " + hottestCountryAbs());

        System.out.println();

        System.out.println("Coldest country average: " + coldestCountryAvg());
        System.out.println("Hottest country average: " + hottestCountryAvg());

        System.out.println();
        System.out.println("Country\t\t\tAverage Temperature");
        System.out.println("-----------------------------------");
        for (Entry<String, Double> entry: countriesAvgTemperature().entrySet()) {
            int tabs = 4 - entry.getKey().length() / 4;
            System.out.println(entry.getKey() + "\t".repeat(tabs) + entry.getValue());
        }

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

}