package studio.kdb;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lm {
    public static String version = "unknown";
    public static String build = "unknown";
    public static String date = "unknown";

    private final static Pattern versionPattern = Pattern.compile("\\s*`(?<version>.*)`\\s*(?<date>.*)");
    private final static String notesFileName = "notes.md";
    private final static String buildFileName = "build.txt";

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
            System.err.println("Can't read version and build date");
            e.printStackTrace();
        }

        try {
            InputStream inputStream = Lm.class.getClassLoader().getResourceAsStream(buildFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            build = reader.readLine();
            inputStream.close();
        } catch (IOException|NullPointerException e) {
            System.err.println("Can't read build hash");
            e.printStackTrace();
        }
    }

}
