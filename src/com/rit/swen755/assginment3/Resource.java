package com.rit.swen755.assginment3;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Resource {
    LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue();
    public Boolean continueProducing = Boolean.TRUE;

    public void put(Integer data) throws InterruptedException {
        this.queue.put(data);
    }

    public Integer get() throws InterruptedException
    {
        return this.queue.poll(1, TimeUnit.SECONDS);
    }
}
