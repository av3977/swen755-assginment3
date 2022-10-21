package com.rit.swen755.assginment3;

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
//                        System.out.println(this.name + " produced: " + (resource.peek()+1));
                        Thread.sleep(2000);
                        resource.put(this.keepTrackOfProducedNumbers++);
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
