package taskone;

import java.net.Socket;

public class ClientManager implements Runnable {
    private final Socket socket;
    private final StringList strings;
    private final int clientID;
    private final boolean isConnected;
    private final int port;

    Performer performer;

    public ClientManager(Socket socket, StringList strings) {
        this.socket = socket;
        this.strings = strings;
        this.clientID = ThreadedServer.getClientCount();
        ThreadedServer.addClient(clientID);
        this.isConnected = true;
        this.port = socket.getLocalPort();
        performer = new Performer(socket, strings);
    }


    @Override
    public void run() {
        System.out.println("New client has connected on port: " + this.port);
        System.out.println("The new count is " + ThreadedServer.getClientCount());
        performer.doPerform();
    }
}
