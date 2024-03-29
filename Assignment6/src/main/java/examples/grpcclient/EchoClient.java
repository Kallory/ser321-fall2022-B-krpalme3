package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import service.*;
import test.TestProtobuf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.protobuf.Empty; // needed to use Empty

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// just to show how to use the empty in the protobuf protocol
    // Empty empt = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  private final WeatherGrpc.WeatherBlockingStub blockingStub4;
  private final RecipeGrpc.RecipeBlockingStub blockingStub5;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = WeatherGrpc.newBlockingStub(channel);
    blockingStub5 = RecipeGrpc.newBlockingStub(channel);
  }

  public void askServerToParrot(String message) {

    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  public void showWeatherCoord(double latitude, double longitude) {
    WeatherCoordinateRequest req = WeatherCoordinateRequest.newBuilder().setLatitude(latitude).setLongitude(longitude).build();
    WeatherResponse response;

    try {
      System.out.println("request sent");
      response = blockingStub4.atCoordinates(req);
      System.out.println("response received");
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    System.out.println("Weather: " + response.getCurrentConditions());
  }

  public void showWeatherCity(String name) {

    WeatherCityRequest req = WeatherCityRequest.newBuilder().setCityName(name).build();
    WeatherResponse response;

    try {
      System.out.println("request sent");
      response = blockingStub4.inCity(req);
      System.out.println("response received");
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }

    System.out.println("Weather in city " + name + ": " + response.getCurrentConditions());
  }

  public void addRecipe(String recipeName, String author, String ingredientName, int quantity, String details) {
    RecipeReq req = RecipeReq.newBuilder().setName(recipeName).setAuthor(author).addIngredient(Ingredient.newBuilder().
      setName(ingredientName).setQuantity(quantity).setDetails(details)).build();  
    RecipeResp response;

    try {
      System.out.println("Request sent");
      response = blockingStub5.addRecipe(req);
      System.out.println("Response recieved");
    } catch (Exception e) {
      System.err.println("RPC failed" + e);
      return;
    }

    System.out.println(response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;


    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("blockingStub3 RPC failed in getServices(): " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("blockingStub3 RPC failed in findServer():" + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 6) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      // ##############################################################################
      // ## Assume we know the port here from the service node it is basically set through Gradle
      // here.
      // In your version you should first contact the registry to check which services
      // are available and what the port
      // etc is.

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       * 
       * You should make sure your client does not crash in case the service node
       * crashes or went offline.
       */

      // Just doing some hard coded calls to the service node without using the
      // registry
      // create client
      EchoClient client = new EchoClient(channel, regChannel);

      // call the parrot service on the server
      client.askServerToParrot(message);

      // ask the user for input how many jokes the user wants
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      boolean exit = false;
      while (exit != true) {
      System.out.println("Which service would you like?"); // NO ERROR handling of wrong input here.
      System.out.println("1) Joke");
      System.out.println("2) weather");
      System.out.println("3) Recipe");
      System.out.println("4) exit");
      String input = reader.readLine();

        int num = Integer.parseInt(input);
        if (num == 1) {
          // Reading data using readLine
          System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
          input = reader.readLine();
          num = Integer.parseInt(input);
          // calling the joked service from the server with num from user input
          client.askForJokes(num);

          // adding a joke to the server
          client.setJoke("I made a pencil with two erasers. It was pointless.");

          // showing 6 joked
          client.askForJokes(Integer.valueOf(6));
        } else if (num == 2) {
          System.out.println("Choose city or enter latitude, longitude: ");
          System.out.println("1) Detroit");
          System.out.println("2) Seattle");
          System.out.println("3) enter latitude and longitude manually");
          input = reader.readLine();
          num = Integer.parseInt(input);

          if (num == 1) {
            client.showWeatherCity("Detroit");
          } else if (num == 2) {
            client.showWeatherCity("Seattle");
          } else if (num == 3) {
            client.showWeatherCoord(1, 1);
          }
        } else if (num == 3) {
          String ingredient = "rice";
          int quantity = 4;
          String details = "make Sushi";
          client.addRecipe("Sushi", "Gordon Ramsey", ingredient, quantity, details);
        } else if (num == 4) {
          exit = true;
        }
      }
      // ############### Contacting the registry just so you see how it can be done

      if (args[5].equals("true")) { 
        // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
        // get thread's services
        client.getServices(); // get all registered services 

        // get parrot
        client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
        
        // get all setJoke
        client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service

        // get getJoke
        client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service

        // does not exist
        client.findServer("random"); // shows the output if the server does not find a given service
      }

    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
