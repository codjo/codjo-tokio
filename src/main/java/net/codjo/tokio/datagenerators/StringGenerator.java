package net.codjo.tokio.datagenerators;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
/**
 *
 */
public class StringGenerator implements Generator<String> {
    private static final String STRING_GENERATOR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789";
    private Set<String> stringDictionary = new TreeSet<String>();
    private int length;


    public StringGenerator(int length) {
        this.length = length;
    }


    public StringGenerator() {
        this(1);
    }


    public String generateString(int aLength) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < aLength; i++) {
            int idnex = (int)(Math.random() * STRING_GENERATOR_POOL.length());
            String character = STRING_GENERATOR_POOL.substring(idnex, idnex + 1);
            if (new Random().nextBoolean()) {
                character = character.toUpperCase();
            }
            result.append(character);
        }

        String stringResult = result.toString();
        if (!stringDictionary.add(stringResult)) {
            return generateString(aLength);
        }
        else {
            return stringResult;
        }
    }


    public String generateValue() {
        return generateString(length);
    }
}
