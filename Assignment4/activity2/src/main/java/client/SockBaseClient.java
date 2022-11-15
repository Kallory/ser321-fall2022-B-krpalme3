package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;
import utils.Protocol;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {

    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int port = 8080; // default port

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username
        System.out.println("Please provide your name for the server.");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();

        // Build the first request object just including the name
//        Request op = Request.newBuilder()
//                .setOperationType(Request.OperationType.NAME)
//                .setName(strToSend).build();
        Request request = Protocol.createRequest(Request.OperationType.NAME, strToSend, "");
        Response response;
        boolean exit = false;
        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();
            in = serverSock.getInputStream();

            request.writeDelimitedTo(out);

            // read from the server
            response = Response.parseDelimitedFrom(in);

            // print the server response. 
            System.out.println(response.getMessage());

            while (exit == false) {
                System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
                boolean numberHappy = false;
                int userOptionNum = 0;
                boolean secondGuess = false;
                String tile1 = "";
                String tile2 = "";
                while (numberHappy == false) {
                    try {
                        strToSend = stdin.readLine();
                        userOptionNum = Integer.parseInt(strToSend);
                        numberHappy = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Must be a number");
                        System.out.println("* \nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
                    }
                }

                if (userOptionNum == 1) {
                    request = Protocol.createRequest(Request.OperationType.LEADER, "", "");
                    request.writeDelimitedTo(out);
                    response = Response.parseDelimitedFrom(in);

                    // print the server response.
                    System.out.println(response.getMessage());
                    for (Entry lead : response.getLeaderList()) {
                        System.out.println(lead.getName() + ": " + lead.getWins());
                    }
                } else if (userOptionNum == 2) {
                    request = Protocol.createRequest(Request.OperationType.NEW, "", "");
                    request.writeDelimitedTo(out);
                    response = Response.parseDelimitedFrom(in);
                    boolean playingGame = true;
                    System.out.println(response.getMessage());
                    System.out.println("Type: " + response.getResponseType());
                    System.out.println("Board: \n" + response.getBoard());
                    System.out.println("Eval: " + response.getEval());
                    System.out.println("Second: " + response.getSecond());

                    while (playingGame == true) {
                        System.out.println("Select a tile to flip (eg a1)");
                        if (secondGuess == false) {
                            tile1 = stdin.readLine();
                            if (tile1.equalsIgnoreCase("exit")) {
                                System.exit(0);
                            }
                            request = Protocol.createRequest(Request.OperationType.TILE1, "", tile1);
                            request.writeDelimitedTo(out);
                            response = Response.parseDelimitedFrom(in);
                            System.out.println(response.getMessage());
                            System.out.println("Type: " + response.getResponseType());
                            System.out.println("Board: \n" + response.getBoard());
                            System.out.println("Eval: " + response.getEval());
                            System.out.println("Second: " + response.getSecond());
                            secondGuess = true;
                        } else {
                            tile2 = stdin.readLine();
                            if (tile2.equalsIgnoreCase("exit")) {
                                System.exit(0);
                            }
                            request = Protocol.createRequest(Request.OperationType.TILE2, "", tile2);
                            request.writeDelimitedTo(out);
                            response = Response.parseDelimitedFrom(in);
                            System.out.println(response.getMessage());
                            System.out.println("Type: " + response.getResponseType());
                            System.out.println("Board: \n" + response.getBoard());
                            System.out.println("Eval: " + response.getEval());
                            System.out.println("Second: " + response.getSecond());

                            secondGuess = false;
                        }
                    }
                } else if (userOptionNum == 3) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
}


