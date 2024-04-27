package ai.reel.gen.helpers;

import io.github.aminovmaksim.chatgpt4j.ChatGPTClient;
import io.github.aminovmaksim.chatgpt4j.model.ChatRequest;
import io.github.aminovmaksim.chatgpt4j.model.ChatResponse;
import io.github.aminovmaksim.chatgpt4j.model.enums.ModelType;

public class GPTClient {

    private ChatGPTClient client;

    private ChatRequest request;

    public GPTClient(String apiKey) {
         client = ChatGPTClient.builder()
        .apiKey(apiKey).requestTimeout((long)200000)
        .build();
    }

    public GPTClient prompt(String prompt) {
        request = new ChatRequest(prompt);
        return this;
    }

    public GPTClient model(ModelType model) {
        request.setModel(model.getName());
        return this;
    }

    public String response() {
        ChatResponse response = client.sendChat(request);
        return response.getChoices().get(0).getMessage().getContent();
    }

    
    
}
