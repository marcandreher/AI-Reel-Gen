package ai.reel.gen.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import ai.reel.gen.generator.ReelGenerator;
import commons.marcandreher.Commons.Flogger.Prefix;

public class MaryClient {

    private String serverUrl;

    private URIBuilder uriBuilder;

    private ReelGenerator reelGenerator;

    public MaryClient(String url, ReelGenerator reelGenerator) {
        this.serverUrl = url;
        this.reelGenerator = reelGenerator;

        try {
            uriBuilder = new URIBuilder(serverUrl);
        } catch (URISyntaxException e) {
            reelGenerator.output(Prefix.WARNING, "Weird URL: " + url);
        }
    }

    public MaryClient voice(String voiceName) {

        uriBuilder.setParameter("VOICE", voiceName);
        reelGenerator.output(Prefix.INFO, "Selecting voice: " + voiceName);
        return this;
    }

    public String request(String input) throws Exception {
        uriBuilder.setParameter("INPUT_TEXT", input);
        uriBuilder.setParameter("INPUT_TYPE", "TEXT");
        uriBuilder.setParameter("OUTPUT_TYPE", "AUDIO");
        uriBuilder.setParameter("AUDIO", "WAVE_FILE");
        uriBuilder.setParameter("LOCALE", "en_US");

        URI uri = uriBuilder.build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(uri);
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = httpResponse.getEntity();
                if (responseEntity != null) {
                    byte[] audioData = EntityUtils.toByteArray(responseEntity);
                    generateAudioFile(audioData, Paths.get("temp/" + reelGenerator.buildNum + ".wav"));
                    return "temp/" + reelGenerator.buildNum + ".wav";
                }
            }
        }
        throw new Exception("No audio data received");
    }

    private void generateAudioFile(byte[] data, Path filePath) throws Exception {

        if (data == null) {
            throw new Exception("No audio data received");
        }

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, data);
    }

}
