package krm.com.CyberBullingDetection.Repo;

import krm.com.CyberBullingDetection.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment,Long> {
}
