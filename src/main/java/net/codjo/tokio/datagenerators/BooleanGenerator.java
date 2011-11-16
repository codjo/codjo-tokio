package net.codjo.tokio.datagenerators;
import java.util.Random;
/**
 *
 */
public class BooleanGenerator implements Generator<Boolean> {

    public Boolean generateBoolean() {
        return new Random().nextBoolean();
    }


    public Boolean generateValue() {
        return generateBoolean();
    }
}
