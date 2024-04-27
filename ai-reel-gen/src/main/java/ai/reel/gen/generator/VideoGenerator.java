package ai.reel.gen.generator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ai.reel.gen.helpers.ReelUtils;
import commons.marcandreher.Commons.Flogger.Prefix;


public class VideoGenerator {

    private List<String> imageFiles;
    public static String ffmpegPath;
    private ReelGenerator reelGenerator;

    private double duration;
    private double imageDuration;

    public VideoGenerator(List<String> imageFiles, ReelGenerator reelGenerator) {
        this.imageFiles = imageFiles;
        this.reelGenerator = reelGenerator;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            ffmpegPath = "bin/ffmpeg.exe";
        } else {
            ffmpegPath = "ffmpeg";
        }
    }

    public String generateVideo() throws Exception {
        duration = ReelUtils.getDurationInSeconds(new File("temp/" + reelGenerator.buildNum + ".wav"));
        imageDuration = duration / imageFiles.size(); // Duration for each image
    
        StringBuilder ffmpegCmdBuilder = new StringBuilder();
        
        ffmpegCmdBuilder.append("-y");
    
        // Input images loop
        for (String imageFile : imageFiles) {
            ffmpegCmdBuilder.append(" -loop 1 -t ").append(imageDuration).append(" -i ").append(escapeFilePath(imageFile));
        }
    
        // Constructing filter_complex string programmatically
        StringBuilder filterComplexBuilder = new StringBuilder();
        filterComplexBuilder.append(" -filter_complex ");
        
        for (int i = 0; i < imageFiles.size(); i++) {
            filterComplexBuilder.append("[")
                               .append(i)
                               .append(":v]scale=720:1280:force_original_aspect_ratio=increase[v")
                               .append(i)
                               .append("];");
        }
        for (int i = 0; i < imageFiles.size(); i++) {
            filterComplexBuilder.append("[v").append(i).append("]");
        }
        filterComplexBuilder.append("concat=n=").append(imageFiles.size()).append(":v=1:a=0[outv]");
        ffmpegCmdBuilder.append(filterComplexBuilder);
    
        // Output file
        ffmpegCmdBuilder.append(" -map [outv] -c:v libx264 -c:a aac -strict -2 ").append(escapeFilePath("temp/" + reelGenerator.buildNum + ".mp4"));
    
        // Execute FFmpeg command
        reelGenerator.output(Prefix.INFO, "Starting adding pictures to video");
        ReelUtils.runFFmpegCMD(ffmpegPath, ffmpegCmdBuilder.toString(), reelGenerator);
    
        return "temp/" + reelGenerator.buildNum + ".mp4";
    }

    // Function to escape file path for Linux
    private String escapeFilePath(String filePath) {
        return filePath.replace(" ", "\\ "); // Escape spaces in file path
    }
    

    public String addAudioFile(String input) throws IOException, InterruptedException {
        StringBuilder ffmpegCmdBuilder = new StringBuilder("-y ");
        ffmpegCmdBuilder.append("-i " + input + " -i temp/" + reelGenerator.buildNum + ".wav -c:v copy -map 0:v:0 -map 1:a:0 -shortest temp/" + reelGenerator.buildNum + "1.mp4");
        ReelUtils.runFFmpegCMD(ffmpegPath, ffmpegCmdBuilder.toString(), reelGenerator);
        return "temp/" + reelGenerator.buildNum + "1.mp4";
    }

    public void transcribeVideo(String input) throws Exception {
        new TranscribeVideos("temp/" + reelGenerator.buildNum + ".wav", input, "build/" + reelGenerator.buildNum + ".mp4", reelGenerator.output.replaceAll("\\*", "").replaceAll(",", "").replaceAll("\n", " "), reelGenerator).generateSubtitles();
    }
    

}
