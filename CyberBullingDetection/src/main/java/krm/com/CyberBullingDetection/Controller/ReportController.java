package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Model.BullyReport;
import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Repo.CommentRepo;
import krm.com.CyberBullingDetection.Repo.ReportRepo;
import krm.com.CyberBullingDetection.Repo.Victumrepo;
import krm.com.CyberBullingDetection.Service.PerspectiveAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private PerspectiveAPIService apiService;

    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private Victumrepo victumrepo;

    @Autowired
    private CommentRepo commentRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        try {
            Optional<BullyReport> report = reportRepo.findById(id);
            if (report.isPresent()) {
                return ResponseEntity.ok(report.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching report by ID: " + e.getMessage());
        }
    }

    // THIS IS THE CORRECTED METHOD
    @PostMapping("/submit")
    public ResponseEntity<?> createReportFromComment(@RequestBody Comment comment) {
        try {
            if (comment.getAuthor() == null || comment.getTarget() == null || comment.getContent() == null) {
                return ResponseEntity.badRequest().body("Missing required comment fields: author, target, content.");
            }

            // You must call your service method to handle the complex logic.
            // Your service knows to save the Comment first, then the Report.
            apiService.analyzeComment(comment);

            // Your service method returns a Map, so we can't directly return a BullyReport.
            // You might need to change your service to return the saved BullyReport instead of a Map
            // But for now, we'll just return a success message.
            return ResponseEntity.status(HttpStatus.CREATED).body("Report creation process started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating bully report: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        try {
            List<BullyReport> reports = reportRepo.findAll();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while retrieving reports: " + e.getMessage());
        }
    }
}