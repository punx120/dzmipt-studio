package studio.util;

import org.junit.jupiter.api.Test;
import studio.utils.HistoricalList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HistoricalListTest {

    @Test
    public void depthTest() {
        HistoricalList<String> list = new HistoricalList<>(10);
        assertEquals(10, list.getDepth());

        for (int i=0; i<=10; i++) {
            list.add(""+i);
        }

        assertEquals(10, list.size());
        assertEquals("10", list.get(0));
        assertEquals("1", list.get(9));

        list.setDepth(5);
        assertEquals(5, list.size());
        assertEquals("10", list.get(0));
        assertEquals("6", list.get(4));
    }

    @Test
    public void orderTest() {
        List<String> list = new HistoricalList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        assertEquals(3, list.size());
        assertEquals("3", list.get(0));
        assertEquals("2", list.get(1));
        assertEquals("1", list.get(2));

        list.add("2");
        assertEquals(3, list.size());
        assertEquals("2", list.get(0));
        assertEquals("3", list.get(1));
        assertEquals("1", list.get(2));

        list.add("1");
        assertEquals(3, list.size());
        assertEquals("1", list.get(0));
        assertEquals("2", list.get(1));
        assertEquals("3", list.get(2));

        list.add("1");
        assertEquals(3, list.size());
        assertEquals("1", list.get(0));
        assertEquals("2", list.get(1));
        assertEquals("3", list.get(2));

        list.add("4");
        assertEquals(4, list.size());
        assertEquals("4", list.get(0));
        assertEquals("1", list.get(1));
        assertEquals("2", list.get(2));
        assertEquals("3", list.get(3));
    }

    @Test
    public void otherMethodsTest() {
        List<String> list = new HistoricalList<>();
        list.add("1");
        list.add("2");
        list.set(1,"3");
        assertEquals(2, list.size());
        assertEquals("2", list.get(0));
        assertEquals("3", list.get(1));

        list.remove("3");
        assertEquals(1, list.size());
        assertEquals("2", list.get(0));

        assertThrows(UnsupportedOperationException.class, ()-> list.add(0,"test"));
        list.clear();
        assertEquals(0, list.size());
    }
}
