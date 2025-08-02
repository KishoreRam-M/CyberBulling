package krm.com.CyberBullingDetection.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bully_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BullyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bully_id", nullable = false)
    private User bully;

    @ManyToOne
    @JoinColumn(name = "victim_id")
    private User victim;

    @Lob
    private String reason;

    @Enumerated(EnumType.STRING)
    private ToxicityLabel label;

    private double score;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "severe_toxicity_score")
    private Double severeToxicityScore;

    @Column(name = "obscene_score")
    private Double obsceneScore;

    @Column(name = "toxicity_score")
    private Double toxicityScore;

    @Column(name = "insult_score")
    private Double insultScore;

    @Column(name = "threat_score")
    private Double threatScore;

    @Column(name = "identity_attack_score")
    private Double identityAttackScore;

    @OneToOne(optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;




    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}