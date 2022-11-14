package utils;

import server.Game;
import server.SockBaseServer;

import java.net.Socket;

public class ClientManager implements Runnable {
    private final Socket socket;
    private final int clientID;
    private final boolean isConnected;
    private final int port;

    Game game;

    public ClientManager(Socket socket) {
        this.socket = socket;
        this.clientID = SockBaseServer.getClientCount();
        SockBaseServer.addClient(clientID);
        this.isConnected = true;
        this.port = socket.getLocalPort();
    }


    @Override
    public void run() {
        System.out.println("New client has connected on port: " + this.port);
        System.out.println("The new count is " + SockBaseServer.getClientCount());
    }
}