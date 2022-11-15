package taskone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadPoolServer {
    public static void main(String[] args)  {
        StringList strings = new StringList();
        int port;
        int limit = 30;

        if (args.length != 2) {
            // gradle runServer -Pport=8080 -q --console=plain
            System.out.println("Usage: gradle runTask3 -Pport=8080 -Plimit=limitNumber -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
            limit = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port], limit, must be an integer");
            System.exit(2);
        }
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server Started...");
            Executor pool = Executors.newFixedThreadPool(limit);
            while (true) {

                System.out.println("Accepting a Request...");
                Socket sock = server.accept();
                pool.execute(new ClientManager(sock, strings));

                try {
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
