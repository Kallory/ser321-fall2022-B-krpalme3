import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class ClientThread extends Thread {
	private BufferedReader bufferedReader;

	public ClientThread(Socket socket) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public static void main(String[] args) throws IOException {
		String input = args[1];
		String[] setupValue = input.split(" ");
		for (int i = 0; i < setupValue.length; i++) {
			String[] address = setupValue[i].split(":");
			Socket socket = null;
			try {
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new ClientThread(socket).start();

			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Cannot connect, wrong input");
					System.out.println("Exiting: I know really user friendly");
					System.exit(0);
				}
			}
		}
	}

	public void askForInput(Socket s) throws Exception {
		try {

			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else {
					// we are sending the message to our server thread. this one is then responsible for sending it to listening peers
					sendMessage("{'username': '"+ "client" +"','message':'" + message + "'}", s);
				}
			}
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void sendMessage(String message, Socket s) {

	}

	public void run() {
		while (true) {
			try {
				JSONObject json = new JSONObject(bufferedReader.readLine());
				System.out.println("message recieved: ");
				System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
			} catch (Exception e) {
//				e.printStackTrace();
				interrupt();
				break;
			}
		}
	}

}
