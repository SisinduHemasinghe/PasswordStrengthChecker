public class PasswordChecker {

//    attributes

    private String password;
    private int score;

//    constructor
    public PasswordChecker(String password) {
        this.password = password;
        this.score = 0;
    }

//    methods


    public void checkStrength() {       //check the strength
        score = 0;

        if (password.length() >= 8) {
            score += 20;
        }

        if (password.length() >= 12) {
            score += 20;
        }

        if (password.matches(".*[a-z].*")) {
            score += 10;
        }

        if (password.matches(".*[A-Z].*")) {
            score += 10;
        }

        if (password.matches(".*[0-9].*")) {
            score += 10;
        }

        if (password.matches(".*[^a-zA-Z0-9].*")) {
            score += 20;
        }

        if (DictionaryChecker.isCommonPassword(password)) {
            score -= 30;
        }

        if (hasRepeatedCharacters()) {
            score -= 15;
        }

        if (hasCommonPattern()) {
            score -= 20;
        }

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }
    }

    private boolean hasRepeatedCharacters() {       //checking whether 3 times repeated characteres
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);

            if (c1 == c2 && c2 == c3) {
                return true;
            }
        }

        return false;
    }

    private String estimateCrackTime() {        //display estimated crack time message
        if (score <= 30) {
            return "Seconds to minutes";
        } else if (score <= 60) {
            return "Hours to days";
        } else if (score <= 80) {
            return "Months to years";
        } else {
            return "Many years";
        }
    }

    private String getStrengthLevel() {
        if (score <= 30) {
            return "Weak";
        } else if (score <= 60) {
            return "Medium";
        } else if (score <= 80) {
            return "Strong";
        } else {
            return "Very Strong";
        }
    }

    private boolean hasCommonPattern() {
        String lower = password.toLowerCase();

        return lower.contains("123")
                || lower.contains("abc")
                || lower.contains("246")
                || lower.contains("135")
                || lower.contains("qwerty")
                || lower.contains("password")
                || lower.contains("admin")
                || lower.contains("xyz");
    }

    public void displayResult() {       //display results
        System.out.println("");
        System.out.println("Password Strength Report");
        System.out.println("------------------------");
        System.out.println("Score: " + score + "/100");
        System.out.println("Strength: " + getStrengthLevel());

        System.out.println("");
        System.out.println("Suggestions:");

        if (password.length() < 8) {
            System.out.println("- Use at least 8 characters.");
        }

        if (password.length() < 12) {
            System.out.println("- Use 12 or more characters for better security.");
        }

        if (!password.matches(".*[A-Z].*")) {
            System.out.println("- Add uppercase letters.");
        }

        if (!password.matches(".*[a-z].*")) {
            System.out.println("- Add lowercase letters.");
        }

        if (!password.matches(".*[0-9].*")) {
            System.out.println("- Add numbers.");
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            System.out.println("- Add special symbols like @, #, $, %.");
        }

        if (DictionaryChecker.isCommonPassword(password)) {
            System.out.println("- Avoid common dictionary passwords.");
        }

        if (hasRepeatedCharacters()) {
            System.out.println("- Avoid repeated characters like aaaa or 111.");
        }

        if (hasCommonPattern()) {
            System.out.println("- Avoid common patterns like 1234, abc, or 111.");
        }

        if (score >= 80) {
            System.out.println("- Good password!");
        }

        System.out.println("");
        System.out.println("Estimated crack time: " + estimateCrackTime());
    }



}

