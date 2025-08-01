package krm.com.CyberBullingDetection.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PerspectiveAPIService {

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

        return result;
    }



}
