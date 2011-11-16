package net.codjo.tokio.datagenerators;

public class LongGenerator implements Generator<Long> {
    private final int maxDigits;
    private long current = 0;


    public LongGenerator(int maxDigits) {
        this.maxDigits = maxDigits;
    }


    public Long generateValue() {
        checkMaxDigits();

        current++;
        return current;
    }


    private void checkMaxDigits() {
        if (current >= Math.pow(10, maxDigits) - 1) {
            current = 0;
        }
    }
}
