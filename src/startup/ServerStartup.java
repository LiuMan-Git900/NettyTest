package startup;

import business.core.HumanWork;
import business.core.Node;
import network.server.ServerNetWork;


/**
 * 服务器启动器
 *  */
public class ServerStartup {
    public static Node currentNode;

    public static void main(String[] args)  {
        // 创建Node
        Node node = new Node();
        currentNode = node;
        // netty 通信组件的启动-- 通信线程的创造,这里的绑定是阻塞似的，所以我们要新启动线程
        ServerNetWork serverNetWork = new ServerNetWork(8080, node);
        // 创建HumanWork
        HumanWork humanWork = new HumanWork("HumanWork1", node);
        HumanWork humanWork2 = new HumanWork("HumanWork2", node);
        HumanWork humanWork3 = new HumanWork("HumanWork3", node);
        // 启动所有的work
        serverNetWork.start();
        humanWork.start();
        humanWork2.start();
        humanWork3.start();

        // 工作线程模型搭建的基础思想：netty搭建成功，创建humanobject对象，我们有多个humanObject对象，且每一个对象都需要心跳，所以需要将humanobject对象放到逻辑线程Work中去。
        // Work线程管理多个humanobject对象，且提供心跳功能。为了线程负载均衡，我们需要多个work线程，所以多个work线程也需要也该Node进行管理。
        // Work是线程提供心跳，Node不是线程，只是负责管理work线程。
        // 工作线程提供两个功能：管理多个humanobject对象，未humanobject对象提供心跳。
    }
}
