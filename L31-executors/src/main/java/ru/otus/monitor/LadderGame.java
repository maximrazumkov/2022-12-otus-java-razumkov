package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LadderGame {
    private static final Logger logger = LoggerFactory.getLogger(LadderGame.class);
    private String last;
    private final int count;

    public LadderGame(String last, int count) {
        this.last = last;
        this.count = count;
    }

    private synchronized void run(int step) {
        int approach = step;
        while(step != 0) {
            try {
                while (last.equals(Thread.currentThread().getName())) {
                    this.wait();
                }
                logger.info(String.valueOf(step));
                if (approach < count) {
                    ++step;
                    ++approach;
                } else {
                    --step;
                }
                last = Thread.currentThread().getName();
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        LadderGame ladderGame = new LadderGame("Поток 2", 10);
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
