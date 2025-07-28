package krm.com.CyberBullingDetection.Repo;

import krm.com.CyberBullingDetection.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User  findByEmail(String email);

}
