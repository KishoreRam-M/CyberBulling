package krm.com.CyberBullingDetection.Request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
