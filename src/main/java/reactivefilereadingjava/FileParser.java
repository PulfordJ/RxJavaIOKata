package reactivefilereadingjava;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileParser {

    private String fileName;
    private final File file;

    public FileParser(String fileName) {
        this.fileName = fileName;
        file = new File(fileName);
    }

    public long getLastModifiedTimestamp() {
        return file.lastModified();
    }

    public String readContents() throws IOException {
        return Files.readString(Paths.get(fileName), StandardCharsets.US_ASCII);
    }
}
