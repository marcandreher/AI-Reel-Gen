package ai.reel.gen.generator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import commons.marcandreher.Commons.Flogger.Prefix;

public class ImageGenerator {

    public List<String> generateImage(List<String> urls, ReelGenerator reelGenerator) throws Exception {
        List<String> gotFiles = new ArrayList<>();
        int imageCount = 0;
        for (String url : urls) {

            imageCount++;
            double progress = ((double) imageCount / urls.size()) * 100.0;
            reelGenerator.output(Prefix.INFO, "Downloading images and cropping them [" + progress + "%]");

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(response.body()));
                int targetWidth = 1440;
                int targetHeight = 2560;

                double originalAspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                double targetAspectRatio = (double) targetWidth / targetHeight;

                int cropWidth, cropHeight;

                if (originalAspectRatio > targetAspectRatio) {
                    // Crop width and keep height
                    cropWidth = (int) (originalImage.getHeight() * targetAspectRatio);
                    cropHeight = originalImage.getHeight();
                } else {
                    // Crop height and keep width
                    cropWidth = originalImage.getWidth();
                    cropHeight = (int) (originalImage.getWidth() / targetAspectRatio);
                }

                BufferedImage croppedImage = originalImage.getSubimage((originalImage.getWidth() - cropWidth) / 2,
                        (originalImage.getHeight() - cropHeight) / 2, cropWidth, cropHeight);

                BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(croppedImage, 0, 0, targetWidth, targetHeight, null);
                g.dispose();

                // Check if 70% of the image is black
                int blackPixels = 0;
                for (int x = 0; x < resizedImage.getWidth(); x++) {
                    for (int y = 0; y < resizedImage.getHeight(); y++) {
                        Color color = new Color(resizedImage.getRGB(x, y));
                        if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                            blackPixels++;
                        }
                    }
                }
                double blackRatio = (double) blackPixels / (resizedImage.getWidth() * resizedImage.getHeight());
                if (blackRatio >= 0.7) {

                    reelGenerator.output(Prefix.WARNING,
                            "Skipped image (" + (imageCount - 1) + ") because it is mostly black");
                    imageCount--;

                    continue;
                }

                ImageIO.write(resizedImage, "jpg", new File("temp/" + reelGenerator.buildNum + imageCount + ".jpg"));
                gotFiles.add("temp/" + reelGenerator.buildNum + imageCount + ".jpg");
            } else {
                throw new Exception("Failed to download and convert image");
            }
        }
        reelGenerator.output(Prefix.INFO, "Images generated: " + imageCount);
        return gotFiles;
    }

}
