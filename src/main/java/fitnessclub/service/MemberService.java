package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import fitnessclub.model.Member;
import fitnessclub.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repo;

    public Member findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public List<Member> getAllMembers() {
        return repo.findAll();
    }

    public Member get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + id));
    }

    public Member add(Member member) {
        if (member.getEmail() != null && findByEmail(member.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        return repo.save(member);
    }

    public Member update(Long id, Member member) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Member not found: " + id);
        }
        Member sameEmail = findByEmail(member.getEmail());
        if (sameEmail != null && !sameEmail.getId().equals(id)) {
            throw new IllegalArgumentException("Email already exists: " + member.getEmail());
        }
        member.setId(id);
        return repo.save(member);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Member not found: " + id);
        }
        repo.deleteById(id);
    }
}
