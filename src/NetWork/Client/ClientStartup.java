package NetWork.Client;
public class ClientStartup {
    public static void main(String[] args) {
            FirstClient client = new FirstClient();
            client.Connection(8080);
    }
}
