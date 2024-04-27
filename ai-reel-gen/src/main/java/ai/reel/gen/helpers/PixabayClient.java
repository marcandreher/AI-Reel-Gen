package ai.reel.gen.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import commons.marcandreher.Commons.GetRequest;

public class PixabayClient {

    public enum Orientation {
        HORIZONTAL("horizontal"),
        VERTICAL("vertical"),
        ALL("all");

        private final String value;

        Orientation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private String defaultUrl;
    private String currentUrl;

    public PixabayClient(String apiKey) {
        this.defaultUrl = "https://pixabay.com/api/?key=" + apiKey;
        this.currentUrl = this.defaultUrl;
    }

    public PixabayClient orientation(Orientation orientation) {
        this.currentUrl += "&orientation=" + orientation.getValue();
        return this;
    }

    public PixabayClient query(String query) {
        this.currentUrl += "&q=" + query;
        return this;
    }

    public PixabayClient perPage(int perPage) {
        this.currentUrl += "&per_page=" + perPage;
        return this;
    }

    public List<String> search(String userAgent) throws Exception {
        try {

            String responseBody = new GetRequest(currentUrl).send(userAgent);
            JSONParser parser = new JSONParser();
            List<String> largeImageURLs = new ArrayList<>();
        
            JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
            JSONArray hitsArray = (JSONArray) jsonObject.get("hits");
        
            for (Object hit : hitsArray) {
                JSONObject hitObject = (JSONObject) hit;
                String largeImageURL = (String) hitObject.get("largeImageURL");
                if (largeImageURL != null) {
                    
                    largeImageURLs.add(largeImageURL);
                }
            }
            return largeImageURLs;
        
        }catch(Exception e) {
            
            throw new Exception("Error in PixabayClient: " + e.getMessage());
        }finally {
            currentUrl = defaultUrl;
        }
    
    
    }
    

}
