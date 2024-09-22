package business.core;

import network.server.ServerNetWork;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 管理HumanWork
 * 项目的负载均衡策略是：work.id /
 * */
public class Node {
    /** 网络通信线程 */
    private ServerNetWork netWork;
    /** 玩家数据处理线程 */
    private Map<String, HumanWork> humanWorks = new ConcurrentHashMap<>();
    public void addNetWork(ServerNetWork work) {
        this.netWork = work;
    }
    public void addHumanWork(HumanWork work) {
        humanWorks.put(work.getId(), work);
    }

    public HumanWork getHumanWork(String id) {
        return humanWorks.get(id);
    }

    public Collection<HumanWork> getAllHumanWork() {
        return humanWorks.values();
    }


}
