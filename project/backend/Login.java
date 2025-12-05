import java.util.Scanner;

public class Login {

    public static void main(String[] args) {

        /* 
           DB 模擬格式：
           0 = username
           1 = email(account)
           2 = password
           3 = role (admin / user)
        */

        String[][] database_array = new String[4][];

        database_array[0] = new String[]{"AdminUser"};       // username
        database_array[1] = new String[]{"admin@test.com"}; // email
        database_array[2] = new String[]{"admin123"};       // password
        database_array[3] = new String[]{"admin"};          // role

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 登入系統 ===");
        System.out.print("請輸入 email: ");
        String inputEmail = scanner.nextLine();

        System.out.print("請輸入密碼: ");
        String inputPassword = scanner.nextLine();

        String dbEmail = database_array[1][0];
        String dbPassword = database_array[2][0];
        String dbUsername = database_array[0][0];
        String dbRole = database_array[3][0];

        if (inputEmail.equals(dbEmail) && inputPassword.equals(dbPassword)) {

            System.out.println("\n登入成功！");
            System.out.println("歡迎 " + dbUsername);

            if (dbRole.equals("admin")) {
                System.out.println("身分：系統管理者 (Admin)");
                // 管理者可做的事情
                System.out.println("你可以進入後台管理頁面。");
            } else {
                System.out.println("身分：一般使用者");
            }

        } else {
            System.out.println("\n登入失敗：帳號或密碼錯誤。");
        }
    }
}
