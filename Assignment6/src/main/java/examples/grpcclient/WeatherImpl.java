package main.java.examples.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;

import java.net.URL;
import java.io.*;

public class WeatherImpl extends WeatherGrpc.WeatherImplBase {

    public WeatherImpl() {
        super();


    }

    @Override
    public void atCoordinates(WeatherCoordinateRequest req, StreamObserver<WeatherResponse> responseObserver) {
        System.out.println("Latitude from client: " + req.getLatitude());
        System.out.println("Longtitude from client: " + req.getLongitude());
        WeatherResponse.Builder response = WeatherResponse.newBuilder();
        String httpString = "https://api.openweathermap.org/data/2.5/onecall?lat=" + req.getLatitude() + "&lon=" + req.getLongitude() + "&appid=18ad0533ec014a798489d7e2b69cd8d6";
        URL url = null;
        HttpURLConnection con = null;
        DataOutputStream wr = null;
        StringBuilder responseString = new StringBuilder(); 
        try {
            url = new URL(httpString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            wr = new DataOutputStream (con.getOutputStream());
            wr.writeBytes(httpString);
            wr.close();
                //Get Response  
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                responseString.append(line);
                responseString.append('\r');
            }
            rd.close();
            System.out.println(responseString);
            if (con != null) {
            con.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            response.setIsSuccess(false);
            response.setError("ERROR");
            WeatherResponse res = response.build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
            System.out.println("key not active yet: ");
        }

        
        if (responseString.equals("")) {
            response.setIsSuccess(false);
            response.setError("ERROR");
        } else {
            response.setIsSuccess(true);
            response.setCurrentTemp(response.getCurrentTemp());
        }

        WeatherResponse res = response.build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void inCity(WeatherCityRequest req, StreamObserver<WeatherResponse> responseObserver) {
        System.out.println("City name: " + req.getCityName());
        String name = req.getCityName();
        double latitude = 0;
        double longitude = 0;
        if (name.equals("Detroit")) {
          latitude = 42.331429;
          longitude =  -83.045753;
        } else if (name.equals("Seattle")) {
          latitude = 47.608013;
          longitude = -122.335167;
        }

        System.out.println("Latitude of city: " + latitude);
        System.out.println("Longtitude of city: " + longitude);
        WeatherResponse.Builder response = WeatherResponse.newBuilder();
        String httpString = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&appid=18ad0533ec014a798489d7e2b69cd8d6";
        URL url = null;
        HttpURLConnection con = null;
        DataOutputStream wr = null;
        StringBuilder responseString = new StringBuilder(); 
        try {
            url = new URL(httpString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            wr = new DataOutputStream (con.getOutputStream());
            wr.writeBytes(httpString);
            wr.close();
                //Get Response  
            InputStream is = con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = rd.readLine()) != null) {
                responseString.append(line);
                responseString.append('\r');
            }
            rd.close();
            System.out.println(responseString);
            if (con != null) {
            con.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            response.setIsSuccess(false);
            response.setError("ERROR");
            WeatherResponse res = response.build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
            System.out.println("key not active yet: ");
            
        }

        
        if (responseString.equals("")) {
            response.setIsSuccess(false);
            response.setError("ERROR");
        } else {
            response.setIsSuccess(true);
            response.setCurrentTemp(response.getCurrentTemp());
        }

        WeatherResponse res = response.build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
    
    // rpc atCoordinates (WeatherCoordinateRequest) returns (WeatherResponse) {}
    // rpc inCity (WeatherCityRequest) returns (WeatherResponse) {}
    // rpc listCities (google.protobuf.Empty) returns (CitiesResponse) {}
}
