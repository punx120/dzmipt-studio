package studio.kdb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class KEqualsTest {

    private void testEquals(K.KBase k0, K.KBase k1) {
        assertTrue(k0.equals(k1));
        assertEquals(k0.hashCode(), k1.hashCode());
    }

    private void testNotEquals(K.KBase k0, K.KBase k1) {
        assertFalse(k0.equals(k1));
    }

    @Test
    public void testBoolean() {
        K.KBase kTrue = new K.KBoolean(true);
        K.KBase kFalse = new K.KBoolean (false);
        testEquals(kTrue, kTrue);
        testEquals(kFalse, kFalse);
        testNotEquals(kTrue, kFalse);
    }

    @ParameterizedTest
    @ValueSource(chars = { ' ', 'a', '\n'})
    public void testChar(char value) {
        K.KBase k0 = new K.KCharacter(value);
        K.KBase k1 = new K.KCharacter(value);
        K.KBase kk = new K.KCharacter((char) (value +1));

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

    @ParameterizedTest
    @ValueSource(bytes = { 0, Byte.MAX_VALUE, Byte.MIN_VALUE, -Byte.MAX_VALUE, 10, -1})
    public void testByte(byte value) {
        K.KBase k0 = new K.KByte(value);
        K.KBase k1 = new K.KByte(value);
        K.KBase kk = new K.KByte( (byte) (value +1) );

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

    @ParameterizedTest
    @ValueSource(shorts = { 0, Short.MAX_VALUE, Short.MIN_VALUE, -Short.MAX_VALUE, 10, -1})
    public void testShort(short value) {
        K.KBase k0 = new K.KShort(value);
        K.KBase k1 = new K.KShort(value);
        K.KBase kk = new K.KShort((short) (value +1) );

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, Integer.MAX_VALUE, Integer.MIN_VALUE, -Integer.MAX_VALUE, 10, -1})
    public void testInteger(int value) {
        K.KBase k0 = new K.KInteger(value);
        K.KBase k1 = new K.KInteger(value);
        K.KBase kk = new K.KInteger(value +1 );

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, Long.MAX_VALUE, Long.MIN_VALUE, -Long.MAX_VALUE, 10, -1})
    public void testLong(long value) {
        K.KBase k0 = new K.KLong(value);
        K.KBase k1 = new K.KLong(value);
        K.KBase kk = new K.KLong(value +1 );

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\"", "`", "some String"})
    public void testString(String value) {
        K.KBase k0 = new K.KCharacterVector(value);
        K.KBase k1 = new K.KCharacterVector(value);
        K.KBase kk = new K.KCharacterVector(value.length() == 0 ? " ": ((char) value.charAt(0)+1) + value.substring(1) );

        testEquals(k0, k1);
        testNotEquals(k0, kk);
    }

}
