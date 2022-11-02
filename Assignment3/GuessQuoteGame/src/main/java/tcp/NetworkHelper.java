package tcp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkHelper {

    public NetworkHelper(OutputStream out, InputStream in, Socket sock, int port, String host) {
        this.out = out;
        this.in = in;
        this.sock = sock;
        this.port = port;
        this.host = host;
    }

    public NetworkHelper() {}

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    OutputStream out;
    InputStream in;
    Socket sock;
    int port = 0;
    String host = "";


}
