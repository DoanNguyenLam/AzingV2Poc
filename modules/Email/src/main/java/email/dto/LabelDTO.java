package email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LabelDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("messageListVisibility")
    private MessageListVisibility messageListVisibility;

    @JsonProperty("labelListVisibility")
    private LabelListVisibility labelListVisibility;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("messagesTotal")
    private int messagesTotal;

    @JsonProperty("messagesUnread")
    private int messagesUnread;

    @JsonProperty("threadsTotal")
    private int threadsTotal;

    @JsonProperty("threadsUnread")
    private int threadsUnread;

    @JsonProperty("color")
    private Color color;

    @Data
    public static class Color {

        @JsonProperty("textColor")
        private String textColor;

        @JsonProperty("backgroundColor")
        private String backgroundColor;
    }

    public enum MessageListVisibility {
        @JsonProperty("show")
        SHOW,
        @JsonProperty("hide")
        HIDE
    }

    public enum LabelListVisibility {
        @JsonProperty("labelShow")
        LABEL_SHOW,
        @JsonProperty("labelShowIfUnread")
        LABEL_SHOW_IF_UNREAD,
        @JsonProperty("labelHide")
        LABEL_HIDE
    }

    public enum Type {
        @JsonProperty("system")
        SYSTEM,
        @JsonProperty("user")
        USER
    }
}
