package studio.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReaderWriter {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static class Content {
        private String content;
        private LineEnding lineEnding = null;
        private boolean mixedLineEnding;

        public String getContent() {
            return content;
        }

        public LineEnding getLineEnding() {
            return lineEnding;
        }

        public boolean isMixedLineEnding() {
            return mixedLineEnding;
        }
    }

    public static void write(String fileName, String content, LineEnding lineEnding) throws IOException {
        write(fileName, content, lineEnding, DEFAULT_CHARSET);
    }

    public static void write(String fileName, String content, LineEnding lineEnding, Charset charset) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), charset))  {
            writer.write(content.replace("\n", lineEnding.getChars()));
        } catch (IOException e) {
            throw e;
        }
    }

    public static Content read(String fileName) throws IOException {
        return read(fileName, DEFAULT_CHARSET);
    }

    public static Content read(String fileName, Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        String fileContent = new String(bytes, charset);

        StringBuilder builder = new StringBuilder();
        int unixEndings = 0;
        int winEndings = 0;
        int macEndings = 0;

        boolean wasCR = false;
        int size = fileContent.length();
        int index = 0;
        while(index < size) {
            char ch = fileContent.charAt(index);

            if (wasCR) {
                wasCR = false;
                builder.append('\n');
                if (ch == '\n') {
                    winEndings++;
                } else {
                    macEndings++;
                    continue;
                }
            } else {
                if (ch == '\r') {
                    wasCR = true;
                } else {
                    if (ch == '\n') {
                        unixEndings++;
                    }
                    builder.append(ch);
                }
            }
            index++;
        }
        if (wasCR) {
            builder.append('\n');
            macEndings++;
        }

        Content content = new Content();
        content.content = builder.toString();

        int count = 0;
        if (unixEndings > 0) count++;
        if (winEndings > 0) count++;
        if (macEndings > 0) count++;
        content.mixedLineEnding = count > 1;

        if (count>0) { // if no ending of line; lineEding will be null
            if (unixEndings >= winEndings && unixEndings >= macEndings) content.lineEnding = LineEnding.Unix;
            else if (winEndings >= unixEndings && winEndings >= macEndings) content.lineEnding = LineEnding.Windows;
            else content.lineEnding = LineEnding.MacOS9;
        }

        return content;
    }


}
