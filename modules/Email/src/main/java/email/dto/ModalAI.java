package email.dto;

public enum ModalAI {
    CHAT_GPT("chat-gpt"),
    CLAUDE_AI("claude-ai");

    private final String description;

    ModalAI(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + " (" + description + ")";
    }
}
