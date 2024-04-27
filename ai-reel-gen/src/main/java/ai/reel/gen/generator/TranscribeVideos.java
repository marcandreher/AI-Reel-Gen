package ai.reel.gen.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class TranscribeVideos {
    private String inputWavScript;
    private String inputVideo;
    private String outputVideo;
    private String script;

    private ReelGenerator reelGenerator;
    
    public TranscribeVideos(String inputWavScript, String inputVideo, String outputVideo, String script, ReelGenerator reelGenerator) {
        this.inputWavScript = inputWavScript;
        this.inputVideo = inputVideo;
        this.outputVideo = outputVideo;
        this.script = script;
        this.reelGenerator = reelGenerator;
    }
    
    public void generateSubtitles() throws Exception {
        // Step 1: Read inputWavScript and calculate the length in seconds
        double audioLength = 0;
    
        audioLength = getAudioLength();
      
       
        
        // Step 2: Calculate the number of words in the script
        String[] words = script.split("\\s+");
        int wordCount = words.length;
        
        // Step 3: Calculate at what time each word is spoken
        double[] wordTimes = new double[wordCount];
        for (int i = 0; i < wordCount; i++) {
            wordTimes[i] = (i * audioLength) / wordCount;
        }
        
        // Step 4: Write the subtitles to a .srt file
        writeSrtFile(words, wordTimes);
        
        // Step 5: Render the video with subtitles using FFmpeg
        renderVideoWithSubtitles();
    }
    
    private double getAudioLength() throws Exception {
        // Use JavaSound to read the length of the input WAV file and return it in seconds
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(inputWavScript));
        AudioFormat format = audioInputStream.getFormat();
        long audioFileLength = new File(inputWavScript).length();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        double audioLengthInFrames = audioFileLength / (double) frameSize;
        return (audioLengthInFrames / frameRate);
    }
    
    private void writeSrtFile(String[] words, double[] wordTimes) throws IOException {
        // Write the subtitles to a new .srt file
        BufferedWriter writer = new BufferedWriter(new FileWriter("temp/" + reelGenerator.buildNum + ".srt"));
        for (int i = 0; i < words.length - 1; i++) {
            String subtitle = (i + 1) + "\n" +
                formatTime(wordTimes[i]) + " --> " + formatTime(wordTimes[i+1]) + "\n" +
                words[i] + "\n\n";
            writer.write(subtitle);
        }
        writer.close();
    }
    
    private void renderVideoWithSubtitles() throws IOException {
        FFMPEG ffmpeg = new FFMPEG(reelGenerator);
        ffmpeg.setCmd(
            VideoGenerator.ffmpegPath,
            "-i", inputVideo,
            "-vf", "subtitles=temp/"+reelGenerator.buildNum+".srt:force_style='Fontsize=30,OutlineColour=&H000000,Outline=1,Shadow=0,BorderStyle=4,Alignment=2,MarginV=125",
            "-c:v", "libx264",
            "-preset", "ultrafast",
            "-crf", "18",
            "-c:a", "copy",
            "-metadata:s:s:0", "language=eng",
            "-metadata:s:s:0", "title=Subtitle",
            "-movflags", "+faststart",
            "-y", // Overwrite output file without asking
            outputVideo
        );
        try {
            ffmpeg.render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(double time) {
        // Format the given time value in seconds to the SRT time format
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);
        int millis = (int) ((time % 1) * 1000);
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis);
    }
}