package krm.com.CyberBullingDetection.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "victim_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VictimProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User victim;

    private int totalToxicComments;

    private boolean alerted = false;

    private LocalDateTime lastToxicDate;
    @Version
    private Long version;
}
