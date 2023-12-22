import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Linux linux = new Linux();

        Scanner scanner = new Scanner(System.in);

        String buffer; //przechowuje komende

        while (true) {
            System.out.printf(linux.getCurrentPath() + ">");
            buffer = scanner.nextLine();
            linux.executeCommand(buffer);

        }
    }
}