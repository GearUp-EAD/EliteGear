package teamnova.elite_gear.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
public class MessageResponse {
    // Getters and setters
    private String content;
    private Date timestamp;

    public MessageResponse(String content) {
        this.content = content;
        this.timestamp = new Date();
    }

}