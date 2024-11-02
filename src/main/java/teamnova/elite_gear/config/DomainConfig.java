package teamnova.elite_gear.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("teamnova.elite_gear.domain")
@EnableJpaRepositories("teamnova.elite_gear.repos")
@EnableTransactionManagement
public class DomainConfig {
}
