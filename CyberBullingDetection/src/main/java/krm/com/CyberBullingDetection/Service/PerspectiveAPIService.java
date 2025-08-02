package krm.com.CyberBullingDetection.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import krm.com.CyberBullingDetection.Model.BullyReport;
import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Model.ToxicityLabel;
import krm.com.CyberBullingDetection.Model.User;
import krm.com.CyberBullingDetection.Repo.CommentRepo;
import krm.com.CyberBullingDetection.Repo.ReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerspectiveAPIService {
    private String apiKey ="AIzaSyB8AAEDX89lBvhqMkn0Z8Z5ucXagPZHwbY";

    private final ReportRepo reportRepo;
    private final CommentRepo commentRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public Map<String, Double> analyzeComment(Comment com) throws Exception {
        // Validate input
        if (com == null || com.getContent() == null || com.getAuthor() == null) {
            throw new IllegalArgumentException("Invalid comment data provided");
        }

        String content = com.getContent();
        User author = com.getAuthor();
        User target = com.getTarget();



        // Prepare API request
        String url = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;

        Map<String, Object> request = new HashMap<>();
        request.put("comment", Map.of("text", content));
        request.put("languages", List.of("en"));
        request.put("requestedAttributes", Map.of("TOXICITY", Collections.emptyMap(), "SEVERE_TOXICITY", Collections.emptyMap(), "INSULT", Collections.emptyMap(), "THREAT", Collections.emptyMap(), "OBSCENE", Collections.emptyMap(), "IDENTITY_ATTACK", Collections.emptyMap()));

        // Call Perspective API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Process API response
        JsonNode responseBody = objectMapper.readTree(response.getBody());
        JsonNode scores = responseBody.get("attributeScores");

        Map<String, Double> result = new HashMap<>();
        scores.fieldNames().forEachRemaining(label -> {
            double value = scores.get(label).get("summaryScore").get("value").asDouble();
            result.put(label, value);
        });

        // Calculate normalized scores
        double obsceneScore = result.get("OBSCENE") * 100;
        double toxicityScore = result.get("TOXICITY") * 100;
        double severeToxicityScore = result.get("SEVERE_TOXICITY") * 100;
        double insultScore = result.get("INSULT") * 100;
        double threatScore = result.get("THREAT") * 100;
        double identityAttackScore = result.get("IDENTITY_ATTACK") * 100;

        double totalScore = (obsceneScore + toxicityScore + insultScore + identityAttackScore + threatScore + severeToxicityScore) / 6;


// Step 1: Create and save the Comment without the BullyReport reference
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setToxic(true);
        comment.setAnalyzedDate(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setTarget(target);
        comment.setToxicityScore(toxicityScore);

// Determine label before persisting
        ToxicityLabel label = determineToxicityLabel(totalScore);
        comment.setToxicityLabel(label);

// Save comment first to generate its ID
        Comment savedComment = commentRepo.save(comment);

// Step 2: Create BullyReport and link to the saved Comment
        BullyReport bullyReport = new BullyReport();
        bullyReport.setBully(author);
        bullyReport.setVictim(target);
        bullyReport.setTimestamp(LocalDateTime.now());

        bullyReport.setObsceneScore(obsceneScore);
        bullyReport.setToxicityScore(toxicityScore);
        bullyReport.setSevereToxicityScore(severeToxicityScore);
        bullyReport.setInsultScore(insultScore);
        bullyReport.setThreatScore(threatScore);
        bullyReport.setIdentityAttackScore(identityAttackScore);
        bullyReport.setScore(totalScore);
        bullyReport.setLabel(label);
        bullyReport.setComment(savedComment); // Link the comment

// Save the report now (it has a valid comment reference)
        BullyReport savedReport = reportRepo.save(bullyReport);

// Step 3: Back-reference the report inside the comment (optional if needed for bidirectional use)
        savedComment.setBullyReport(savedReport);
        commentRepo.save(savedComment); // Safe final update


        return result;
    }

    private ToxicityLabel determineToxicityLabel(double score) {
        if (score >= 95) return ToxicityLabel.SEVERE_TOXIC;
        if (score >= 85) return ToxicityLabel.TOXIC;
        if (score >= 70) return ToxicityLabel.INSULT;
        if (score >= 50) return ToxicityLabel.THREAT;
        if (score >= 20) return ToxicityLabel.OBSCENE;
        if (score >= 1) return ToxicityLabel.IDENTITY_HATE;
        return ToxicityLabel.NONE;
    }
}