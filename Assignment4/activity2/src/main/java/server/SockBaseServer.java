package server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;
import org.json.JSONTokener;
import utils.Protocol;

public class SockBaseServer implements Runnable {
    static String logFilename = "logs.txt";

    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket;
    int port = 9099; // default port
    static Game game;
    Response response;
    static int leaderEntryIndex = 0;
    static Response.Builder res;
    static String tileString1;
    static String tileString2;
    static String msg2;
    static char tileChar1 = '*';
    static char tileChar2 = '+';
    static boolean won = false;

    public SockBaseServer(Socket sock){
        this.clientSocket = sock;
        response = Protocol.createResponse(Response.ResponseType.GREETING, res, "", "", false, false, "");
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e){
            System.out.println("Error in constructor: " + e);
        }
    }

    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void run() {
        String name;
        Entry leaderEntry = null;
        boolean nameGotten = false;
        System.out.println("New client has connected on port: " + this.port);
        System.out.println("The new count is " + SockBaseServer.getClientCount());
        try {
            // read the proto object and put into new object
            while (true) {
                Request op = Request.parseDelimitedFrom(in);
                String result = null;
                // if the operation is NAME (so the beginning then say there is a connection and greet the client)
                if (nameGotten == false && op.getOperationType() == Request.OperationType.NAME) {
                    // get name from proto object
                    name = op.getName();

                    // writing a connection message to the log with name and CONNECT
                    writeToLog(name, Message.CONNECT);
                    System.out.println("Got a connection and a name: " + name);
                    String msg = "Hello " + name + " and welcome. Welcome to a simple game of battleship. ";
                    Entry leader = Protocol.createEntry(name, 0, 0);
                    res.addLeader(leader);
                    res.build();
                    response = Protocol.createResponse(Response.ResponseType.GREETING, res,"", "", false, false, msg);
                    response.writeDelimitedTo(out);
                    nameGotten = true;
                } else if (op.getOperationType() == Request.OperationType.LEADER) {

                    response = Protocol.createResponse(Response.ResponseType.LEADER, res,"", "", false, false, "LeaderBoard:\n");
                    response.writeDelimitedTo(out);
                    for (Entry lead: res.getLeaderList()){
                        System.out.println(lead.getName() + ": " + lead.getWins());

                    }
                } else if (op.getOperationType() == Request.OperationType.NEW) {
                    String msg = "Starting new game";
                    response = Protocol.createResponse(Response.ResponseType.PLAY, res, game.getBoard(), "", false, false, msg);
                    response.writeDelimitedTo(out);
                } else if (op.getOperationType() == Request.OperationType.TILE1) {
                    tileString1 = op.getTile();
                    String rowString = tileString1.substring(0, 1);
                    String colString = tileString1.substring(1);
                    int row = getRow(rowString);
                    int col = getCol(colString);
                    game.flipTile(row, col);
                    tileChar1 = game.getTile(row, col);
                    msg2 = "tile1Recieved";
                    System.out.println("Board: \n" + game.showBoard());
                    response = Protocol.createResponse(Response.ResponseType.PLAY, res, game.getBoard(), tileString1, false, false, msg2);
                    response.writeDelimitedTo(out);
                } else if (op.getOperationType() == Request.OperationType.TILE2) {
                    tileString2 = op.getTile();
                    String rowString2 = tileString2.substring(0, 1);
                    String colString2 = tileString2.substring(1);
                    int row2 = getRow(rowString2);
                    int col2 = getCol(colString2);
                    String rowString1 = tileString1.substring(0, 1);
                    String colString1 = tileString1.substring(1);
                    int row1 = getRow(rowString1);
                    int col1 = getCol(colString1);
                    game.flipTile(row2, col2);
                    tileChar2 = game.getTile(row2, col2);
                    msg2 = "tile2Recieved";
                    System.out.println("Board: \n" + game.showBoard());
                    boolean eval = false;
                    if (tileChar2 == tileChar1) {
                        eval = true;
                        won = game.getWon();
                    } else {
                        eval = false;
                        game.unflipTile(row1, col1, row2, col2);
                    }
                    response = Protocol.createResponse(Response.ResponseType.PLAY, res, game.getBoard(), tileString2, true, eval, msg2);
                    response.writeDelimitedTo(out);
                }
            }




            // Example how to start a new game and how to build a response with the board which you could then send to the server
            // LINES between ====== are just an example for Protobuf and how to work with the different types. They DO NOT
            // belong into this code as is!

            // ========= Example start
//            game.newGame(); // starting a new game
//
//            // Example on how you could build a simple response for PLAY as answer to NEW
//            Response response2 = Response.newBuilder()
//            .setResponseType(Response.ResponseType.PLAY)
//            .setBoard(game.getBoard()) // gets the hidden board
//            .setEval(false)
//            .setSecond(false)
//            .build();
//
//            // this just temporarily un-hides, the "hidden" image in game is still the same
//            System.out.println("One flipped tile");
//            System.out.println(game.tempFlipWrongTiles(1,2));
//
//            System.out.println("Two flipped tiles");
//            System.out.println(game.tempFlipWrongTiles(1,2, 2, 4));
//
//            System.out.println("Flip for found match, hidden in game will now be changed");
//            // I flip two tiles here but it will NOT necessarily be a match, since I hard code the rows/cols here
//            // and the board is randomized
//            game.replaceOneCharacter(1,2);
//            game.replaceOneCharacter(2,4);
//            System.out.println(game.getBoard()); // shows the now not hidden tiles
//
//
//            // On the client side you would receive a Response object which is the same as the one in line 73, so now you could read the fields
//            System.out.println("\n\nExample response:");
//            System.out.println("Type: " + response2.getResponseType());
//            System.out.println("Board: \n" + response2.getBoard());
//            System.out.println("Eval: \n" + response2.getEval());
//            System.out.println("Second: \n" + response2.getSecond());
//
//            // Creating Entry and Leader response
//            Response.Builder res = Response.newBuilder()
//            .setResponseType(Response.ResponseType.LEADER);
//
//            // building an Entry for the leaderboard
//            Entry leader = Entry.newBuilder()
//            .setName("name")
//            .setWins(0)
//            .setLogins(0)
//            .build();
//
//            // building another Entry for the leaderboard
//            Entry leader2 = Entry.newBuilder()
//            .setName("name2")
//            .setWins(1)
//            .setLogins(1)
//            .build();
//
//            // adding entries to the leaderboard
//            res.addLeader(leader);
//            res.addLeader(leader2);
//
//            // building the response
//            Response response3 = res.build();
//
//            // iterating through the current leaderboard and showing the entries
//
//            System.out.println("\n\nExample Leaderboard:");
//            for (Entry lead: response3.getLeaderList()){
//                System.out.println(lead.getName() + ": " + lead.getWins());
//            }
//
//            // ========= Example end

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    if (out != null)  out.close();
                    if (in != null)   in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public int getRow(String letter) {
        if (letter.equalsIgnoreCase("a")) {
            return 1;
        } else if (letter.equalsIgnoreCase("b")) {
            return 2;
        } else if (letter.equalsIgnoreCase("c")) {
            return 3;
        }
        else return 0;
    }

    public int getCol(String number) {
        if (number.equalsIgnoreCase("1")) {
            return 2;
        } else if (number.equalsIgnoreCase("2")) {
            return 4;
        } else if (number.equalsIgnoreCase("3")) {
            return 6;
        } else if (number.equalsIgnoreCase("4")) {
            return 8;
        } else return 0;
    }


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();
            System.out.println(date);

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }

    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a log's entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
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


    public static void main (String[] args) throws Exception {
        game = new Game();
        game.newGame();
        res = Response.newBuilder()
                .setResponseType(Response.ResponseType.LEADER);
        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        Socket clientSocket = null;
        ServerSocket serv = null;

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        while (true) {
            System.out.println("Accepting a request");
            clientSocket = serv.accept();
//        clientManager = new ClientManager(clientSocket);

            SockBaseServer server = new SockBaseServer(clientSocket);
            Thread t = new Thread(server);
            t.start();
        }
    }
}

