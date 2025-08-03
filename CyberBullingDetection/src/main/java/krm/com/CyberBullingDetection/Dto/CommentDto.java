package krm.com.CyberBullingDetection.Dto;

import krm.com.CyberBullingDetection.Model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private UserDto author;
    private UserDto target;

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setId(this.id);
        comment.setContent(this.content);
        if (this.author != null) {
            comment.setAuthor(this.author.toUser());
        }
        if (this.target != null) {
            comment.setTarget(this.target.toUser());
        }
        return comment;
    }
}
