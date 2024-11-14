package teamnova.elite_gear.model;

import java.util.Date;

public class MessageResponse {
    private String content;
    private Date timestamp;

    public MessageResponse(String content) {
        this.content = content;
        this.timestamp = new Date();
    }

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}