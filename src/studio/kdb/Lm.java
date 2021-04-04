package studio.kdb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lm {
    public static String version = "unknown";
    public static String build = "unknown";
    public static String date = "unknown";

    public static String notes = "Failed to read notes. The latest notes can be found at https://github.com/dzmipt/kdbStudio/blob/master/notes.md";

    private static final Pattern versionPattern = Pattern.compile("\\s*\\<h2\\>\\<code\\>(?<version>.*)\\</code\\>\\s*(?<date>.*)\\</h2\\>\\s*");
    private static final String notesFileName = "notes.html";
    private static final String buildFileName = "build.txt";

    private static final Logger log = LogManager.getLogger();

    static {
        try {
            InputStream inputStream = Lm.class.getClassLoader().getResourceAsStream(notesFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder notesBuilder = new StringBuilder();
            String line;
            boolean versionFound = false;
            while ( (line = reader.readLine())!=null) {
                if (!versionFound) {
                    Matcher matcher = versionPattern.matcher(line);
                    if (matcher.matches()) {
                        version = matcher.group("version");
                        date = matcher.group("date");
                        versionFound = true;
                    }
                }

                notesBuilder.append(line).append("\n");
            }
            notes = notesBuilder.toString();
            inputStream.close();
        } catch (IOException|NullPointerException|IllegalStateException|IllegalArgumentException e) {
            log.error("Can't read version and build date", e);
        }

        try {
            InputStream inputStream = Lm.class.getClassLoader().getResourceAsStream(buildFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            build = reader.readLine();
            inputStream.close();
        } catch (IOException|NullPointerException e) {
            log.error("Can't read build hash", e);
        }
    }

}
