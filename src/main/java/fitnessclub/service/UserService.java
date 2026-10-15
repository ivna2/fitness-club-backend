package fitnessclub.service;

import fitnessclub.dto.RegisterRequest;
import fitnessclub.model.Member;
import fitnessclub.model.Role;
import fitnessclub.model.User;
import fitnessclub.repository.RoleRepository;
import fitnessclub.repository.UserRepository;
import fitnessclub.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createAdminIfNotExist(String adminUsername, String adminPassword) {
        if (userRepo.existsByUsername(adminUsername)) {
            return;
        }
        if (!PasswordPolicy.isStrong(adminPassword)) {
            throw new IllegalArgumentException("Пароль администратора слишком слабый. " + PasswordPolicy.MESSAGE);
        }
        userRepo.save(newUser(adminUsername, adminPassword, "ROLE_ADMIN", null));
    }

    /** Регистрация участника клуба: создаёт (или находит по email) участника и учётную запись с ролью USER. */
    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepo.existsByUsername(email)) {
            throw new IllegalArgumentException("Пользователь уже зарегистрирован: " + email);
        }
        Member member = memberService.findByEmail(email);
        if (member == null) {
            member = memberService.add(new Member(null, request.name().trim(), email));
        }
        return userRepo.save(newUser(email, request.password(), "ROLE_USER", member));
    }

    private User newUser(String username, String rawPassword, String roleName, Member member) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setMember(member);
        Role role = roleRepo.findByName(roleName)
                .orElseGet(() -> roleRepo.save(new Role(null, roleName)));
        user.setRoles(Set.of(role));
        return user;
    }
}
