package Objects;

public class Message {
    private String content;

    public Message(String content) {
        this.content = content;
    }

    public String toString() {
        return "#%" + content + "#%";
    }
}
