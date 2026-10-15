package fitnessclub.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import fitnessclub.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
    private final UserService userService;
    private final Environment env;

    @Override
    public void run(String... args) {
        String adminEmail = env.getProperty("admin.email", "admin@fitness.local");
        String adminPassword = env.getProperty("admin.password", "Admin@123!");

        if (!adminEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Неверный формат email администратора");
        }
        if (!env.containsProperty("admin.password")) {
            log.warn("Используется пароль администратора по умолчанию: задайте admin.password (ADMIN_PASSWORD)");
        }

        userService.createAdminIfNotExist(adminEmail, adminPassword);
        log.info("Админ создан (если отсутствовал): {}", adminEmail);
    }
}
