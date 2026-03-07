package fitnessclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fitnessclub.model.Member;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}