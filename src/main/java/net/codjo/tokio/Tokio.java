package net.codjo.tokio;
import net.codjo.database.common.api.ConnectionMetadata;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import java.sql.SQLException;
@Deprecated
public final class Tokio {
    public static final String JDBC_DRIVER = "tokio.jdbc.driver";
    public static final String JDBC_USER = "tokio.jdbc.user";
    public static final String JDBC_PWD = "tokio.jdbc.pwd";
    public static final String JDBC_CATALOG = "tokio.jdbc.catalog";
    public static final String JDBC_SERVER = "tokio.jdbc.server";
    public static final String JDBC_CHARSET = "tokio.jdbc.charset";
    public static final String JDBC_LANGUAGE = "tokio.jdbc.language";
    public static final String JDBC_BASE = "tokio.jdbc.base";
    public static final String DEFAULT_JDBC_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";


    private Tokio() {
    }


    /**
     * @deprecated use DatabaseFactory.createJdbcFixture() instead.
     */
    @Deprecated
    public static JdbcFixture newJdbcFixture() {
        try {
            ConnectionMetadata connectionMetadata = new DatabaseFactory().createDatabaseHelper()
                  .createApplicationConnectionMetadata();
            return new DatabaseFactory().createJdbcFixture(connectionMetadata);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
