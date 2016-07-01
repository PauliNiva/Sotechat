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
 * Luokassa konfiguroidaan <code>JPA</code>-rajapinnan toteuttava Hibernate.
 * Hibernaten tehtavana on luoda SQL-tietokantaan
 * <code>@Entity</code>-annotaatiolla
 * merkittyja Java-luokkia vastaavat tietokantataulut. Hibernaten tehtaviin
 * kuuluu huolehtia tietokantatransaktioista, siis
 * <code>@Entity</code>-luokkia koskevat tietokantaoperaatiot
 * toteutetaan myos varsinaiseen SQL-tietokantaan. Luokkamuuttujat eivat ole
 * kovakoodattuja arvoja, vaan nimia joiden perusteella arvo haetaan
 * <code>application.properties</code>-tiedostoista.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"sotechat.repo"})
@EnableTransactionManagement
public class HibernateConfig {

    /**
     * Ajurin nimi.
     */
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";

    /**
     * Nimi attribuutille, josta salasana loytyy.
     */
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";

    /**
     * Nimi attribuutille, josta SQL-tietokannan osoite loytyy.
     */
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";

    /**
     * Nimi attribuutille, josta kayttajanimi loytyy.
     */
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    /**
     *  Hibernate <code>Dialect</code>.
     */
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT =
            "hibernate.dialect";

    /**
     * Mista <code>EntityManager</code> etsii <code>@Entity</code>-annotaatiolla
     * merkittyja luokkia.
     */
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN =
            "entitymanager.packages.to.scan";

    /**
     * Miten toimitaan palvelimen kaynnistys- ja sammutustilanteessa,
     * esimerkiksi kaynnistystilanteessa tietokanta pystytetaan tai
     * sammutustilanteessa tietokanta tuhotaan.
     */
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO =
            "hibernate.hbm2ddl.auto";

    /**
     * Paketti josta <code>@Entity</code>-luokat loytyvat.
     */
    private static final String PROPERTY_NAME_PACKAGES_TO_SCAN =
            "sotechat.domain";

    /**
     * Tietokantataulujen nimeamistapa.
     */
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
            "hibernate.ejb.naming_strategy";

    /**
     * Naytetaanko komentorivilla tietokantaoperaatiot.
     */
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL =
            "hibernate.show_sql";

    /**
     * Muoto, jossa tietokantaoperaatiot naytetaan. //TODO:plz
     */
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL =
            "hibernate.format_sql";

    /**
     * Tietokantaosoitteen maksimipituus.
     */
    private static final int LENGTH_OF_PRODUCTION_DBURL = 128;


    /**
     * Ymparistoresurssi.
     */
    @Resource
    private Environment env;

    /**
     * Tuotantoprofiili. Luodaan yhteys tuotantotietokantaan.
     *
     * @return Palauttaa tietokantayhteyksista vastaavan
     * <code>HikariDataSource</code>:n.
     * @throws URISyntaxException Jos
     * haettua <code>String</code>:ia ei tunnisteta <code>URI</code>-osoitteeksi.
     */
    @Profile("production")
    @Bean(destroyMethod = "close")
    public HikariDataSource dataSourceForProduction()
            throws URISyntaxException {
        URI dbUri = new URI(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];

        StringBuilder dbUrl = new StringBuilder(LENGTH_OF_PRODUCTION_DBURL);
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
     * Kehitysprofiili. Luodaan yhteys testitietokantaan.
     * <code>HikariDataSource</code> vastaa tietokantayhteyksien yllapitamisesta.
     * @return Palauttaa <code>HikariDataSource</code>-olion.
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
     * Luo <code>EntityManagerFactory</code> olion, joka alustaa
     * <code>@Entity</code> luokat Hibernaten kayttoon.
     *
     * @param dataSource Tietokantayhteyksista huolehtiva olio.
     * @return Palauttaa <code>EntityManagerFactory</code>:n.
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
     * Luo <code>Bean</code>:in, joka huolehtii transaktioista.
     *
     * @param entityManagerFactory Olio, joka sisältää kaikki JPA Entity-oliot. //TODO:fix
     * @return Palautetaan JpaTransactionManager-olio, joka vastaa siitä,
     * että JPA-olioihin tehdyt tietokantaoperaatiot toteutetaan myös
     * tietokantaan.
     */
    @Bean
    JpaTransactionManager transactionManager(
            final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
