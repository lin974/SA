import java.sql.*;
import java.util.Scanner;

public class Register {
    // 連線參數保持不變，因為它們是正確的
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 🎯 修正 1: 提示改為真實姓名 (realname)
        System.out.print("請輸入真實姓名(realname): ");
        String realname = scanner.nextLine(); // 變數名稱改為 realname

        System.out.print("請輸入帳號(email): ");
        String email = scanner.nextLine();

        System.out.print("請輸入密碼(password): ");
        String password = scanner.nextLine();

        // 🎯 修正 2: SQL 語法改為 personaldata 表格，只包含三個欄位
        String sql = "INSERT INTO personaldata (realname, email, password) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // 🎯 修正 3: PreparedStatement 參數設定
                stmt.setString(1, realname); // 設置 realname
                stmt.setString(2, email);
                stmt.setString(3, password);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("\n=== 註冊成功！已寫入 personaldata 表格 ===");
                    System.out.println("Realname: " + realname);
                    System.out.println("Account: " + email);
                    System.out.println("Password: " + password);
                } else {
                    System.out.println("\n=== 註冊失敗！沒有資料被寫入 ===");
                }

            } 
        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！請確認您的 classpath 設定正確。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫操作錯誤！請確認 personaldata 表格已建立！");
            e.printStackTrace();
        } finally {
            scanner.close(); 
        }
    }
}