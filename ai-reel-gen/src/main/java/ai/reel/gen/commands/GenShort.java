package ai.reel.gen.commands;

import ai.reel.gen.generator.ReelGenerator;
import ai.reel.gen.main.Main;
import commons.marcandreher.Commons.Flogger;
import commons.marcandreher.Commons.Flogger.Prefix;
import commons.marcandreher.Input.Command;

public class GenShort implements Command{

    @Override
    public void executeAction(String[] arg0, Flogger arg1) {
        ReelGenerator reelGenerator = new ReelGenerator("video about the log4j exploit", Main.ENV.get("OPENAIAPI_KEY"), Main.ENV.get("PIXABAYAPI_KEY"), Main.ENV.get("MARYAPI_URL"));
        try {

            reelGenerator.toGPT();
            reelGenerator.generateAudio();
            reelGenerator.getPictures();
            reelGenerator.generateVideo();
        } catch (Exception e) {
            Flogger.instance.error(e);
            reelGenerator.output(Prefix.ERROR, "Build failed");
            return;
        } 
        reelGenerator.output(Prefix.INFO, "Build successful");
    }

    @Override
    public String getAlias() {
      return "";
    }

    @Override
    public String getDescription() {
        return "Generate short";
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "genshort";
    }
    
}
