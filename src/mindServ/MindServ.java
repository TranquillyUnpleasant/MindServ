package mindServ;

import arc.util.*;
import com.sun.net.httpserver.*;
import mindServ.req.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.*;

public class MindServ {

    public static ContentHandler contentHandler = new ContentHandler();
    public static HttpServer server;
    public static ThreadPoolExecutor executor;

    public static int mapCount;
    public static int schemCount;
    public static int errorCount;

    public static final String assets = "content/";
    public static final int port = 6969;
    public static final long startTime = Time.millis();
    public static final String[] prefixes = new String[]{"d ", "h ", "m ", "s ", "ms "};

    public static void main(String[] args) {
        Log.info("Loading server.");
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            Log.err(e);
            System.exit(1);
        }
        File contentDir = new File(assets);
        if (!contentDir.exists() && !contentDir.mkdir()) {
            Log.err("Could not create content directory.");
            System.exit(0);
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        server.createContext("/", new HandleGet());
        server.createContext("/map", new HandleMap());
        server.createContext("/schematic", new HandleSchem());
        server.setExecutor(executor);
        server.start();

        Log.info("Server started on port " + port + ".");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        while (!Objects.equals(input, "exit")) {
            try {
                input = reader.readLine().trim();
                switch (input) {
                    case "status" -> {
                        Log.info("Server Status:");
                        Log.info("  Runtime: @", runTime());
                        Log.info("  Maps processed: @", mapCount);
                        Log.info("  Schematics processed: @", schemCount);
                        Log.info("  Errors: @", errorCount);
                    }
                    case "exit" -> {
                        server.stop(0);
                        System.exit(0);
                    }
                    default -> Log.warn("Invalid input. Type in @ or @", "status", "exit");
                }
            } catch (IOException e) {
                Log.err(e);
            }
        }
    }

    public static String runTime() {
        StringBuilder format = new StringBuilder();
        int elapsed = (int) (startTime - Time.millis());
        int[] time = new int[]{
                elapsed / 86400000,
                (elapsed % 86400000) / 3600000,
                (elapsed % 3600000) / 60000,
                (elapsed % 60000) / 1000,
                (elapsed & 1000)
        };
        for (int i = 0 ; i < 5 ;i++){
            if (time[i] > 0){
                format.append(time[i]).append(prefixes[i]);
            }
        }
        return format.toString();
    }

    public static void emptyResponse(HttpExchange ex, int code) {
        try {
            ex.sendResponseHeaders(code, -1);
        } catch (IOException e) {
            Log.err(e);
            errorCount++;
        }
    }

    public static void fileResponse(HttpExchange ex, File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            OutputStream body = ex.getResponseBody();
            ex.sendResponseHeaders(200, bytes.length);
            body.write(bytes);
            body.close();
        } catch (Exception e) {
            emptyResponse(ex, 500);
            Log.err(e);
            errorCount++;
        }
    }

    private static final String AlphaNumericString =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "0123456789" +
                    "abcdefghijklmnopqrstuvxyz";

    public static String randomString(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(AlphaNumericString.charAt((int) (Math.random() * AlphaNumericString.length())));
        }
        return sb.toString();
    }
}
