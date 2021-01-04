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

    private static final Pattern versionPattern = Pattern.compile("\\s*`(?<version>.*)`\\s*(?<date>.*)");
    private static final String notesFileName = "notes.md";
    private static final String buildFileName = "build.txt";

    private static final Logger log = LogManager.getLogger();

    static {
        try {
            InputStream inputStream = Lm.class.getClassLoader().getResourceAsStream(notesFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ( (line = reader.readLine())!=null) {
                Matcher matcher = versionPattern.matcher(line);
                if (matcher.matches()) {
                    version = matcher.group("version");
                    date = matcher.group("date");
                    break;
                }
            }
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
