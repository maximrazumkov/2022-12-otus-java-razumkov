package ru.otus.monitor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LadderGame {
    private static final Logger logger = LoggerFactory.getLogger(LadderGame.class);
    private BlockingQueue<String> queue;
    private final int count;

    public LadderGame(BlockingQueue<String> queue, int count) {
        this.queue = queue;
        this.count = count;
    }

    private synchronized void run(int step) {
        int approach = step;
        while(step != 0) {
            try {
                String currentThreadName = Thread.currentThread().getName();
                while (!queue.peek().equals(currentThreadName)) {
                    this.wait();
                }
                String poll = queue.take();
                queue.put(poll);
                logger.info(String.valueOf(step));
                if (approach < count) {
                    ++step;
                    ++approach;
                } else {
                    --step;
                }
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        queue.add("Поток 1");
        queue.add("Поток 2");
        LadderGame ladderGame = new LadderGame(queue, 10);
        Thread thread1 = new Thread(() -> ladderGame.run(1));
        thread1.setName("Поток 1");

        Thread thread2 = new Thread(() -> ladderGame.run(1));
        thread2.setName("Поток 2");

        thread1.start();
        thread2.start();
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
