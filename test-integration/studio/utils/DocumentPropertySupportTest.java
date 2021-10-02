package studio.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseKit;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;

public class DocumentPropertySupportTest {

    private Document createDocument() {
        return new BaseDocument(BaseKit.class, true);
    }

    private static void assertPropertyEvent(DocumentPropertySupport.PropertyEvent actual,
                                            Document source, Object key, Object oldValue, Object newValue) {
        Assertions.assertEquals(source, actual.getSource());
        Assertions.assertEquals(key, actual.getKey());
        Assertions.assertEquals(oldValue, actual.getOldValue());
        Assertions.assertEquals(newValue, actual.getNewValue());
    }

    @Test
    public void test() {
        Object key1 = new Object();
        Object value1 = new Object();
        Object value12 = new Object();
        Object key2 = new Object();
        Object value2 = new Object();
        Object value21 = new Object();
        Object key3 = new Object();
        Object value3 = new Object();


        Document doc = createDocument();
        doc.putProperty(key1, value1);
        Assertions.assertEquals(value1, doc.getProperty(key1));
        Listener listener = new Listener();
        DocumentPropertySupport.addPropertyChangeListener(doc, listener);
        Assertions.assertEquals(value1, doc.getProperty(key1));

        Assertions.assertNull(doc.getProperty(key2));
        doc.putProperty(key2, value2);
        Assertions.assertEquals(1, listener.size());
        Assertions.assertEquals(value2, doc.getProperty(key2));
        assertPropertyEvent(listener.get(0), doc, key2, null, value2);

        doc.putProperty(key2, value21);
        Assertions.assertEquals(2, listener.size());
        Assertions.assertEquals(value21, doc.getProperty(key2));
        assertPropertyEvent(listener.get(1), doc, key2, value2, value21);

        doc.putProperty(key2, value21);
        Assertions.assertEquals(2, listener.size());
        Assertions.assertEquals(value21, doc.getProperty(key2));

        doc.putProperty(key2, null);
        Assertions.assertEquals(3, listener.size());
        Assertions.assertNull(doc.getProperty(key2));
        assertPropertyEvent(listener.get(2), doc, key2, value21, null);

        doc.putProperty(key1, value12);
        Assertions.assertEquals(4, listener.size());
        Assertions.assertEquals(value12, doc.getProperty(key1));
        assertPropertyEvent(listener.get(3), doc, key1, value1, value12);

        DocumentPropertySupport.removePropertyChangeListener(doc, listener);

        doc.putProperty(key3, value3);
        Assertions.assertEquals(4, listener.size());
        Assertions.assertEquals(value3, doc.getProperty(key3));
    }

    static class Listener implements DocumentPropertySupport.PropertyChangeListener {
        private final List<DocumentPropertySupport.PropertyEvent> events = new ArrayList<>();

        @Override
        public void propertyChanged(DocumentPropertySupport.PropertyEvent event) {
            events.add(event);
        }

        int size() {
            return events.size();
        }

        DocumentPropertySupport.PropertyEvent get(int index) {
            return events.get(index);
        }
    }

}
