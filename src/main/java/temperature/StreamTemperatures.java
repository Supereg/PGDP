package temperature;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class StreamTemperatures extends Temperatures {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isEmpty()) {
            System.out.println("Required args[0] = url/filepath!");
            return;
        }

        StreamTemperatures temperatures;
        try {
            temperatures = new StreamTemperatures(new URL(args[0]));
        } catch (MalformedURLException ignored) {
            // it's not a url so probably a File
            File file = new File(args[0]);
            if (!file.exists()) {
                System.out.println("File '" + file.getName()+ "' does not exist!");
                return;
            }

            temperatures = new StreamTemperatures(file);
        }

        temperatures.printSummary();

        System.out.println();
        System.out.println();

        System.out.println("Country\t\t\tAverage temperature delta per year");
        System.out.println("--------------------------------------------------");
        temperatures.avgTemperatureDeltaPerYearPerCountry().forEach((key, value) -> {
            // 4 tabs should be enough for country length
            int tabs = 4 - key.length() / 4;
            System.out.println(key + "\t".repeat(tabs) + value);
        });
    }

    public StreamTemperatures(Iterable<Temperature> temperatures) {
        super(temperatures);
    }

    public StreamTemperatures(File csv) {
        super(csv);
    }

    public StreamTemperatures(URL url) {
        super(url);
    }

    @Override
    public long size() {
        return stream().count();
    }

    @Override
    public List<Date> dates() {
        return stream().map(Temperature::getDate).sorted().distinct().collect(Collectors.toList());
    }

    @Override
    public Set<String> cities() {
        return stream().map(Temperature::getCity).collect(Collectors.toSet());
    }

    @Override
    public Set<String> countries() {
        return stream().map(Temperature::getCountry).collect(Collectors.toSet());
    }

    @Override
    public Map<String, Temperatures> temperaturesByCountry() {
        return stream().collect(Collectors.toMap(Temperature::getCountry, temperature -> new HashSet<>(List.of(temperature)), (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        })).entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> new StreamTemperatures(entry.getValue())));
    }

    @Override
    public String coldestCountryAbs() {
        return stream().min(Comparator.comparingDouble(Temperature::getAverageTemperature)).map(Temperature::getCountry).orElse(null);
    }

    @Override
    public String hottestCountryAbs() {
        return stream().max(Comparator.comparingDouble(Temperature::getAverageTemperature)).map(Temperature::getCountry).orElse(null);
    }

    @Override
    public String coldestCountryAvg() {
        return countriesAvgTemperature().entrySet().stream().min(Comparator.comparingDouble(Entry::getValue)).map(Entry::getKey).orElse(null);
    }

    @Override
    public String hottestCountryAvg() {
        return countriesAvgTemperature().entrySet().stream().max(Comparator.comparingDouble(Entry::getValue)).map(Entry::getKey).orElse(null);
    }

    @Override
    public Map<String, Double> countriesAvgTemperature() {
        return temperaturesByCountry().entrySet().stream().collect(Collectors.toMap(
                Entry::getKey,
                temperatures -> temperatures.getValue().stream().map(Temperature::getAverageTemperature).reduce(0D, Double::sum)
                        / temperatures.getValue().size()
        ));
    }

    public Map<String, Double> avgTemperatureDeltaPerYearPerCountry() {
        Map<String, Double> avgTempDeltaPerYear = temperaturesByCountry().entrySet().stream().collect(
                Collectors.toMap(Entry::getKey, tempByCountryEntry -> {
                    // mapping year to total temperature sum
                    @SuppressWarnings("deprecation")
                    Map<Integer, Integer> yearToTempSum = tempByCountryEntry.getValue().stream().collect(
                            Collectors.toMap(temperature -> temperature.getDate().getYear(), temperature -> 1, Integer::sum)
                    );
                    // mapping year to average temp
                    @SuppressWarnings("deprecation")
                    Map<Integer, Double> yearToAvgTemp = tempByCountryEntry.getValue().stream().collect(
                            Collectors.toMap(temperature -> temperature.getDate().getYear(), Temperature::getAverageTemperature, Double::sum)
                    ).entrySet().stream().filter(entry -> entry.getKey() >= 0).collect(
                            Collectors.toMap(Entry::getKey, entry -> entry.getValue() / yearToTempSum.get(entry.getKey()))
                    );

                    // list of all temperature deltas between two years
                    List<Double> yearlyTempDeltas = yearToAvgTemp.entrySet().stream()
                            .map(yearToAvgEntry -> {
                                Double avgTempNextYear = yearToAvgTemp.get(yearToAvgEntry.getKey() + 1);
                                return avgTempNextYear != null? avgTempNextYear - yearToAvgEntry.getValue(): null;
                            }).filter(Objects::nonNull).collect(Collectors.toList());

                    // sum all temperature deltas and divide by size
                    return yearlyTempDeltas.stream().reduce(0D, Double::sum) / yearlyTempDeltas.size();
                })
        );

        // adding global key
        Double globallyAvg = avgTempDeltaPerYear.values().stream().reduce(0D, Double::sum) / avgTempDeltaPerYear.size();
        avgTempDeltaPerYear.put("Globally", globallyAvg);

        return avgTempDeltaPerYear;
    }

}