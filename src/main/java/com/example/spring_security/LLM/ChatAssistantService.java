package com.example.spring_security.LLM;

import com.openai.client.OpenAIClient;
import com.openai.models.*;
import com.openai.models.chat.completions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatAssistantService {

    private final OpenAIClient client;

    @Autowired
    public ChatAssistantService(OpenAIClient client) {
        this.client = client;
    }

    public String refineText(String rawText, String tone) {
        String systemInstruction = "You are a professional text refinement engine. " +
                "Your task is to rewrite the user's text to improve grammar, spelling, and match the requested tone: '"
                + tone + "'. " +
                "\n\nCRITICAL RULES:" +
                "\n1. DETECT the language of the user input and output the result IN THE SAME LANGUAGE." +
                "\n2. Do NOT translate the text (unless the user explicitly asks to in the text)." +
                "\n3. Return ONLY the refined text string. No markdown, no quotes, no conversational filler." +
                "\n4. If the input is already perfect, return it unchanged.";

        String userPrompt = String.format("Refine this text: \"%s\"", rawText);

        return callOpenAi(systemInstruction, userPrompt);
    }

    public List<String> suggestReplies(String incomingMessage) {
        // 1. Prompt "Quốc tế hóa"
        String systemInstruction = "You are a Smart Reply API assistant. " +
                "Task: Analyze the incoming message and generate 3 short, polite, and contextually appropriate replies. "
                +
                "\n\nOUTPUT RULES:" +
                "\n- DETECT the language of the incoming message and generate replies IN THE SAME LANGUAGE." +
                "\n- Generate exactly 3 replies." +
                "\n- Separate replies with a vertical bar '|'." +
                "\n- NO numbering, NO newlines, NO introductory text." +
                "\n- Example Output: Thank you|I will check|Sounds good";

        // Prompt người dùng đơn giản để tránh nhiễu ngôn ngữ
        String userPrompt = "Incoming message: \"" + incomingMessage + "\"";

        // 2. Gọi AI (Temperature 0.6 - 0.7 là đẹp cho task này)
        String rawResponse = callOpenAi(systemInstruction, userPrompt);

        // 3. Xử lý chuỗi (Giữ nguyên logic cũ của bạn)
        if (rawResponse != null && !rawResponse.isEmpty()) {
            String[] suggestions = rawResponse.split("\\|");
            List<String> result = new ArrayList<>();
            for (String s : suggestions) {
                // Xử lý thêm: Đôi khi AI lỡ thêm dấu ngoặc kép hoặc khoảng trắng thừa
                String clean = s.trim().replaceAll("^\"|\"$", "");
                if (!clean.isEmpty()) {
                    result.add(clean);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private String callOpenAi(String systemContent, String userContent) {
        List<ChatCompletionMessageParam> messages = new ArrayList<>();

        // 1. SỬA LỖI: Dùng Builder cho System Message
        // Sai: ChatCompletionMessageParam.ofSystem(systemContent)
        // Đúng: Phải build object ChatCompletionSystemMessageParam trước
        ChatCompletionSystemMessageParam systemMsg = ChatCompletionSystemMessageParam.builder()
                .content(systemContent)
                .build();
        messages.add(ChatCompletionMessageParam.ofSystem(systemMsg));

        // 2. SỬA LỖI: Dùng Builder cho User Message
        // Sai: ChatCompletionMessageParam.ofUser(userContent)
        // Đúng: Phải build object ChatCompletionUserMessageParam trước
        ChatCompletionUserMessageParam userMsg = ChatCompletionUserMessageParam.builder()
                .content(userContent)
                .build();
        messages.add(ChatCompletionMessageParam.ofUser(userMsg));

        // Các bước tiếp theo giữ nguyên
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("groq/compound")
                .messages(messages)
                .maxTokens(500)
                .temperature(0.3)
                .build();

        ChatCompletion completion = client.chat().completions().create(params);

        return completion.choices().get(0).message().content().orElse("");
    }
}