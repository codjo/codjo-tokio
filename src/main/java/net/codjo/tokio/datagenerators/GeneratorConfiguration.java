package net.codjo.tokio.datagenerators;

public class GeneratorConfiguration {
    public static final String GENERATE_NUMERIC = "generateNumeric";
    public static final String GENERATE_INT = "generateInt";
    public static final String GENERATE_STRING = "generateString";
    public static final String GENERATE_DATE = "generateDate";
    public static final String GENERATE_BOOLEAN = "generateBoolean";
    public static final String GENERATE_DATETIME = "generateDateTime";

    private final String name;
    private final String precision;


    public GeneratorConfiguration(String name) {
        this(name, null);
    }


    public GeneratorConfiguration(String name, String precision) {
        this.name = name;
        this.precision = precision;
    }


    public String getName() {
        return name;
    }


    public String getPrecision() {
        return precision;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeneratorConfiguration that = (GeneratorConfiguration)o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (precision != null ? !precision.equals(that.precision) : that.precision != null) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        return result;
    }
}
