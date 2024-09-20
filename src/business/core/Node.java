package business.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 管理HumanWork
 * 项目的负载均衡策略是：work.id /
 * */
public class Node {

    private Map<String, HumanWork> humanWorks = new HashMap<>();

    public void addHumanWork(HumanWork work) {
        humanWorks.put(work.toString(), work);
    }

    public HumanWork getHumanWork(String id) {
        return humanWorks.get(id);
    }


}
