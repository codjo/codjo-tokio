package net.codjo.tokio;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
public class AutoCompleteConstants {
    public static final String DEFAULT_NUMBER = "1";
    public static final String DEFAULT_STRING = "Z";
    public static final String DEFAULT_DATE = "1997-07-07";
    public static final String DEFAULT_TIMESTAMP = "1997-07-07 07:07:07.7";
    public static final String DEFAULT_TIME = "00:00:00";
    private static final Map<Integer, String> defaults = new HashMap<Integer, String>();


    static {
        for (AutoCompleteDefautValues autoCompleteDefautValues : AutoCompleteDefautValues.values()) {
            defaults.put(autoCompleteDefautValues.getSqlType(), autoCompleteDefautValues.getDefaultValue());
        }
    }


    private AutoCompleteConstants() {
    }


    enum AutoCompleteDefautValues {
        TINYINT(Types.TINYINT, DEFAULT_NUMBER),
        SMALLINT(Types.SMALLINT, DEFAULT_NUMBER),
        INTEGER(Types.INTEGER, DEFAULT_NUMBER),
        REAL(Types.REAL, DEFAULT_NUMBER),
        FLOAT(Types.FLOAT, DEFAULT_NUMBER),
        DOUBLE(Types.DOUBLE, DEFAULT_NUMBER),
        BIGINT(Types.BIGINT, DEFAULT_NUMBER),
        DECIMAL(Types.DECIMAL, DEFAULT_NUMBER),
        NUMERIC(Types.NUMERIC, DEFAULT_NUMBER),
        BIT(Types.BIT, DEFAULT_NUMBER),
        CHAR(Types.CHAR, DEFAULT_STRING),
        VARCHAR(Types.VARCHAR, DEFAULT_STRING),
        LONGVARCHAR(Types.LONGVARCHAR, DEFAULT_STRING),
        DATE(Types.DATE, DEFAULT_DATE),
        TIME(Types.TIME, DEFAULT_TIME),
        TIMESTAMP(Types.TIMESTAMP, DEFAULT_TIMESTAMP);

        private int sqlType;
        private String defaultValue;


        AutoCompleteDefautValues(int sqlType, String defaultValue) {
            this.sqlType = sqlType;
            this.defaultValue = defaultValue;
        }


        public int getSqlType() {
            return sqlType;
        }


        public String getDefaultValue() {
            return defaultValue;
        }
    }


    public static String getDefaultValue(int sqlType) {
        return defaults.get(sqlType);
    }
}
