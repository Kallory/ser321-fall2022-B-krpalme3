/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        return json;
    }

    public JSONObject pop() {
        JSONObject json = new JSONObject();

        int index = state.size() - 1;
        if (index == 0) {
            json.put("datatype", 2);
            json.put("type", "remove");
            json.put("data", "Nothing to remove, empty list");
        } else {
            json.put("datatype", 2);
            json.put("type", "remove");
            json.put("data", state.getElement(index));
            state.remove(index);
        }
        return json;
    }

    public JSONObject display() {
        JSONObject json = new JSONObject();

        json.put("datatype", 3);
        json.put("type", "display");
        json.put("data", state.toString());
        return json;
    }

    public  JSONObject count() {
        JSONObject json = new JSONObject();

        json.put("datatype", 4);
        json.put("type", "count");
        json.put("data", "" + state.size());
        return json;
    }

    public JSONObject switching(int indexOne, int indexTwo) {
        JSONObject json = new JSONObject();
        if (indexOne >= 0 && indexTwo >= 0 && indexOne < state.size() && indexTwo < state.size()) {
            json.put("datatype", 5);
            json.put("type", "switch");

            String temp = state.getElement(indexOne);
            System.out.println("Temp is: " + temp);
            state.remove(indexOne);
            state.addAtIndex(indexOne, state.getElement(indexTwo));
            System.out.println("Index one now contains: " + state.getElement(indexOne));
            state.remove(indexTwo);
            state.addAtIndex(indexTwo, temp);
            json.put("data", state.toString());
        } else {
            json.put("datatype", 5);
            json.put("type", "switch");
            json.put("data", "one of the indices is out of bounds");
        }

        return json;
    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
                String inStr;
   
                int choice = message.getInt("selected");
                    switch (choice) {
                        case (1):
                            inStr = (String) message.get("data");
                            returnMessage = add(inStr);
                            break;
                        case (2):
                            returnMessage = pop();
                            break;
                        case (3):
                            returnMessage = display();
                            break;
                        case (4):
                            returnMessage = count();
                            break;
                        case (5):
                            //TODO fix switch
                            inStr = (String) message.get("data");
                            int indexOfSpace = inStr.indexOf(' ');
                            String str1 = inStr.substring(0, indexOfSpace);
                            System.out.println("index of space = " + indexOfSpace);
                            String str2 = inStr.substring(indexOfSpace + 1);
                            int in1 = Integer.parseInt(str1);
                            int in2 = Integer.parseInt(str2);
                            returnMessage = switching(in1, in2);
                            break;
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
//            out.close();
//            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
