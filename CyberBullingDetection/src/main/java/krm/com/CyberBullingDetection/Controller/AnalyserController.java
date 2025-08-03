package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Dto.CommentDto;
import krm.com.CyberBullingDetection.Service.PerspectiveAPIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analyze")
public class AnalyserController {

    private final PerspectiveAPIService apiService;

    // Use constructor injection - this is a modern Spring best practice
    public AnalyserController(PerspectiveAPIService apiService) {
        this.apiService = apiService;
    }

    @PostMapping(value = "/toxicity")
    public ResponseEntity<?> analyzeCommentToxicity(@RequestBody CommentDto commentDto) {
        // Simple and clear validation
        if (commentDto == null || commentDto.getContent() == null || commentDto.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment content must not be null or empty.");
        }

        try {
            // The service receives the DTO and handles the model conversion internally
            return ResponseEntity.ok(apiService.analyzeComment(commentDto.toComment()));
        } catch (Exception e) {
            // Consistent error handling
            System.err.println("Error analyzing comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error analyzing comment. Please try again later.");
        }
    }
}