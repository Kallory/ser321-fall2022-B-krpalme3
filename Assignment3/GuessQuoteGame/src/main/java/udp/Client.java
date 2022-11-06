package udp;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status.
 *
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing
 *        still happens in the GUI. If it is desired to continue processing in the
 *        background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 *
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 *
 */

public class Client implements OutputPanel.EventHandlers {
    JDialog frame;
    PicturePanel picturePanel;
    OutputPanel outputPanel;
    static OutputStream out;
    static ObjectOutputStream os;
    static InputStream in;
    static ObjectInputStream inOs;
    static NetworkHelper networkHelper = new NetworkHelper();
    boolean nameFlag = false;
    /**
     * Construct dialog
     */
    public Client() {
        frame = new JDialog();
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // set up the top picture frame
        picturePanel = new PicturePanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.25;
        frame.add(picturePanel, c);

        // set up the input, button, and output area
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.75;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        outputPanel = new OutputPanel();
        outputPanel.addEventHandlers(this);
        frame.add(outputPanel, c);
    }

    /**
     * Shows the current state in the GUI
     * @param makeModal - true to make a modal window, false disables modal behavior
     */
    public void show(boolean makeModal) {
        frame.pack();
        frame.setModal(makeModal);
        frame.setVisible(true);
    }

    /**
     * Creates a new game and set the size of the grid
     * @param dimension - the size of the grid will be dimension x dimension
     */
    public void newGame(int dimension, NetworkHelper networkHelper) throws IOException, ClassNotFoundException {
        picturePanel.newGame(dimension);
        outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");

        if (inOs != null) {
            String msg = (String) inOs.readObject();
            System.out.println("got message: " + msg);
            outputPanel.appendOutput(msg);
        } else System.out.println("Didn't get message :(");
    }

    /**
     * Insert an image into the grid at position (col, row)
     *
     * @param filename - filename relative to the root directory
     * @param row - the row to insert into
     * @param col - the column to insert into
     * @return true if successful, false if an invalid coordinate was provided
     * @throws IOException An error occurred with your image file
     */
    public boolean insertImage(String filename, int row, int col) throws IOException {
        String error = "";
        try {
            // insert the image
            if (picturePanel.insertImage(filename, row, col)) {
                // put status in output
                outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
                return true;
            }
            error = "File(\"" + filename + "\") not found.";
        } catch(PicturePanel.InvalidCoordinateException e) {
            // put error in output
            error = e.toString();
        }
        outputPanel.appendOutput(error);
        return false;
    }

    /**
     * Submit button handling
     *
     * Change this to whatever you need
     */
    @Override
    public void submitClicked() {
        // An example how to update the points in the UI
        System.out.println("Submit click pressed, nothing in function executed");
        outputPanel.setPoints(10);
        System.out.println("output panel points set in the background");
        // Pulls the input box text
        String input = outputPanel.getInputText();
        System.out.println("input panel input: " + input);
        String response = "";
        JSONObject responseJson = null;
        // if has input
        if (input.length() > 0) {
            // append input to the output panel
            outputPanel.appendOutput(input);


            if (nameFlag == false) {
                String name = input;

                try {
                    os.writeObject("{\"type\": \"sendName\", \"value\": " + "\"" + name + "\"}" );
                    response = (String) inOs.readObject();
//                    byte[] responseBytes = NetworkUtils.receive(in);
                    responseJson = new JSONObject(response);
                    System.out.println();
                    String msgReceived = "";
                    if (responseJson.getBoolean("newName") == true) {
                        msgReceived = "Hello, " + responseJson.getString("value");
                    } else {
                        msgReceived = "Welcome back, " + responseJson.getString("value");
                    }
                    outputPanel.appendOutput(msgReceived);
                    outputPanel.appendOutput("Type new game for a new game or records to view records or quit to quit.");
                    nameFlag = true;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (input.equalsIgnoreCase("start game") || input.equalsIgnoreCase("new game") || input.equalsIgnoreCase("sg") || input.equalsIgnoreCase("ng")) {
                try {
                    os.writeObject("{\"type\": \"newGame\"}");
                    System.out.println("wrote type newGame to server");
                    response = (String) inOs.readObject();
                    System.out.println("Json response : " + response);
                    responseJson = new JSONObject(response);
                    String fileName = responseJson.getString("value");
                    insertImage(fileName, 0, 0);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

//            PrintWriter writer = new PrintWriter(out, true);
//            writer.println(input);
//            try {
//                String msg = getServerMsg(networkHelper.getIn());
//                outputPanel.appendOutput(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            // clear input text box
            outputPanel.setInputText("");
        }
    }

    /**
     * Key listener for the input text box
     *
     * Change the behavior to whatever you need
     */
    @Override
    public void inputUpdated(String input) {
        if (input.equals("surprise")) {
            outputPanel.appendOutput("You found me!");
        }
    }

    public String getServerMsg(InputStream input) throws IOException{
        int bufLen = 1024;
        byte[] bytesReceived = new byte[bufLen];

        String strReceived = "";
        try {
            //Reader reader = new InputStreamReader(input);
            System.out.println("Waiting on input");
            int numBytesReceived = input.read(bytesReceived, 0, bufLen);
            strReceived = new String(bytesReceived, 0, numBytesReceived);
            System.out.println("strReceived " + strReceived);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strReceived;
    }



    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        in = null;
        Socket sock;
        int port = 0;
        String host = "";


        try {
            if (args[0] != null) port = Integer.parseInt(args[0]);
            else port = 8080;
            if (args[1] != null) host = args[1];
            else host = "localhost";

            System.out.println("Connecting to port: " + port);
            System.out.println("Connecting to host: " + host);
        } catch (NumberFormatException nfe) {
            System.out.println("Port must be an integer");
            System.exit(2);
        }

        try {
            sock = new Socket(host, port);
            out = sock.getOutputStream();
            os = new ObjectOutputStream(out);
            os.flush();
            System.out.println("ObjectOutputStream initiated");
//            in = sock.getInputStream();
            inOs = new ObjectInputStream(sock.getInputStream());
            System.out.println("ObjectInputStream initiated");
            networkHelper = new NetworkHelper(out, in, sock, port, host);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // create the frame
        Client main = new Client();
        System.out.println("clientObject initiated");

        // set up the UI to display on image
        main.newGame(1, networkHelper);
        System.out.println("new game initiated");
        // add images to the grid
//        main.insertImage("img/Jack_Sparrow/quote4.png", 0, 0);

        // show the GUI dialog as modal
        main.show(true); // you should not have your logic after this. Your main logic should happen whenever "submit" is clicked
    }
}
