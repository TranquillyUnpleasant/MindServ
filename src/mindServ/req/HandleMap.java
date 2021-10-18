package mindServ.req;

import arc.util.*;
import arc.util.serialization.*;
import com.sun.net.httpserver.*;
import mindServ.*;

import javax.imageio.*;
import java.io.*;
import java.util.*;

import static mindServ.MindServ.*;

public class HandleMap implements HttpHandler{
    @Override
    public void handle(HttpExchange ex) throws IOException{
        Log.info(ex.getRequestMethod());
        if(!ex.getRequestMethod().equals("POST")){
            emptyResponse(ex, 400);
            return;
        }

        InputStream data = ex.getRequestBody();
        Log.info(data.available());

        ContentHandler.Map map = contentHandler.readMap(data);

        Log.info(map.author);
        Log.info(map.description);

        File imgFile = new File(assets + randomString(10) + ".png");
        Log.info("Before boolean check");
        List<String> param = ex.getRequestHeaders().get("terrain");
        boolean terrain = param != null && Boolean.parseBoolean(param.get(0));
        Log.info("It gets here");
        Log.info(map.image);
        Log.info(map.terrain);
        Log.info("There isnt anything to log");
        ImageIO.write(terrain ? map.terrain : map.image, "png", imgFile);

        Headers headers = ex.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        if(map.author != null){
            headers.add("author", Base64Coder.encodeString(map.author));
            headers.add("desc", Base64Coder.encodeString(map.description));
            headers.add("size", Base64Coder.encodeString(map.image.getWidth() + " x " + map.image.getHeight()));
            headers.add("name", Base64Coder.encodeString(map.name));
        }

        fileResponse(ex, imgFile);
    }
}
