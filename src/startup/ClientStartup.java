package startup;

import network.client.ClientNetWork;
/**
 * 客户端启动器
 *  */
public class ClientStartup {
    public static void main(String[] args) {
            ClientNetWork client = new ClientNetWork();
            client.Connection(8080);
    }
}
