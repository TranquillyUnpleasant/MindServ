package mindServ.req;

import com.sun.net.httpserver.*;

import java.io.*;

import static mindServ.MindServ.emptyResponse;

public class HandleGet implements HttpHandler{
    @Override
    public void handle(HttpExchange ex) throws IOException{
        if (!ex.getRequestMethod().equals("GET")){
            emptyResponse(ex, 400);
            return;
        }

        String res = "Server running, hosting maps and schematics!";
        ex.getResponseHeaders().add("Content-Type", "text/html");
        ex.sendResponseHeaders(200, res.length());
        OutputStream body = ex.getResponseBody();
        body.write(res.getBytes());
        body.close();
    }
}
