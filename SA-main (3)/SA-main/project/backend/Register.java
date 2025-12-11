import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class Register {

    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.print("請輸入真實姓名(realname): ");
        String realname = scanner.nextLine();

        System.out.print("請輸入帳號(email): ");
        String email = scanner.nextLine();

        System.out.print("請輸入密碼(password): ");
        String password = scanner.nextLine();

        int autoTopicId = random.nextInt(100000) + 1;
        int autoCommentId = random.nextInt(100000) + 1;

        String sql = "INSERT INTO personaldata (realname, email, password, role, topic_id, comment_id) VALUES (?, ?, ?, '0', ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, realname);
                stmt.setString(2, email);
                stmt.setString(3, password);

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