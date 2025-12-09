import java.sql.*;
import java.util.Scanner;
import java.util.Random; // 1. 匯入 Random 用來產生 ID

public class Register {

    // ✅ 修正 1: 設定移到 main 外面 (類別層級)
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random(); // 準備亂數產生器

        System.out.print("請輸入真實姓名(realname): ");
        String realname = scanner.nextLine();

        System.out.print("請輸入帳號(email): ");
        String email = scanner.nextLine();

        System.out.print("請輸入密碼(password): ");
        String password = scanner.nextLine();

        // ✅ 修正 3: 自動產生隨機 ID (因為資料庫規定這兩個欄位不能是空的)
        int autoTopicId = random.nextInt(100000) + 1;
        int autoCommentId = random.nextInt(100000) + 1;

        // SQL 語法：補上 topic_id, comment_id，並設定 role 預設為 '0'
        String sql = "INSERT INTO personaldata (realname, email, password, role, topic_id, comment_id) VALUES (?, ?, ?, '0', ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ✅ 修正 2: 使用正確的變數名稱 DB_URL...
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, realname);
                stmt.setString(2, email);
                stmt.setString(3, password);
                
                // 填入自動產生的 ID
                stmt.setInt(4, autoTopicId);
                stmt.setInt(5, autoCommentId);

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("\n=== 註冊成功！ ===");
                    System.out.println("Realname: " + realname);
                    System.out.println("系統分配 ID: [" + autoTopicId + ", " + autoCommentId + "]");
                } 
            } 
        } catch (ClassNotFoundException e) {
            System.out.println("找不到 Driver！");
        } catch (SQLException e) {
            System.out.println("資料庫錯誤！可能是 ID 重複，請重試。");
            e.printStackTrace();
        } finally {
            scanner.close(); 
        }
    }
}