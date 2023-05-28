package ru.otus.protobuf.observer;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.generated.NumberFromInterval;

@Slf4j
@RequiredArgsConstructor
public class QueueStreamObserver implements StreamObserver<NumberFromInterval> {

    private final BlockingQueue<NumberFromInterval> queue;

    @Override
    public void onNext(NumberFromInterval numberFromInterval) {
        queue.offer(numberFromInterval);
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
