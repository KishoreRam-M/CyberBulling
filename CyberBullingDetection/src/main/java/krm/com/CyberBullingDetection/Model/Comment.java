package krm.com.CyberBullingDetection.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference; // Add this import
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", indexes = {
        @Index(columnList = "isToxic"),
        @Index(columnList = "toxicityLabel")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonBackReference // <-- FIX: This breaks the loop for the author relationship
    private User author;

    @ManyToOne
    @JoinColumn(name = "target_id")
    @JsonBackReference
    private User target;

    private boolean isToxic;

    @Enumerated(EnumType.STRING)
    private ToxicityLabel toxicityLabel;

    private double toxicityScore;

    private LocalDateTime analyzedDate;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private BullyReport bullyReport;


    @PrePersist
    public void prePersist() {
        if (analyzedDate == null) {
            analyzedDate = LocalDateTime.now();
        }
    }
}