package net.codjo.tokio.datagenerators;
import java.math.BigDecimal;
import java.math.MathContext;

public class NumericGenerator implements Generator<BigDecimal> {
    private final int maxDigits;
    private final int scale;
    private long current = 0;


    public NumericGenerator(int maxDigits, int scale) {
        this.maxDigits = maxDigits;
        this.scale = scale;
    }


    public BigDecimal generateValue() {
        checkMaxDigits();

        current++;
        BigDecimal bigDecimal = new BigDecimal(current);
        bigDecimal = bigDecimal.round(new MathContext(maxDigits));
        bigDecimal = bigDecimal.movePointLeft(scale);
        return bigDecimal;
    }


    private void checkMaxDigits() {
        if (current >= Math.pow(10, maxDigits) - 1) {
            current = 0;
        }
    }
}
