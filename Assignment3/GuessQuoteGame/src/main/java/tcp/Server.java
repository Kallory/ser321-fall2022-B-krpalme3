package tcp;

import org.json.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Scanner;

public class Server {
    static File userNameFile = null;
    static String fileNamePath = "usernameFile.json";
    static FileWriter fileWriter = null;
    /* JSON Request
        { "header": {"type": "sendName", "getLeaderboard", "startGame"},
          "body": {"data": {"username": <String>, "guess": <String>}}
        }

        JSON Response
        {"ok": <bool>,
         "value": <String>,
         "error": <String>
        }
     */
    public static JSONObject getName(String name) {
        JSONObject json = new JSONObject();
        json.put("header", "String");
        json.put("type", "msg");
        json.put("data", "data, " + name);
        return json;
    }

    public static void createFile() {
        try {
            userNameFile = new File(fileNamePath);

            if (userNameFile.createNewFile()) {
                System.out.println("File created: " + userNameFile.getName());
                fileWriter = new FileWriter(userNameFile, true);
                JSONObject json = new JSONObject();
                json.put("Name", "test");
                fileWriter.write(json.toString(1));
                fileWriter.flush();
//                fileWriter.close();
            } else {
                System.out.println("File already exists");
                fileWriter = new FileWriter(userNameFile, true);
            }
        } catch (IOException e) {
            System.out.println("error creating file");
        }
    }


    public static void main (String[] args) throws IOException {
        ServerSocket serv = null;
        Socket sock = null;
        OutputStream out = null;
        ObjectOutputStream os = null;
        int port = 0;
        String name = "";
        int counter = 0;
        Boolean nameFlag = false;
        int bufLen = 1024;

        JSONObject json = new JSONObject();
        try {
            if (args[0] != null) port = Integer.parseInt(args[0]);
            else port = 8080;
            serv = new ServerSocket(port);

            while (true) {
                try {
                    System.out.println("Server waiting for a connection on port " + port);
                    sock = serv.accept(); // blocking wait
                    out = sock.getOutputStream();
                    InputStream input = sock.getInputStream();
                    System.out.println("Server connected to client");
                    PrintWriter writer = new PrintWriter(out, true);

                    createFile();

                    writer.println("Please enter your name: ");
                    byte clientInput[] = new byte[bufLen];
                    int numBytesReceived = input.read(clientInput, 0, bufLen);

                    System.out.println();
                    System.out.println("name gotten, before while loop");
                    name = new String(clientInput, 0, numBytesReceived);
                    name = name.trim();

                    final Scanner scanner = new Scanner(userNameFile);
                    while (scanner.hasNext()) {
                        System.out.println("Inside scanner while loop");
                        String lineFromFile = scanner.nextLine();
                        lineFromFile = lineFromFile.replace("\\n", "").replace("\\r", "").replace("\"", "");
                        System.out.println(name.trim());

                        System.out.println("Line from file: " + lineFromFile);
                        System.out.print("Name is: " + name + " and contains = ");
                        System.out.println((lineFromFile.contains(name)));
                        if (!(lineFromFile.contains(name))) {
                            json.put("Name", name);
                            System.out.println("Hello, " + json.get("Name"));
                            writer.println("Hello, " + name);
                        } else {
                            System.out.println("Welcome back, " + name);
                            writer.println("Welcome back, " + name);
                        }
                    }


                    try {
                        fileWriter.write(json.toString(1));
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("JSON file saved!" + json);

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
