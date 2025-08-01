package krm.com.CyberBullingDetection.Repo;

import krm.com.CyberBullingDetection.Model.VictimProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Victumrepo extends JpaRepository<VictimProfile,Long> {
}
