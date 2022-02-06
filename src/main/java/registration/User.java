package registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String email;

    private String groupName;

    private Long chatId;

    private String userName;

    private String userSurname;

    private Integer currentQuestion;

    private String currentBlock;

    public User(long chatId) {

        this.chatId = chatId;
        this.email = "";
        this.groupName = "";
        this.currentQuestion = 0;
    }

    public User(long chatId, String email, String groupName) {

        this.chatId = chatId;
        this.email = email;
        this.groupName = groupName;
    }
}
