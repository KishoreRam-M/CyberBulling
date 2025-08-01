package krm.com.CyberBullingDetection.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import krm.com.CyberBullingDetection.Model.BullyReport;
import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Model.ToxicityLabel;
import krm.com.CyberBullingDetection.Repo.CommentRepo;
import krm.com.CyberBullingDetection.Repo.ReportRepo;
import krm.com.CyberBullingDetection.Repo.Victumrepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PerspectiveAPIService {


    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private Victumrepo victumrepo;

    @Autowired
    private CommentRepo commentRepo;
    private String apiKey ="AIzaSyB8AAEDX89lBvhqMkn0Z8Z5ucXagPZHwbY";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Double> analyzeComment(String content) throws Exception {
        String url = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;

        Map<String, Object> request = new HashMap<>();
        request.put("comment", Map.of("text", content));
        request.put("languages", List.of("en"));
        request.put("requestedAttributes", Map.of(
                "TOXICITY", new HashMap<>(),
                "SEVERE_TOXICITY", new HashMap<>(),
                "INSULT", new HashMap<>(),
                "THREAT", new HashMap<>(),
                "OBSCENE", new HashMap<>(),
                "IDENTITY_ATTACK", new HashMap<>()
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        JsonNode responseBody = objectMapper.readTree(response.getBody());

        JsonNode scores = responseBody.get("attributeScores");
        Map<String, Double> result = new HashMap<>();

        for (Iterator<String> it = scores.fieldNames(); it.hasNext();) {
            String label = it.next();
            double value = scores.get(label).get("summaryScore").get("value").asDouble();
            result.put(label, value);
        }


        double  OBSCENE=result.get("OBSCENE")*100;
        double  TOXICITY= result.get("TOXICITY")*100;
        double SEVERE_TOXICITY=result.get("SEVERE_TOXICITY");
        double INSULT= result.get("INSULT")*100;
        double THREAT=result.get("THREAT")*100;
        double  IDENTITY_ATTACK=result.get("IDENTITY_ATTACK")*100;
         double total = OBSCENE+TOXICITY+INSULT+IDENTITY_ATTACK+THREAT+SEVERE_TOXICITY/5;

        BullyReport bullyReport = new BullyReport();
        bullyReport.setIdentityAttackScore(IDENTITY_ATTACK);
        bullyReport.setSevereToxicityScore(SEVERE_TOXICITY);
        bullyReport.setToxicityScore(TOXICITY);
        bullyReport.setThreatScore(THREAT);
        bullyReport.setObsceneScore(OBSCENE);
        bullyReport.setInsultScore(INSULT);
        bullyReport.setScore(total);


        Comment comment = new Comment();

        if (total >= 95) {
            comment.setToxicityLabel(ToxicityLabel.SEVERE_TOXIC);
            bullyReport.setLabel(ToxicityLabel.SEVERE_TOXIC);
            reportRepo.save(bullyReport);


        } else if (total >= 85) {
            comment.setToxicityLabel(ToxicityLabel.TOXIC);
            bullyReport.setLabel(ToxicityLabel.TOXIC);
            reportRepo.save(bullyReport);

        } else if (total >= 70) {
            comment.setToxicityLabel(ToxicityLabel.INSULT);
            bullyReport.setLabel(ToxicityLabel.INSULT);
            reportRepo.save(bullyReport);


        } else if (total >= 50) {
            comment.setToxicityLabel(ToxicityLabel.THREAT);
            bullyReport.setLabel(ToxicityLabel.THREAT);
            reportRepo.save(bullyReport);


        } else if (total >= 20) {
            comment.setToxicityLabel(ToxicityLabel.OBSCENE);
            bullyReport.setLabel(ToxicityLabel.OBSCENE);
            reportRepo.save(bullyReport);


        } else if (total >= 1) {
            comment.setToxicityLabel(ToxicityLabel.IDENTITY_HATE);
            bullyReport.setLabel(ToxicityLabel.IDENTITY_HATE);
            reportRepo.save(bullyReport);


        } else {
            comment.setToxicityLabel(ToxicityLabel.NONE);
            bullyReport.setLabel(ToxicityLabel.NONE);
            reportRepo.save(bullyReport);

        }

        commentRepo.save(comment);



        return result;
    }



}
