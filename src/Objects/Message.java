package Objects;

import java.io.Serializable;

public class Message implements Serializable{
    private static final long serialVersionUID = 1529685098267757690L;
    private String from;
    private String content;

    public Message(String from, String content) {
        this.content = content;
        this.from = from;
    }

    public String toString() {
        return "#%" + content + "#%";
    }

    public String getFrom(){
        return from;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String c){
        content = c;
    }
}
