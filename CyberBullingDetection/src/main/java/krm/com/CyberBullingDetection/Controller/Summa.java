package krm.com.CyberBullingDetection.Controller;

import krm.com.CyberBullingDetection.Model.Comment;
import krm.com.CyberBullingDetection.Service.PerspectiveAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/detect")
@RestController
public class Summa {
    @Autowired
    PerspectiveAPIService apiService;

    @PostMapping
    public  Map<String,Double> analyseComment(@RequestBody  Comment comment) throws Exception {
         return  apiService.analyzeComment(comment.getContent());





    }
    
}
