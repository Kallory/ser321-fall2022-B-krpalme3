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
import java.util.List;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;

public class RecipeImpl extends RecipeGrpc.RecipeImplBase {

    public RecipeImpl() {
        super();
    }

    @Override
    public void addRecipe(RecipeReq req, StreamObserver<RecipeResp> responseObserver) {
        System.out.println("Name Of Recipe: " + req.getName());
        System.out.println("Author: " + req.getAuthor());

        RecipeResp.Builder response = RecipeResp.newBuilder();


        List<Ingredient> ingredientList = req.getIngredientList();
        for (int i = 0; i < ingredientList.size(); i++) {
            System.out.println("Ingredient: " + ingredientList.get(i));
        }

        response.setIsSuccess(true);
        response.setMessage("Recipe added: " + req.getName() + "\n" + "by: " + req.getAuthor() + "\n");

        RecipeResp res = response.build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void viewRecipes(com.google.protobuf.Empty req, StreamObserver<RecipeViewResp> responseObserver) {
        
    }

    @Override
    public void rateRecipe(RecipeRateReq req, StreamObserver<RecipeResp> responseObserver) {

    }

    // rpc addRecipe (RecipeReq) returns (RecipeResp) {}
    // rpc viewRecipes (google.protobuf.Empty) returns (RecipeViewResp) {}
    // rpc rateRecipe (RecipeRateReq) returns (RecipeResp) {}
}
