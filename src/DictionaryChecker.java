import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryChecker {

    public static boolean isCommonPassword(String password) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("common_password.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                if (password.equalsIgnoreCase(line)) {
                    reader.close();
                    return true;
                }
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Dictionary file not found.");
        }

        return false;
    }
}