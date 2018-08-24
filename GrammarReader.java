import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class GrammarReader {

    public static Reader readFromFile(String path) throws IOException {

        File toRead = new File(path);
        BufferedReader configuredReader;

        if (!toRead.exists() || toRead.isDirectory()) {
            throw new IOException("File not found!");
        }
        else {
            configuredReader = new BufferedReader(new FileReader(toRead));
        }

        return configuredReader;
    }

}