package krm.com.CyberBullingDetection.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", indexes = {
        @Index(columnList = "isToxic"),
        @Index(columnList = "toxicityLabel")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private User target;

    private boolean isToxic;

    @Enumerated(EnumType.STRING)
    private ToxicityLabel toxicityLabel;

    private double toxicityScore;

    private LocalDateTime analyzedDate = LocalDateTime.now();
}
