package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main (String[] args) throws IOException {
        ServerSocket serv = null;
        Socket sock = null;
        OutputStream out = null;
        ObjectOutputStream os = null;
        int port = 0;
        String name = "";
        int counter = 0;
        Boolean nameGotten = false;
        Boolean numberGotten = false;

        try {
            if (args[0] != null) port = Integer.parseInt(args[0]);
            else port = 8080;
            serv = new ServerSocket(port);

            while (true) {
                try {
                    System.out.println("Server waiting for a connection on port " + port);
                    sock = serv.accept(); // blocking wait
                    out = sock.getOutputStream();
                    InputStream in = sock.getInputStream();
                    System.out.println("Server connected to client");
                } catch (Exception e) {
                    System.out.println("Client disconnected");
                }
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Port must be an integer");
            System.exit(2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serv != null) serv.close();
        }

    }
}
