package net.codjo.tokio.model;
import org.junit.Test;

public class ComparatorTest {

    @Test
    public void test_getReason() throws Exception {
        new AbstractComparator() {
            public boolean isEqual(Object expected, Object actual, int sqlType) {
                return false;
            }


            @Override
            public String getReason() {
                return "Parce que.";
            }
        };
    }
}
