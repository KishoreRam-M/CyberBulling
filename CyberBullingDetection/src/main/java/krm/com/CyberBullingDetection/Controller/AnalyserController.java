package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Service.PerspectiveAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analyze")
public class AnalyserController {

    @Autowired
    private PerspectiveAPIService apiService;

    @PostMapping("/toxicity")
    public ResponseEntity<?> analyzeCommentToxicity(@RequestBody Comment comment) {
        try {
            if (comment == null || comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Comment content must not be null or empty.");
            }

              return  ResponseEntity.ok(apiService.analyzeComment(comment)
              );


        } catch (Exception e) {
            e.printStackTrace();
            String error = "Error analyzing comment: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
