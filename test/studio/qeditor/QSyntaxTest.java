package studio.qeditor;

import org.fife.ui.rsyntaxtextarea.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import studio.qeditor.syntax.QSyntaxParser;

import javax.swing.text.Segment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QSyntaxTest {

    private static QSyntaxParser parser;

    @BeforeAll
    private static void init() {
        parser = new QSyntaxParser();
    }

    private String replaceAll(String src, String... pairs) {
        assert(pairs.length % 2 == 0);
        for (int i = 0; i<pairs.length; i+=2) {
            int index;
            while ( (index = src.indexOf(pairs[i])) != -1) {
                src = src.substring(0, index) + pairs[i+1] + src.substring(index + pairs[i].length());
            }
        }
        return src;
    }

    private String decode(String text) {
        return replaceAll(text, "\\\\", "\\", "\\n", "\n", "\\,", ",");
    }

    private String encode(String text) {
        return replaceAll(text, "\n","\\n");
    }

    private void assertSyntax(String text, String expectedLine) {
        StringBuilder actualLine = new StringBuilder();
        RSTokenMaker tokenMaker = new RSTokenMaker();
        Segment segment = new Segment(text.toCharArray(), 0, 0);
        int offset = 0;
        int tokenType = 0;
        do {
            if (offset > 0) actualLine.append(';');

            int lineEnd = text.indexOf('\n', offset);
            if (lineEnd == -1) lineEnd = text.length();
            segment.offset = offset;
            segment.count = lineEnd - offset;
            Token token = tokenMaker.getTokenList(segment, tokenType, offset);

            boolean firstToken = true;
            while (token != null) {
                tokenType = token.getType();
                RSToken rsToken = RSToken.fromTokenType(tokenType);
                if (rsToken != RSToken.NULL) {
                    if (firstToken) {
                        firstToken = false;
                    } else {
                        actualLine.append(',');
                    }
                    actualLine.append(rsToken.toString().toLowerCase());
                }
                token = token.getNextToken();
            }

            offset = lineEnd + 1;
        } while (offset < text.length());

        assertEquals(expectedLine, actualLine.toString(), "Failure for text: " + encode(text));
    }

    @Test
    public void testSyntaxFromFile() throws IOException {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("syntax.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ( (line = reader.readLine()) != null) {
            if (line.trim().length() == 0) continue;
            if (line.charAt(0)=='#') continue;

            int index = 0;
            for(;;) {
                index = line.indexOf(',',index);
                if (index==0 || line.charAt(index-1)!='\\') break;
                index++;
            }
            String text = line.substring(0, index);
            String expectedLine = line.substring(index+1);

            assertSyntax(decode(text), expectedLine);
        }
        reader.close();
    }

}
