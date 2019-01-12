package temperature;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

public class IteratorTemperatures extends Temperatures {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            System.out.println("Required args[0] = url/filepath!");
            return;
        }

        Temperatures temperatures;
        try {
            temperatures = new IteratorTemperatures(new URL(args[0]));
        } catch (MalformedURLException ignored) {
            // it's not a url so probably a File
            File file = new File(args[0]);
            if (!file.exists()) {
                System.out.println("File '" + file.getName()+ "' does not exist!");
                return;
            }

            temperatures = new IteratorTemperatures(file);
        }

        temperatures.printSummary();
    }

    public IteratorTemperatures(Iterable<Temperature> temperatures) {
        super(temperatures);
    }

    public IteratorTemperatures(File csv) {
        super(csv);
    }

    public IteratorTemperatures(URL url) {
        super(url);
    }

    @Override
    public long size() {
        // return temperatureList.size();
        // it stated explicitly that we are supposed to use iterators/foreach, so don't know if the above is illegal
        long size = 0;
        for (@SuppressWarnings("unused") Temperature temperature: this)
            size++;
        return size;
    }

    @Override
    public List<Date> dates() {
        Set<Date> dates = new TreeSet<>();
        for (Temperature temperature: this)
            dates.add(temperature.getDate());

        return new ArrayList<>(dates);
    }

    @Override
    public Set<String> cities() {
        Set<String> cities = new HashSet<>(temperatureList.size());
        for (Temperature temperature: this)
            cities.add(temperature.getCity());

        return cities;
    }

    @Override
    public Set<String> countries() {
        Set<String> countries = new HashSet<>(temperatureList.size());
        for (Temperature temperature: this)
            countries.add(temperature.getCountry());

        return countries;
    }

    @Override
    public Map<String, Temperatures> temperaturesByCountry() {
        Map<String, Set<Temperature>> map = new HashMap<>();

        for (Temperature temperature: this) {
            Set<Temperature> set = map.get(temperature.getCountry());
            //noinspection Java8MapApi
            if (set == null) {
                set = new HashSet<>();
                map.put(temperature.getCountry(), set);
            }

            set.add(temperature);
        }

        Map<String, Temperatures> result = new HashMap<>(map.size());
        for (Entry<String, Set<Temperature>> entry: map.entrySet())
            result.put(entry.getKey(), new IteratorTemperatures(entry.getValue()));

        return result;
    }

    @Override
    public String coldestCountryAbs() {
        String country = null;
        double coldestTemperature = Double.MAX_VALUE;

        for (Temperature temperature: this) {
            if (temperature.getAverageTemperature() < coldestTemperature) {
                country = temperature.getCountry();
                coldestTemperature = temperature.getAverageTemperature();
            }
        }

        return country;
    }

    @Override
    public String hottestCountryAbs() {
        String country = null;
        double hottestTemperature = Double.MIN_VALUE;

        for (Temperature temperature: this) {
            if (temperature.getAverageTemperature() > hottestTemperature) {
                country = temperature.getCountry();
                hottestTemperature = temperature.getAverageTemperature();
            }
        }

        return country;
    }

    @Override
    public String coldestCountryAvg() {
        Map<String, Double> countriesAvgTemperature = countriesAvgTemperature();

        String country = null;
        Double coldestAvg = Double.MAX_VALUE;

        for (Entry<String, Double> entry: countriesAvgTemperature.entrySet()) {
            if (entry.getValue() < coldestAvg) {
                country = entry.getKey();
                coldestAvg = entry.getValue();
            }
        }

        return country;
    }

    @Override
    public String hottestCountryAvg() {
        Map<String, Double> countriesAvgTemperature = countriesAvgTemperature();

        String country = null;
        Double hottestAvg = Double.MIN_VALUE;

        for (Entry<String, Double> entry: countriesAvgTemperature.entrySet()) {
            if (entry.getValue() > hottestAvg) {
                country = entry.getKey();
                hottestAvg = entry.getValue();
            }
        }

        return country;
    }

    @Override
    public Map<String, Double> countriesAvgTemperature() {
        Map<String, Temperatures> temperaturesByCountry = temperaturesByCountry();
        Map<String, Double> countriesAvgTemperature = new HashMap<>(temperaturesByCountry.size());

        for (Entry<String, Temperatures> entry: temperaturesByCountry.entrySet()) {
            double temperatureSum = 0;

            for (Temperature temperature: entry.getValue())
                temperatureSum += temperature.getAverageTemperature();

            countriesAvgTemperature.put(entry.getKey(), temperatureSum / entry.getValue().size());
        }

        return countriesAvgTemperature;
    }

}