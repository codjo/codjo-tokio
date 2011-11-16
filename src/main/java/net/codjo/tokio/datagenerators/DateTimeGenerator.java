package net.codjo.tokio.datagenerators;
import java.sql.Timestamp;

public class DateTimeGenerator implements Generator<Timestamp> {
    private long current = 0;


    public Timestamp generateValue() {
        return new Timestamp(current++);
    }
}
