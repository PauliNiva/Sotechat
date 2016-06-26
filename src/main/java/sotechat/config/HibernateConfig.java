package sotechat.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Luokassa konfiguroidaan JPA-rajapinnan toteuttava Hibernate. Hibernaten
 * tehtävänä on luoda SQL-tietokantaan @Entity-annotaatiolla merkittyjä
 * Java-luokkia vastaavat tietokantataulut. Lisäksi Hibernaten tehtäviin
 * kuuluu huolehtia tietokantatransaktioista, eli, että @Entity-luokkia
 * koskevat tietokantaoperaatiot toteutetaan myös varsinaiseen SQL-
 * tietokantaan.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"sotechat.repo"})
@EnableTransactionManagement
public class HibernateConfig {

    /**
     * Ajuri, jolla saadaan Javasta muodostettua yhteys tietokantaan.
     */
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";

    /**
     * Salasana tietokantayhteyden muodostamiseksi.
     */
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";

    /**
     * Osoite, josta SQL-tietokanta löytyy.
     */
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";

    /**
     * Käyttäjänimi tietokantayhteyden muodostamiseksi.
     */
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    /**
     *  Mitä "dialektia" hibernate käyttää, eli arvo riippuu siitä
     *  käytetäänkö esim. H2-tietokantaa kehitysvaiheessa vai PostgreSql-
     *  tietokantaa tuotannossa
     */
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT =
            "hibernate.dialect";

    /**
     * Mistä EntityManager-bean etsii @Entity-annotaatiolla merkittyjä
     * luokkia.
     */
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN =
            "entitymanager.packages.to.scan";

    /**
     * Miten toimitaan palvelimen käynnistys- ja sammutustilanteessa, voi olla
     * esim. että käynnistystilanteessa tietokanta pystytetään ja sammutus-
     * tilanteessa tietokanta tuhotaan.
     */
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO =
            "hibernate.hbm2ddl.auto";

    /**
     * Mistä Hibernate löytää Entity-luokat.
     */
    private static final String PROPERTY_NAME_PACKAGES_TO_SCAN =
            "sotechat.domain";

    /**
     * Miten SQL-tietokantataulut
     * nimetään @Entity-luokkien nimien pohjalta.
     */
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
            "hibernate.ejb.naming_strategy";

    /**
     * Näytetäänkö komentorivillä tietokantaoperaatiot.
     */
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL =
            "hibernate.show_sql";
    /**
     * Missä muodossa tietokantaoperaatiot näytetään.
     */
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL =
            "hibernate.format_sql";
    /**
     * Määritellään ympäristömuuttuja, josta voidaan hakea esim. salaiset
     * tietokanna kirjautumistiedot.
     */
    @Resource
    private Environment env;

    /**
     * Luodaan yhteys tuotantotietokantaan.
     *
     * @return Palautaa tietokantayhteyksistä vastaavaa HikariDataSourceen.
     * @throws URISyntaxException
     */
    @Profile("production")
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSourceForProduction()
            throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        System.out.println(dbUri.toString());
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];

        StringBuilder dbUrl = new StringBuilder(128);
        dbUrl.append("jdbc:postgresql://")
                .append(dbUri.getHost())
                .append(":")
                .append(dbUri.getPort())
                .append(dbUri.getPath());

        String query = dbUri.getQuery();
        if (null != query && !query.isEmpty()) {
            dbUrl.append("?").append(query);
        }

        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(
                env.getRequiredProperty(
                        PROPERTY_NAME_DATABASE_DRIVER));
        dataSourceConfig
                .setJdbcUrl(dbUrl.toString());
        dataSourceConfig
                .setUsername(username);
        dataSourceConfig
                .setPassword(password);

        return new HikariDataSource(dataSourceConfig);
    }
    /**
     * Luodaan yhteys testitietokantaan. HikariDataSource vastaa
     * tietokantayhteyksien ylläpitämisestä
     * @return Palauttaa tietokantayhteyksien ylläpitäjäolion HikariDataSourcen
     */
    @Profile("development")
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSourceForDevelopment() {
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
                                PROPERTY_NAME_DATABASE_USERNAME));
        dataSourceConfig
                .setPassword(env.getRequiredProperty(
                        PROPERTY_NAME_DATABASE_PASSWORD));

        return new HikariDataSource(dataSourceConfig);
    }

    /**
     * Luodaan EntityManagerFactoryBean eli olio, joka alustaa @Entity-
     * notaatiolla varustetut luokat Hibernaten käyttöön.
     *
     * @param dataSource Tietokantayhteyksistä huolehtiva olio
     * @return Palautetaan EntityManagerFactoryBean
     */
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(
            final HikariDataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(
                new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(
                PROPERTY_NAME_PACKAGES_TO_SCAN);

        Properties jpaProperties = new Properties();

        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                env.getRequiredProperty(
                    PROPERTY_NAME_HIBERNATE_DIALECT));

        jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
                env.getRequiredProperty(
                    PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));

        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
                env.getRequiredProperty(
                        PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));

        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }

    /**
     * Luodaan Bean, jonka tehtävänä on huolehtia transaktioista, eli domain-
     * Service-paketista löytyvien service-luokkien avulla tehdyistä tieto-
     * kantaoperaatioista, kuten tietokantaan talletuksista, ja tietokannasta
     * hauista.
     *
     * @param entityManagerFactory
     * @return
     */
    @Bean
    JpaTransactionManager transactionManager(
            final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
