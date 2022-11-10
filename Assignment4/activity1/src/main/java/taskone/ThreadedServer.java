package taskone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThreadedServer {

    public static void main(String[] args)  {
        StringList strings = new StringList();
        int port;
        ClientManager clientManager;

        if (args.length != 1) {
            // gradle runServer -Pport=8080 -q --console=plain
            //TODO set default of 8080 and localhost instead of this message
            System.out.println("Usage: gradle runServer -Pport=8080 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server Started...");
            while (true) {

                System.out.println("Accepting a Request...");
                Socket sock = server.accept();
                clientManager = new ClientManager(sock, strings);
                Thread t = new Thread(clientManager);
                t.start();

                try {
                    //TODO: Close socket of individual client, utilize ClientManager.remove(), reduce count
                    System.out.println("close socket of client ");
//                    sock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static final List<Integer> clients = new ArrayList<>();

    public synchronized static void removeClient(int clientID) {
        clients.remove(clientID);
    }

    public synchronized static void addClient(int clientID) {
        clients.add(clientID);
    }

    public static synchronized int getClientCount() {
        return clients.size();
    }

}
