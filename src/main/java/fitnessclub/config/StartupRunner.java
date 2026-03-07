package fitnessclub.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import fitnessclub.service.UserService;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
    private final UserService userService;
    private final Environment env;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = env.getProperty("admin.email", "admin@fitness.local");
        String adminPassword = env.getProperty("admin.password", "Admin@123!");

        if (!adminEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Неверный формат email администратора");
        }


        userService.createAdminIfNotExist(adminEmail, adminPassword);
        System.out.println("Админ создан (если отсутствовал): " + adminEmail);
    }
}