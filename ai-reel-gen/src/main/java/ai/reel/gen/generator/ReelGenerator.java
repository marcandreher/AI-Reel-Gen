package ai.reel.gen.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.reel.gen.helpers.GPTClient;
import ai.reel.gen.helpers.MaryClient;
import ai.reel.gen.helpers.PixabayClient;
import commons.marcandreher.Commons.Flogger;
import commons.marcandreher.Commons.Flogger.Prefix;
import commons.marcandreher.Utils.StringUtils;
import io.github.aminovmaksim.chatgpt4j.model.enums.ModelType;

public class ReelGenerator {

    private String prompt = "Hey you are now AI-Short Generator and will help me writing the text for a youtube short that could go viral!\n\n"
            + "EXAMPLE: In **2017** the internet was **swept up** END EXAMPLE" +
            "YOUR ONLY JOB IS writing the plain text:\n" +
            "NO CALL TO ACTION\n" +
            "HIGHLIGHT MULTIPLE MINIMUM 10 KEYWORDS WITH **{keyword}** a keyword is a appealing word that could get a good picture on pixabay\n"
            +
            "YOU ARE ONLY THE SPEAKER and write a SHORT text (1000 chars, minimum 10 keywords)\n\n" +
            "YOUR TOPIC TO WRITE is: ";

    public String output = null;

    private String openAIAPIKey = "";
    private String pixabayAPIKey = "";
    private String maryAPIUrl = "";

    public String buildNum;

    private List<String> imageUrls = new ArrayList<>();
    private List<String> imageFiles;

    public ReelGenerator(String script, String openAIAPIKey, String pixabayAPIKey, String maryAPIUrl) {
        this.prompt += script;

        this.openAIAPIKey = openAIAPIKey;
        this.pixabayAPIKey = pixabayAPIKey;
        this.maryAPIUrl = maryAPIUrl;

        buildNum = StringUtils.generateRandomString(7);
        output(Prefix.INFO, "Starting build");
    }

    public ReelGenerator toGPT() {
        output = new GPTClient(openAIAPIKey).prompt(prompt).model(ModelType.GPT_3_5_TURBO_0301).response();
        output(Prefix.INFO, output);
        return this;
    }

    public ReelGenerator generateAudio() throws Exception {
        output(Prefix.INFO, "Generating audio");
        String get = new MaryClient(maryAPIUrl, this).request(output);
        output(Prefix.INFO, "Audio generated: " + get);
        return this;
    }

    public ReelGenerator getPictures() throws Exception {
        output(Prefix.INFO, "Getting pictures");

        PixabayClient pixabayClient = new PixabayClient(pixabayAPIKey);
      

        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(output);

        while (matcher.find()) {
            String keyword = matcher.group(1);
            output(Prefix.INFO, "Getting picture for keyword: " + keyword);
            try {
                imageUrls.add(pixabayClient.query(keyword).orientation(PixabayClient.Orientation.VERTICAL).perPage(3).search("ai-short-gen (POC)").get(0));
            } catch (Exception e) {
               output(Prefix.WARNING, "No image found for keyword: " + keyword);
            }

        }

        ImageGenerator imageGenerator = new ImageGenerator();
        imageFiles = imageGenerator.generateImage(imageUrls, this);
        return this;

    }

    public ReelGenerator generateVideo() throws Exception {
        VideoGenerator videoGenerator = new VideoGenerator(imageFiles, this);

        videoGenerator.transcribeVideo(videoGenerator.addAudioFile(videoGenerator.generateVideo()));
        return this;
    }



    public void output(Prefix prefix, String message) {
        Flogger.instance.log(prefix, "BUILD[" + buildNum + "] " + message, 0);
    }

    public void output(ai.reel.gen.utils.Prefix prefix, String message) {
        Flogger.instance.log(prefix+ "BUILD[" + buildNum + "] " + message, 0);
    }


}
