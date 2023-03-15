package ru.otus.dataprocessor;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedHashMap;
import ru.otus.model.Measurement;

import java.util.List;
import java.util.Map;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        return data.stream()
            .sorted(comparing(Measurement::getName))
            .collect(toMap(Measurement::getName, Measurement::getValue, Double::sum, LinkedHashMap::new));
    }
}
