package com.asniaire.redfox.persistence;

import lombok.Getter;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;
import java.util.Properties;

public class JpaBaseTest {

    private static final String H2_PERSISTENCE_UNIT = "redfox-persistence-unit-tests";

    @Getter private static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void init() throws SQLException {
        silentC3p0Log();
        entityManagerFactory = Persistence.createEntityManagerFactory(H2_PERSISTENCE_UNIT);
    }

    private static void silentC3p0Log() {
        Properties properties = new Properties(System.getProperties());
        properties.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        properties.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(properties);
    }

    @AfterClass
    public static void tearDown(){
        entityManagerFactory.close();
    }

}

