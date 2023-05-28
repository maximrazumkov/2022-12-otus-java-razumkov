package ru.otus.protobuf.service;

import static ru.otus.protobuf.util.Utils.sleep;

import io.grpc.stub.StreamObserver;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.generated.Interval;
import ru.otus.protobuf.generated.NumberFromInterval;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;

@Slf4j
public class RemoteDBServiceImpl extends RemoteDBServiceGrpc.RemoteDBServiceImplBase {

    @Override
    public void getNumbersFromInterval(Interval interval, StreamObserver<NumberFromInterval> numberFromInterval) {
        int start = interval.getFirstValue();
        int finish = interval.getLastValue() + 1;
        IntStream.range(start, finish)
            .forEach(number -> onNextNumber(numberFromInterval, number));
        numberFromInterval.onCompleted();
        log.info("Completed");
    }

    private void onNextNumber(StreamObserver<NumberFromInterval> numberFromInterval, int i) {
        sleep(2);
        numberFromInterval.onNext(getNumberFromInterval(i));
        log.info("send number: " + i);
    }

    private NumberFromInterval getNumberFromInterval(int i) {
        return NumberFromInterval.newBuilder()
            .setValue(i)
            .build();
    }
}
