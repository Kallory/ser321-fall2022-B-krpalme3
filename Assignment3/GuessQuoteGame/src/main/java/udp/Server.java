package udp;

import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    static File userNameFile = null;
    static String fileNamePath = "usernameFile.json";
    static FileWriter fileWriter = null;
    /* JSON Request
        { "type": "sendName", "getLeaderboard", "startGame"},
          "data": {"username": <String>, "guess": <String>}}
        }

        JSON Response
        {"ok": <bool>,
         "value": <String>,
         "error": <String>
        }
     */

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
        ObjectInputStream inOs = null;
        int port = 0;
        String name = "";
        int counter = 0;
        boolean nameFlag = false;
        int bufLen = 1024;
        boolean connected = false;

        JSONObject json = new JSONObject();
        JSONObject jsonResponse = null;
        try {
            if (args[0] != null) port = Integer.parseInt(args[0]);
            else port = 8080;
            serv = new ServerSocket(port);

            while (true) {
                try {
                    if (!connected) {
                        System.out.println("Server waiting for a connection on port " + port);
                        sock = serv.accept(); // blocking wait
                        out = sock.getOutputStream();
                        os = new ObjectOutputStream(out);
//                    InputStream input = sock.getInputStream();
                        inOs = new ObjectInputStream(sock.getInputStream());
                        System.out.println("Server connected to client");
                        connected = true;
                    }
                    PrintWriter writer = new PrintWriter(out, true);

                    createFile();
                    if (nameFlag == false) {
                        os.writeObject("Please enter your name: ");
                        nameFlag = true;
                    }
//                    writer.println("Please enter your name: ");

//                    byte clientInput[] = new byte[bufLen];
//                    int numBytesReceived = input.read(clientInput, 0, bufLen);

//                    byte[] clientInput = NetworkUtils.receive(input);
//
                    if (inOs != null) {
                        String jsonRequestString = (String) inOs.readObject();
                        JSONObject jsonRequest = new JSONObject(jsonRequestString);
                        System.out.println("Json request: " + jsonRequest);
                        jsonResponse = new JSONObject();
                        System.out.println("jsonResponse initiated, ready to send");
                        System.out.println("Server got a type: " + jsonRequest.getString("type"));
                        if (jsonRequest.getString("type").equals("sendName")) {

                            jsonResponse.put("ok", true);
                            jsonResponse.put("type", "sendName");
                            name = jsonRequest.getString("value");
                            jsonResponse.put("value", name);

                            final Scanner scanner = new Scanner(userNameFile);
                            while (scanner.hasNext()) {
                                System.out.println("Inside scanner while loop");
                                String lineFromFile = scanner.nextLine();
                                lineFromFile = lineFromFile.replace("\\n", "").replace("\\r", "").replace("\"", "");
//                                System.out.println(name.trim());

//                                System.out.println("Line from file: " + lineFromFile);
//                                System.out.print("Name is: " + name + " and contains = ");
//                                System.out.println((lineFromFile.contains(name)));
                                if (!(lineFromFile.contains(name))) {
                                    System.out.println("Hello, " + jsonResponse.get("value"));
                                    jsonResponse.put("newName", true);
                                    System.out.println("jsonResponse: " + jsonResponse.toString());
                                    os.writeObject(jsonResponse.toString());
                                    os.flush();
                                } else {
                                    System.out.println("Welcome back, " + name);
                                    jsonResponse.put("newName", false);
                                    os.writeObject(jsonResponse.toString());
                                    os.flush();
                                }
                            }
                        } else if (jsonRequest.getString("type").equals("newGame")) {
                            System.out.println("New game started");
                            jsonResponse.put("ok", true);
                            jsonResponse.put("type", "newGame");
                            jsonResponse.put("value", "img/Jack_Sparrow/quote4.png");
                            System.out.println("jsonResponse: " + jsonResponse.toString());
                            os.writeObject(jsonResponse.toString());
                            os.flush();
                        }
                    }

                    //        main.insertImage("img/Jack_Sparrow/quote4.png", 0, 0);




                    try {
                        if (jsonResponse.getBoolean("newName") == true) {
                            fileWriter.write(jsonResponse.getString("value"));
                        }
                        fileWriter.write(jsonResponse.getString("score"));
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("JSON file saved!" + json);

                } catch (Exception e) {
                    System.out.println("Client disconnected");
                    connected = false;
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
