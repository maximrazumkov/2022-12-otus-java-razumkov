package ru.otus.protobuf;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.otus.protobuf.service.RemoteDBServiceImpl;

import java.io.IOException;

@Slf4j
public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        new GRPCServer().run(SERVER_PORT);
    }

    public void run(int serverPort) throws IOException, InterruptedException {
        var remoteDBService = new RemoteDBServiceImpl();
        var server = getServer(serverPort, remoteDBService);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        log.info("server waiting for client connections...");
        server.awaitTermination();
    }

    private Server getServer(int serverPort, RemoteDBServiceImpl remoteDBService) {
        return ServerBuilder
            .forPort(serverPort)
            .addService(remoteDBService)
            .build();
    }
}
