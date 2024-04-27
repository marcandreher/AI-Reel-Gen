package ai.reel.gen.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ai.reel.gen.generator.FFMPEG;
import ai.reel.gen.generator.ReelGenerator;

public class ReelUtils {
    public static double getDurationInSeconds(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        audioInputStream.close();
        return Double.valueOf(durationInSeconds);
    }

    public static void runFFmpegCMD(String ffmpegLocation, String command, ReelGenerator reelGenerator) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegLocation);
        cmd.addAll(Arrays.asList(command.split("\\s")));

        FFMPEG ffmpeg = new FFMPEG(reelGenerator);
        ffmpeg.setCmd(cmd);
        try {
            ffmpeg.render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatTime(double time) {
        // Format the given time value in seconds to the SRT time format
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);
        int millis = (int) ((time % 1) * 1000);
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis);
    }

}
