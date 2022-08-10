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
        List<String> param = ex.getRequestHeaders().get("terrain");
        ImageIO.write(
        param != null && Boolean.parseBoolean(param.get(0)) ? map.terrain : map.image, "png", imgFile
        );

        Headers headers = ex.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        if(map.author != null){
            headers.add("author", Base64Coder.encodeString(map.author));
            headers.add("desc", Base64Coder.encodeString(map.description));
            headers.add("size", Base64Coder.encodeString(map.image.getWidth() + " x " + map.image.getHeight()));
            headers.add("name", Base64Coder.encodeString(map.name));
        }

        fileResponse(ex, imgFile);

        mapCount++;
    }
}
