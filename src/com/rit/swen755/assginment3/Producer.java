package com.rit.swen755.assginment3;

public class Producer implements Runnable {

    private Resource resource;

    private int keepTrackOfProducedNumbers;
    public Producer(Resource resource) {
        this.resource = resource;
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
                    for (Integer i = 1; i <= 5; i++) {
                        System.out.println("Producer produced: " + this.keepTrackOfProducedNumbers++);
                        Thread.sleep(3000);
                        resource.put(i);
                    }
                    this.resource.continueProducing = Boolean.FALSE;
                }

                Thread.sleep(1000);
                if (keepCheckOnQueue())
                    this.resource.continueProducing = Boolean.TRUE;
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
