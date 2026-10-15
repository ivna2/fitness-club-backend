package fitnessclub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fitnessclub.model.Role;
import fitnessclub.model.User;
import fitnessclub.model.Member;
import fitnessclub.repository.RoleRepository;
import fitnessclub.repository.UserRepository;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    // Администратор не связан с участником клуба (member = null)
    public User createAdminIfNotExist(String adminUsername, String adminPassword) {
        if (userRepo.existsByUsername(adminUsername)) {
            return userRepo.findByUsername(adminUsername).orElseThrow();
        }

        User user = new User();
        user.setUsername(adminUsername);
        user.setPassword(passwordEncoder.encode(adminPassword));
        user.setEnabled(true);

        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
        user.setRoles(Set.of(adminRole));

        return userRepo.save(user);
    }

    public void createRegularUser(String email, String rawPassword) {
        if (userRepo.existsByUsername(email)) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован: " + email);
        }

        Member member = memberService.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("Участник не найден: " + email);
        }

        User user = new User();
        user.setUsername(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setMember(member);

        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));
        user.setRoles(Set.of(userRole));

        userRepo.save(user);
    }
}