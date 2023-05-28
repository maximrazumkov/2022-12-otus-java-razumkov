package ru.otus.protobuf;

import static java.util.Objects.isNull;
import static ru.otus.protobuf.util.Utils.sleep;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.generated.NumberFromInterval;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;
import ru.otus.protobuf.generated.Interval;
import ru.otus.protobuf.observer.QueueStreamObserver;

@Slf4j
public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    public static void main(String[] args) {
        new GRPCClient().run(SERVER_HOST, SERVER_PORT);
    }

    public void run(String serverHost, int serverPort) {
        var channel = getChannel(serverHost, serverPort);
        var queue = new ArrayBlockingQueue<NumberFromInterval>(1);
        QueueStreamObserver queueStreamObserver = new QueueStreamObserver(queue);
        Interval interval = getInterval(0, 30);
        var newStub = RemoteDBServiceGrpc.newStub(channel);
        newStub.getNumbersFromInterval(interval, queueStreamObserver);
        int currentValue = 0;
        for (int i = 0; i <= 50; ++i) {
            sleep(1);
            NumberFromInterval numberFromInterval = queue.poll();
            int lastNumFromServer = isNull(numberFromInterval) ? 0 : numberFromInterval.getValue();
            log.info("number from server: " + lastNumFromServer);
            currentValue += lastNumFromServer + 1;
            log.info("currentValue: " + currentValue);
        }
        channel.shutdown();
    }

    private ManagedChannel getChannel(String serverHost, int serverPort) {
        return ManagedChannelBuilder.forAddress(serverHost, serverPort)
            .usePlaintext()
            .build();
    }

    private Interval getInterval(int start, int finish) {
        return Interval.newBuilder()
            .setFirstValue(start)
            .setLastValue(finish)
            .build();
    }
}
