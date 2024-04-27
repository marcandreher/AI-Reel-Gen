package ai.reel.gen.generator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import ai.reel.gen.utils.Prefix;

public class FFMPEG {

    private ReelGenerator reelGenerator;

    public FFMPEG(ReelGenerator reelGenerator) {
        this.reelGenerator = reelGenerator; 
    }

    private ProcessBuilder proBuilder = null;
    private List<String> cmd;

    public void render() throws Exception{
        System.out.println(Prefix.FFMPEG + String.join(" ", cmd));
        proBuilder = new ProcessBuilder(cmd);
        proBuilder.redirectErrorStream(true);
        Process process = proBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reelGenerator.output(Prefix.FFMPEG, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception(Prefix.FFMPEG + "Command failed with exit code " + exitCode);
        }
    }

    public void setCmd(String... cmd) {
        this.cmd = Arrays.asList(cmd);
    }

    public void setCmd(List<String> cmd) {
        this.cmd = cmd;
    }
    
    public List<String> getCmd() {
        return cmd;
    }
    
}
