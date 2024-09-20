package business.core;

import business.service.server.SHumanObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HumanWork implements Runnable {
    private String id;
    private Node owner;
    private Map<Long, SHumanObject> humanObjectMap = new ConcurrentHashMap<>();

    public HumanWork(String id, Node owner) {
        this.id = id;
        this.owner = owner;
        this.owner.addHumanWork(this);
    }

    public void addHumanObject(SHumanObject humanObject) {
        humanObjectMap.put(humanObject.getId(), humanObject);
    }

    public void start() {
        new Thread(this).start();
    }

    private void pulse() {
        for (SHumanObject humanObject : humanObjectMap.values()) {
            humanObject.pulse();
        }
    }

    @Override
    public void run() {
        while (true) {
            pulse();
        }
    }
}
