package com.jemnetworks.strongholdconfig.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CallableThreadGroup extends AbstractCollection<Thread> implements Runnable {
    protected ThreadGroup group = null;
    protected List<Thread> threads = new ArrayList<>();
    protected String nameFormat = null;

    public CallableThreadGroup() {
    }

    public CallableThreadGroup(ThreadGroup group) {
        this.group = group;
        enumerateGroup();
    }

    public CallableThreadGroup(Thread thread) {
        group = thread.getThreadGroup();
        enumerateGroup();
    }

    public CallableThreadGroup(Thread thread, boolean enumerateGroup) {
        group = thread.getThreadGroup();
        if (enumerateGroup) {
            enumerateGroup();
        } else {
            threads.add(thread);
        }
    }

    public CallableThreadGroup(Collection<Thread> threads) {
        addAll(threads);
    }

    public CallableThreadGroup(Thread[] threads) {
        Arrays.stream(threads).forEach(this::add);
    }

    private void enumerateGroup() {
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        Arrays.stream(threads).forEach(this.threads::add);
    }

    public ThreadGroup getThreadGroup() {
        return group;
    }

    public int size() {
        return threads.size();
    }

    public Iterator<Thread> iterator() {
        return threads.iterator();
    }

    public boolean add(Thread thread) {
        if (thread.getThreadGroup() != group) {
            group = null;
        }
        return threads.add(thread);
    }

    public boolean remove(Thread thread) {
        return threads.remove(thread);
    }

    public void clear() {
        threads.clear();
    }

    public void run() {
        threads.forEach(t -> t.run());
    }

    public void interrupt() {
        threads.forEach(t -> t.interrupt());
    }

    public void join() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    public void join(long millis) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(millis);
        }
    }

    public void join(long millis, int nanos) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(millis, nanos);
        }
    }

    public void setDaemon(boolean on) {
        threads.forEach(t -> t.setDaemon(on));
    }

    public void setName(String name) {
        threads.forEach(t -> t.setName(name));
    }

    public void setNameFormat(String format) {
        nameFormat = format;
        if (nameFormat != null) {
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                thread.setName(String.format(format, i));
            }
        }
    }

    public void setPriority(int newPriority) {
        threads.forEach(t -> t.setPriority(newPriority));
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh) {
        threads.forEach(t -> t.setUncaughtExceptionHandler(eh));
    }

    public void start() {
        threads.forEach(t -> t.start());
    }

    public Thread newThreadInGroup(Runnable target) {
        Thread thread = nameFormat == null
                        ? new Thread(group, target)
                        : new Thread(group, target, String.format(nameFormat, threads.size()));
        add(thread);
        return thread;
    }

    public Thread newThreadInGroup(Runnable target, String name) {
        Thread thread = new Thread(group, target, name);
        add(thread);
        return thread;
    }

    public Thread newThreadInGroup(Runnable target, String name, long stackSize) {
        Thread thread = new Thread(group, target, name, stackSize);
        add(thread);
        return thread;
    }

    public Thread newThreadInGroup(String name) {
        Thread thread = new Thread(group, name);
        add(thread);
        return thread;
    }
}
