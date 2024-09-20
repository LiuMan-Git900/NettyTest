package network.server;

public class ServerStartup {
    public static void main(String[] args)  {
        FirstServer server = new FirstServer();
        server.bind(8080);
    }
}
