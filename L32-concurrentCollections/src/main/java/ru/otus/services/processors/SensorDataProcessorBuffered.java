package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityBlockingQueue<SensorData> sensorDataPriorityBlockingQueue;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.sensorDataPriorityBlockingQueue = new PriorityBlockingQueue<>(bufferSize, Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        sensorDataPriorityBlockingQueue.put(data);
        if (sensorDataPriorityBlockingQueue.size() >= bufferSize) {
            flush();
        }
    }

    public synchronized void flush() {
        try {
            if (!sensorDataPriorityBlockingQueue.isEmpty()) {
                List<SensorData> sensorDataList = new ArrayList<>();
                SensorData sensorData;
                while ((sensorData = sensorDataPriorityBlockingQueue.poll()) != null) {
                    sensorDataList.add(sensorData);
                }
                writer.writeBufferedData(sensorDataList);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }


    @Override
    public void onProcessingEnd() {
        flush();
    }
}
