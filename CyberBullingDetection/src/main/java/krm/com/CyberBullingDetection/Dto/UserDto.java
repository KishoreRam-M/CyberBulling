package krm.com.CyberBullingDetection.Dto;

import krm.com.CyberBullingDetection.Model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;


    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setRole(this.role);
        return user;
    }
}
