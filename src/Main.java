import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter password to check: ");
        String password = input.nextLine();

        PasswordChecker checker = new PasswordChecker(password);

        checker.checkStrength();
        checker.displayResult();

        input.close();
    }
}
