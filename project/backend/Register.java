import java.sql.*;
import java.util.Scanner;

public class Register {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("請輸入使用者名稱(username): ");
        String username = scanner.nextLine();

        System.out.print("請輸入帳號(email): ");
        String email = scanner.nextLine();

        System.out.print("請輸入密碼(password): ");
        String password = scanner.nextLine();

        // MySQL 連線資訊
        String url = "jdbc:mysql://127.0.0.1:3306/SA_SQL_BASIC?serverTimezone=UTC";
        String user = "javauser";
        String pass = "123456";

        try {
            // 註冊 MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立連線
            Connection conn = DriverManager.getConnection(url, user, pass);

            // INSERT 寫入資料庫
            String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, 'user')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("\n=== 註冊成功！已寫入 MySQL ===");
                System.out.println("Username: " + username);
                System.out.println("Account: " + email);
                System.out.println("Password: " + password);
            }

            // 關閉資源
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫連線錯誤！");
            e.printStackTrace();
        }
    }
}
