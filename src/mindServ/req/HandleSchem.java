package mindServ.req;

import arc.files.*;
import arc.util.*;
import arc.util.serialization.*;
import com.sun.net.httpserver.*;
import mindustry.game.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import static mindServ.ContentHandler.schemHeader;
import static mindServ.MindServ.*;

public class HandleSchem implements HttpHandler{
    @Override
    public void handle(HttpExchange ex) throws IOException{
        if (ex.getRequestMethod().equals("GET")){
            String token = ex.getRequestHeaders().get("token").get(0);
            File file = new File(assets + token + ".msch");
            if (!file.exists()){
                emptyResponse(ex, 400);
                return;
            }

            Log.info("File requested. Sending.");
            ex.getResponseHeaders().add("Content-Type", "application/json");
            fileResponse(ex, file);
            return;
        }
        if (!ex.getRequestMethod().equals("POST")){
            emptyResponse(ex, 400);
            return;
        }

        InputStream data = ex.getRequestBody();
        Scanner reader = new Scanner(data);
        String req = reader.hasNext() ? reader.next() : "";
        BufferedImage preview;
        Schematic schem;
        try{
            if (req.startsWith(schemHeader)){
                Log.info("Processing text schematic.");
                schem = contentHandler.parseSchematic(req);
                preview = contentHandler.previewSchematic(schem);
            }else if (req.startsWith("https://") && req.endsWith(".msch")){
                Log.info("Processing file schematic.");
                schem = contentHandler.parseSchematicURL(req);
                preview = contentHandler.previewSchematic(schem);
            }else{
                emptyResponse(ex, 400);
                return;
            }
        }catch (Exception e){
            Log.err(e);
            emptyResponse(ex, 400);
            return;
        }
        String name = schem.name().replaceAll("[/ ]", "_");
        Log.info(name);

        String id = randomString(10);
        File imgFile = new File(assets + id + "_msch.png");
        File schemFile = new File(assets + id + ".msch");
        ImageIO.write(preview, "png", imgFile);
        Schematics.write(schem, new Fi(schemFile));

        Headers headers = ex.getResponseHeaders();

        headers.add("Content-Type", "application/json");
        headers.add("name", Base64Coder.encodeString(name));
        headers.add("desc", Base64Coder.encodeString(schem.description()));
        headers.add("item", Base64Coder.encodeString(schem.requirements().toString()));
        headers.add("power_in", Base64Coder.encodeString(String.valueOf((int)(schem.powerProduction() * 60))));
        headers.add("power_out", Base64Coder.encodeString(String.valueOf((int)(schem.powerConsumption() * 60))));
        headers.add("requestid", Base64Coder.encodeString(id));

        fileResponse(ex, imgFile);
    }
}
