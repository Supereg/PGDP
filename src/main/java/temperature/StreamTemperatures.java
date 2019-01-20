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
        return parallelStream().count();
    }

    @Override
    public List<Date> dates() {
        return parallelStream().map(Temperature::getDate).sorted().distinct().collect(Collectors.toList());
    }

    @Override
    public Set<String> cities() {
        return parallelStream().map(Temperature::getCity).collect(Collectors.toSet());
    }

    @Override
    public Set<String> countries() {
        return parallelStream().map(Temperature::getCountry).collect(Collectors.toSet());
    }

    @Override
    public Map<String, Temperatures> temperaturesByCountry() {
        return parallelStream()
                .collect(Collectors.toMap(Temperature::getCountry, temperature -> new ArrayList<>(List.of(temperature)), (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                })).entrySet().parallelStream().collect(Collectors.toMap(Entry::getKey, entry -> new StreamTemperatures(entry.getValue())));
    }

    @Override
    public String coldestCountryAbs() {
        return parallelStream()
                .min(Comparator.comparingDouble(Temperature::getAverageTemperature))
                .map(Temperature::getCountry).orElse(null);
    }

    @Override
    public String hottestCountryAbs() {
        return parallelStream()
                .max(Comparator.comparingDouble(Temperature::getAverageTemperature))
                .map(Temperature::getCountry).orElse(null);
    }

    @Override
    public String coldestCountryAvg() {
        return countriesAvgTemperature().entrySet().parallelStream()
                .min(Comparator.comparingDouble(Entry::getValue))
                .map(Entry::getKey).orElse(null);
    }

    @Override
    public String hottestCountryAvg() {
        return countriesAvgTemperature().entrySet().parallelStream()
                .max(Comparator.comparingDouble(Entry::getValue))
                .map(Entry::getKey).orElse(null);
    }

    @Override
    public Map<String, Double> countriesAvgTemperature() {
        return temperaturesByCountry().entrySet().parallelStream().collect(Collectors.toMap(
                Entry::getKey,
                temperatures -> temperatures.getValue().parallelStream()
                        .mapToDouble(Temperature::getAverageTemperature).average().orElse(0D)
        ));
    }

    public Map<String, Double> avgTemperatureDeltaPerYearPerCountry() {
        Map<String, Double> avgTempDeltaPerYear = temperaturesByCountry().entrySet().parallelStream().collect(
                Collectors.toMap(Entry::getKey, tempByCountryEntry -> {
                    // mapping year to total temperature sum
                    @SuppressWarnings("deprecation")
                    Map<Integer, Integer> yearToTempSum = tempByCountryEntry.getValue().parallelStream().collect(
                            Collectors.toMap(temperature -> temperature.getDate().getYear(), temperature -> 1, Integer::sum)
                    );
                    // mapping year to average temp
                    @SuppressWarnings("deprecation")
                    Map<Integer, Double> yearToAvgTemp = tempByCountryEntry.getValue().parallelStream()
                            .collect(Collectors.toMap(temperature -> temperature.getDate().getYear(), Temperature::getAverageTemperature, Double::sum))
                            .entrySet().parallelStream()
                            .filter(entry -> entry.getKey() >= 0) // filter >= year 1900
                            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue() / yearToTempSum.get(entry.getKey())));


                    List<Double> yearlyTempDeltas = yearToAvgTemp.entrySet().parallelStream()
                            .map(yearToAvgEntry -> {
                                Double avgTempNextYear = yearToAvgTemp.get(yearToAvgEntry.getKey() + 1);
                                return avgTempNextYear != null? avgTempNextYear - yearToAvgEntry.getValue(): null;
                            }).filter(Objects::nonNull).collect(Collectors.toList());

                    // sum all temperature deltas and divide by size
                    return yearlyTempDeltas.parallelStream().mapToDouble(d -> d).average().orElse(0D);
                })
        );

        // adding global key
        Double globallyAvg = avgTempDeltaPerYear.values().parallelStream().mapToDouble(d -> d).average().orElse(0D);
        avgTempDeltaPerYear.put("Globally", globallyAvg);

        return avgTempDeltaPerYear;
    }

}