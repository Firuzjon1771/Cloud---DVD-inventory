package gr.aegean.dvd.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertyReader {
    private static String dbName;
    private static String dbHost;
    private static String dbPort;
    private static String login;
    private static String pwd;

    private PropertyReader() {
        throw new AssertionError("Utility class. Cannot be instantiated.");
    }

    static {
        loadProperties();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream("service.properties")) {
            if (input != null) {
                props.load(input);
                dbName = getPropertyValue(props, "dbName", "");
                dbHost = getPropertyValue(props, "dbHost", "");
                dbPort = getPropertyValue(props, "dbPort", "");
                login = getPropertyValue(props, "login", "");
                pwd = getPropertyValue(props, "pwd", "");
            } else {
                System.out.println("Sorry, unable to find service.properties");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        overridePropertiesWithEnvironmentVariables();
    }
    
    private static String getPropertyValue(Properties props, String key, String defaultValue) {
        String value = props.getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private static void overridePropertiesWithEnvironmentVariables() {
        try {
            String altDbHost = System.getenv("DB_HOST");
            if (altDbHost != null && !altDbHost.trim().isEmpty()) {
                dbHost = altDbHost;
            }
            String loginVar = System.getenv("DB_USER");
            if (loginVar != null && !loginVar.trim().isEmpty()) {
                login = loginVar;
            }
            String pwdVar = System.getenv("DB_PWD");
            if (pwdVar != null && !pwdVar.trim().isEmpty()) {
                pwd = pwdVar;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDbName() {
        return dbName;
    }

    public static String getDbHost() {
        return dbHost;
    }

    public static void setDbHost(String dbHost) {
        PropertyReader.dbHost = dbHost;
    }

    public static String getDbPort() {
        return dbPort;
    }

    public static String getLogin() {
        return login;
    }

    public static String getPwd() {
        return pwd;
    }
}
