package utils;

import buffers.RequestProtos.*;
import buffers.ResponseProtos.*;

public class Protocol {

    public static Request createRequest(Request.OperationType ot, String name, String tile) {
        Request request = Request.newBuilder()
                .setOperationType(ot)
                .setName(name)
                .setTile(tile)
                .build();
        return request;
    }

    public static Logs createLogs(int index, String log) {
        Logs logs = Logs.newBuilder()
                .setLog(index, log)
                .build();

        return logs;
    }


    public static Response createResponse(Response.ResponseType rt, Response.Builder res, String board, String flipped, boolean second, boolean eval, String msg) {
        Response response = Response.newBuilder()
                .setResponseType(rt)
                .setFlippedBoard(flipped)
                .addAllLeader(res.getLeaderList())
                .setBoard(board)
                .setSecond(second)
                .setEval(eval)
                .setMessage(msg)
                .build();

        //eval - true = match

        return response;
    }


    public static Entry createEntry(String name, int wins, int logins) {
        Entry entry = Entry.newBuilder()
                .setName(name)
                .setWins(wins)
                .setLogins(logins)
                .build();

        return entry;
    }
}






//    Person john =
//            Person.newBuilder()
//                    .setId(1234)
//                    .setName("John Doe")
//                    .setEmail("jdoe@example.com")
//                    .addPhones(
//                            Person.PhoneNumber.newBuilder()
//                                    .setNumber("555-4321")
//                                    .setType(Person.PhoneType.HOME))
//                    .build();
