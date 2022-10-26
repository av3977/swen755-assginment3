package com.rit.swen755.assginment3;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Resource {
    LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue();
    public Boolean continueProducing = Boolean.TRUE;

    public Resource() {
        // initialize
        this.queue.add(1);
    }

    public void put(Integer data) throws InterruptedException {
        this.queue.put(data);
    }
    public int put() throws InterruptedException {
        this.queue.put(this.queue.size()+1);
        return this.queue.size();
    }

    public Integer get() throws InterruptedException {
        return this.queue.poll(1, TimeUnit.SECONDS);
    }

    public Integer peek() throws InterruptedException {
        return this.queue.peek();
    }

    public LinkedBlockingQueue<Integer> getQueue() {
        return queue;
    }
}
