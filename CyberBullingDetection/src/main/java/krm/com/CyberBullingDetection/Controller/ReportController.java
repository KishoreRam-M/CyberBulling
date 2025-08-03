package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Dto.CommentDto;
import krm.com.CyberBullingDetection.Model.BullyReport;
import krm.com.CyberBullingDetection.Repo.ReportRepo;
import krm.com.CyberBullingDetection.Service.PerspectiveAPIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PerspectiveAPIService apiService;
    private final ReportRepo reportRepo;

    // Use constructor injection
    public ReportController(PerspectiveAPIService apiService, ReportRepo reportRepo) {
        this.apiService = apiService;
        this.reportRepo = reportRepo;
    }

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
            System.err.println("Error fetching report by ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the report.");
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> createReportFromComment(@RequestBody CommentDto commentDto) {
        try {
            // Simple validation on required fields
            if (commentDto.getAuthor() == null || commentDto.getTarget() == null || commentDto.getContent() == null) {
                return ResponseEntity.badRequest().body("Missing required fields: author, target, or content.");
            }

            // The service layer handles the core logic of analyzing and creating a report
            apiService.analyzeComment(commentDto.toComment());

            // Return a simple success message
            return ResponseEntity.status(HttpStatus.CREATED).body("Report creation process initiated successfully.");

        } catch (Exception e) {
            System.err.println("Error creating bully report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the bully report.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BullyReport>> getAllReports() {
        try {
            List<BullyReport> reports = reportRepo.findAll();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            System.err.println("Error retrieving all reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}