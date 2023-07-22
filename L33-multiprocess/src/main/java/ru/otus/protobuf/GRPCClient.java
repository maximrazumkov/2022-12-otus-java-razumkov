package ru.otus.protobuf;

import static ru.otus.protobuf.util.Utils.sleep;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.generated.RemoteDBServiceGrpc;
import ru.otus.protobuf.generated.Interval;
import ru.otus.protobuf.observer.NumberStreamObserver;

@Slf4j
public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    public static void main(String[] args) {
        new GRPCClient().run(SERVER_HOST, SERVER_PORT);
    }

    public void run(String serverHost, int serverPort) {
        var channel = getChannel(serverHost, serverPort);
        var numberFromServer = new AtomicInteger(0);
        NumberStreamObserver queueStreamObserver = new NumberStreamObserver(numberFromServer);
        Interval interval = getInterval(0, 30);
        var newStub = RemoteDBServiceGrpc.newStub(channel);
        newStub.getNumbersFromInterval(interval, queueStreamObserver);
        int currentValue = 0;
        for (int i = 0; i <= 50; ++i) {
            sleep(1);
            int lastNumFromServer = numberFromServer.getAndSet(0);
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
