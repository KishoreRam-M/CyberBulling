package krm.com.CyberBullingDetection.Repo;

import krm.com.CyberBullingDetection.Model.BullyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface ReportRepo extends JpaRepository<BullyReport,Long> {
}
