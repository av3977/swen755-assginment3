package com.rit.swen755.assginment3;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Producer implements Runnable {

    private Resource resource;
    private String name;

    private int keepTrackOfProducedNumbers;
    public Producer(Resource resource) {
        this.resource = resource;
        this.keepTrackOfProducedNumbers = 0;
    }

    public Producer(Resource resource, String name) {
        this.resource = resource;
        this.name = name;
        this.keepTrackOfProducedNumbers = 0;
    }

    public boolean keepCheckOnQueue() {
         return resource.queue.size() == 0;
    }
    @Override
    public void run() {
        try {
            while (true) {
                if (this.resource.continueProducing) {
                    for (Integer i = 1; i <= 3; i++) {
                        System.out.println("Produced Queue: " + resource.getQueue());
                        System.out.println(this.name + " produced: " + (this.keepTrackOfProducedNumbers));
                        Thread.sleep(1000);
                        resource.put(this.keepTrackOfProducedNumbers++);
                    }
                    this.resource.continueProducing = Boolean.FALSE;
                } else {
                    if (keepCheckOnQueue())
                        resource.continueProducing = Boolean.TRUE;
                }
                Thread.sleep(2000);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
