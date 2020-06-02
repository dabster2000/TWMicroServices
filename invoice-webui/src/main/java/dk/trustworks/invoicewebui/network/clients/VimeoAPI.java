package dk.trustworks.invoicewebui.network.clients;

import com.clickntap.vimeo.Vimeo;
import com.clickntap.vimeo.VimeoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.trustworks.invoicewebui.model.vimeo.DataItem;
import dk.trustworks.invoicewebui.model.vimeo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class VimeoAPI {

    private static final Logger log = LoggerFactory.getLogger(VimeoAPI.class);

    public static LocalDate videoAge = LocalDate.now();

    @Value("${vimeoToken}")
    private String vimeoToken;

    private String createdTime;

    public String getTrustworksStatus() {
        Vimeo vimeo = new Vimeo(vimeoToken);
        try {
            VimeoResponse vimeoResponse = vimeo.get("https://api.vimeo.com/me/albums/4783832/videos?direction=desc&sort=date");
            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(vimeoResponse.getJson().toString(), Response.class);
            if(response.getData()==null) return "";
            createdTime = response.getData().get(0).getCreatedTime();
            videoAge = LocalDate.parse(createdTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return "https://player.vimeo.com"+response.getData().get(0).getUri().replace("videos", "video");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String[] getTrustworksTrips() {
        Vimeo vimeo = new Vimeo(vimeoToken);
        try {
            VimeoResponse vimeoResponse = vimeo.get("https://api.vimeo.com/me/albums/4783108/videos?direction=desc&sort=date");
            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(vimeoResponse.getJson().toString(), Response.class);
            if(response.getData()==null) return new String[0];
            String[] urls = new String[response.getData().size()];
            int i = 0;
            for (DataItem dataItem : response.getData()) {
                urls[i++] = "https://player.vimeo.com"+dataItem.getUri().replace("videos", "video");
            }
            return urls;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String[] getTrustworksKnowledge() {
        Vimeo vimeo = new Vimeo(vimeoToken);
        try {
            VimeoResponse vimeoResponse = vimeo.get("https://api.vimeo.com/me/albums/4831161/videos?direction=desc&sort=date");
            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(vimeoResponse.getJson().toString(), Response.class);
            if(response.getData()==null) return new String[0];
            String[] urls = new String[response.getData().size()];
            int i = 0;
            for (DataItem dataItem : response.getData()) {
                urls[i++] = "https://player.vimeo.com"+dataItem.getUri().replace("videos", "video");
            }
            return urls;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

}
