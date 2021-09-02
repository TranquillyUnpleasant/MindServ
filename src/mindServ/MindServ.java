package mindServ;

import arc.util.*;
import com.sun.net.httpserver.*;
import mindServ.req.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class MindServ{

    public static ContentHandler contentHandler = new ContentHandler();
    public static HttpServer server;
    public static ThreadPoolExecutor executor;
    public static final String assets = "content/";
    public static final int port = 6969;

    public static void main(String[] args){
        Log.info("Loading server.");
        try{
            server = HttpServer.create(new InetSocketAddress(port), 0);
        }catch(IOException e){
            Log.err(e);
            System.exit(1);
        }
        File contentDir = new File(assets);
        if (!contentDir.exists() && !contentDir.mkdir()) {
            Log.err("Could not create content directory.");
            System.exit(0);
        }
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);

        server.createContext("/", new HandleGet());
        server.createContext("/map", new HandleMap());
        server.createContext("/schematic", new HandleSchem());
        server.setExecutor(executor);
        server.start();

        Log.info("Server started on port " + port + ".");
    }

    public static void emptyResponse(HttpExchange ex, int code){
        try{
            ex.sendResponseHeaders(code, -1);
        }catch(IOException e){
            Log.err(e);
        }
    }

    public static void fileResponse(HttpExchange ex, File file){
        try{
            byte[] bytes = Files.readAllBytes(file.toPath());
            OutputStream body = ex.getResponseBody();
            ex.sendResponseHeaders(200, bytes.length);
            body.write(bytes);
            body.close();
        }catch(Exception e){
            emptyResponse(ex, 500);
            Log.err(e);
        }
    }

    private static final String AlphaNumericString =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
    "0123456789" +
    "abcdefghijklmnopqrstuvxyz";

    public static String randomString(int n){
        StringBuilder sb = new StringBuilder(n);
        for(int i = 0; i < n; i++){
            sb.append(AlphaNumericString.charAt((int)(Math.random() * AlphaNumericString.length())));
        }
        return sb.toString();
    }
}
