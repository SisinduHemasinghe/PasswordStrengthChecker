import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = ";!@#$%^&*()_-+=<>?";

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length, boolean useUpper, boolean useLower,       //method for generate password
                                  boolean useDigits, boolean useSymbols) {

        StringBuilder pool = new StringBuilder();
        if (useUpper) pool.append(UPPER);
        if (useLower) pool.append(LOWER);
        if (useDigits) pool.append(DIGITS);
        if (useSymbols) pool.append(SYMBOLS);

        if (pool.length() == 0) {
            throw new IllegalArgumentException("Select at least one character type.");
        }

        StringBuilder password = new StringBuilder();

        // guarantee at least one char from each selected category
        if (useUpper) password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        if (useLower) password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        if (useDigits) password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        if (useSymbols) password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        while (password.length() < length) {
            password.append(pool.charAt(random.nextInt(pool.length())));
        }

        // shuffle so the guaranteed chars aren't always at the start
        char[] chars = password.substring(0, length).toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}
