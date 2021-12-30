package com.buuz135.findme.client;

import dev.architectury.event.events.client.ClientTickEvent;

import java.util.Iterator;
import java.util.LinkedList;

public class ClientTickHandler {
    private static final Lock lock = new Lock();
    private static boolean ticking = false;

    private static LinkedList<QueueEntry> queue = new LinkedList<>();
    private static LinkedList<WaitEntry> waitlist = new LinkedList<>();

    public static void clientTick() {

        ticking = true;
        synchronized (lock) {
            ticking = true;
            Iterator<QueueEntry> iterator = queue.iterator();
            while (iterator.hasNext()) {
                QueueEntry entry = iterator.next();
                if (entry.counter-- <= 0) {
                    entry.runnable.run();
                    iterator.remove();
                }
            }
            ticking = false;
            for (WaitEntry entry : waitlist) {
                queue.addLast(new QueueEntry(entry.runnable, entry.delay));
            }
        }
        waitlist.clear();
    }

    public static void addRunnable(Runnable runnable) {
        addRunnable(runnable, 0);
    }

    public static void addRunnable(Runnable runnable, int delay) {
        synchronized (lock) {
            if (ticking) {
                waitlist.addLast(new WaitEntry(runnable, delay));
            } else {
                queue.addLast(new QueueEntry(runnable, delay));
            }
        }
    }

    private static class WaitEntry {
        public Runnable runnable;
        public int delay;

        public WaitEntry(Runnable runnable, int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }

    private static class QueueEntry {
        public Runnable runnable;
        public int counter;

        public QueueEntry(Runnable runnable, int counter) {
            this.runnable = runnable;
            this.counter = counter;
        }
    }

    private static class Lock {
        private Lock() {
        }
    }
}