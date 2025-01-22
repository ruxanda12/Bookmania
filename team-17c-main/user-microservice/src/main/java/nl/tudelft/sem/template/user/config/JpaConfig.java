package nl.tudelft.sem.template.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"nl.tudelft.sem.template.user.database"})
public class JpaConfig {
}
