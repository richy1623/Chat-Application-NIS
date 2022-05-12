package Objects;

import java.io.Serializable;

public class Message implements Serializable{
    private String[] to;
    private String from;
    private String content;

    public Message(String from, String[] to, String content) {
        this.content = content;
        this.from = from;
        this.to = to;
    }

    public String toString() {
        return "#%" + content + "#%";
    }

    public String getFrom(){
        return from;
    }

    public String[] getTo(){
        return to;
    }

    public String getContent(){
        return content;
    }
}
