package ru.otus.protobuf.observer;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.generated.NumberFromInterval;

@Slf4j
@RequiredArgsConstructor
public class NumberStreamObserver implements StreamObserver<NumberFromInterval> {

    private final AtomicInteger value;

    @Override
    public void onNext(NumberFromInterval numberFromInterval) {
        value.set(numberFromInterval.getValue());
    }

    @Override
    public void onError(Throwable t) {
        log.error(String.valueOf(t));
    }

    @Override
    public void onCompleted() {
        log.info("Completed");
    }
}
