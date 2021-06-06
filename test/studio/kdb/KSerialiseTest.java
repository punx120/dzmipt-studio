package studio.kdb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@EnabledIfEnvironmentVariable(named = "qTestPort", matches = "[0-9]+")
public class KSerialiseTest {

    private static kx.c c;

    @BeforeAll
    public static void connect() throws kx.c.K4Exception, IOException {
        c = new kx.c("localhost", Integer.parseInt(System.getenv("qTestPort")), "", false);
        c.k(new K.KCharacterVector(".z.pg:{$[x~\"reset\";`.z.pg set value;x]}"));
    }

    @AfterAll
    public static void exit() throws kx.c.K4Exception, IOException {
        c.k(new K.KCharacterVector("reset"));
    }

    private void test(K.KBase k) {
        try {
            K.KBase result = c.k(k);
            assertEquals(k, result);
        } catch (kx.c.K4Exception|IOException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, Long.MIN_VALUE, Long.MAX_VALUE, -Long.MAX_VALUE, 1, -1279875})
    public void testLong(long value) {
        test(new K.KLong(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Test", "Test string", " ", "", "something\nwith new line" })
    public void testString(String value) {
        test(new K.KCharacterVector(value));
    }


}
