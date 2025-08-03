package krm.com.CyberBullingDetection.Dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ContentDto {
    private Long id;
    private String content;
    private UserDto author;
    private UserDto target;

}
