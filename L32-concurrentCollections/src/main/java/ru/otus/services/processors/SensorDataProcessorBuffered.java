package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final Queue<SensorData> sensorDataPriorityBlockingQueue;
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.sensorDataPriorityBlockingQueue = new PriorityBlockingQueue<>(bufferSize, Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        sensorDataPriorityBlockingQueue.offer(data);
        if (sensorDataPriorityBlockingQueue.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        if (atomicBoolean.getAndSet(false)) {
            try {
                if (!sensorDataPriorityBlockingQueue.isEmpty()) {
                    SensorData sensorData;
                    List<SensorData> sensorDataList = new ArrayList<>();
                    while ((sensorData = sensorDataPriorityBlockingQueue.poll()) != null) {
                        sensorDataList.add(sensorData);
                    }
                    writer.writeBufferedData(sensorDataList);
                }
                atomicBoolean.set(true);
            } catch (Exception e) {
                log.error("Ошибка в процессе записи буфера", e);
            }
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
