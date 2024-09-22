package business.core;

import business.service.server.SHumanObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HumanWork implements Runnable {
    /** id */
    private String id;
    /** 归属Node */
    private Node owner;
    /** 管理的所有humanObject */
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
        Thread thread = new Thread(this);
        thread.setName(id);
        thread.start();
    }

    private void pulse() {
        for (SHumanObject humanObject : humanObjectMap.values()) {
            humanObject.pulse();
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public void run() {
        while (true) {
            pulse();
        }
    }
}
