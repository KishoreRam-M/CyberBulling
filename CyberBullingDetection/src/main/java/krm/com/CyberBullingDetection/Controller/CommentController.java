package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Repo.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepo commentRepo;

    @PostMapping("/submit")
    public ResponseEntity<?> getComment(@RequestBody Comment comment) {
        try {
            if (comment == null || comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Comment content must not be empty.");
            }

            Comment savedComment = commentRepo.save(comment);

            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = "Failed to save comment: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
