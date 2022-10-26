package com.rit.swen755.assginment3;

public class Consumer implements Runnable {
    private String name;
    private Resource resource;
    private int requestNumberOfItems;
    private long startedTime;

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    private boolean isScheduled = false;
    public Consumer(String name, Resource resource, int requestNumberOfItems, long startedTime) {
        this.name = name;
        this.resource = resource;
        this.requestNumberOfItems = requestNumberOfItems;
        this.startedTime = startedTime;
        this.isScheduled = false;
        System.out.println(this.name + " entered the store to request " + this.requestNumberOfItems + " items.");
    }

    public Consumer(String name, Resource resource, int requestNumberOfItems) {
        this.name = name;
        this.resource = resource;
        this.requestNumberOfItems = requestNumberOfItems;
        this.isScheduled = false;
        System.out.println(this.name + " entered the store to request " + this.requestNumberOfItems + " items.");
    }

    @Override
    public void run() {
        try {
            Integer data = resource.get();
            while (true) {
                Thread.sleep(2000);
                if (data!=null) {
                    System.out.println(this.name + " processed data from resource: " + data);
                    this.requestNumberOfItems--;
                    if (this.requestNumberOfItems == 0)
                        break;
                } else {
//                    System.out.println(this.name + " is waiting... ");
                }
                data = resource.get();
            }

            System.out.println("Consumer " + this.name + " finished its job; terminating.");
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isStarving() {
        return (System.currentTimeMillis() - this.startedTime) > 10000;
    }

    public String getName() {
        return name;
    }

    public int getRequestNumberOfItems() {
        return requestNumberOfItems;
    }
}
