package sotechat.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {
        "sotechat.repo"
})
@EnableTransactionManagement
public class HibernateConfig {

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT =
            "hibernate.dialect";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN =
            "entitymanager.packages.to.scan";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO =
            "hibernate.hbm2ddl.auto";

    private static final String PROPERTY_NAME_PACKAGES_TO_SCAN =
            "sotechat.domain";

    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
            "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL =
            "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL =
            "hibernate.format_sql";

    @Resource
    private Environment env;

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(
                env.getRequiredProperty(
                        PROPERTY_NAME_DATABASE_DRIVER));
        dataSourceConfig
                .setJdbcUrl(
                        env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSourceConfig
                .setUsername(
                        env.getRequiredProperty(
                                PROPERTY_NAME_DATABASE_PASSWORD));
        dataSourceConfig
                .setPassword(env.getRequiredProperty(
                        PROPERTY_NAME_DATABASE_PASSWORD));

        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            HikariDataSource dataSource,
            Environment env) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(
                new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(
                PROPERTY_NAME_PACKAGES_TO_SCAN);

        Properties jpaProperties = new Properties();

        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                env.getRequiredProperty(
                    PROPERTY_NAME_HIBERNATE_DIALECT));

        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
                env.getRequiredProperty(
                    PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));

        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
                env.getRequiredProperty(
                        PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
