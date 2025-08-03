package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Dto.CommentDto;
import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Repo.CommentRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepo commentRepo;

    // Use constructor injection
    public CommentController(CommentRepo commentRepo) {
        this.commentRepo = commentRepo;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> createComment(@RequestBody CommentDto commentDto) {
        // Simple and clear validation
        if (commentDto == null || commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment content must not be null or empty.");
        }

        try {
            // Convert DTO to Model before saving
            Comment commentToSave = commentDto.toComment();
            Comment savedComment = commentRepo.save(commentToSave);

            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            System.err.println("Failed to save comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save comment. An internal error occurred.");
        }
    }
}