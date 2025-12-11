import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Search {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 論壇文章搜尋系統 ===");
        System.out.print("請輸入搜尋關鍵字 (搜尋內容): ");
        String keyword = scanner.nextLine();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, "%" + keyword + "%");

                try (ResultSet rs = stmt.executeQuery()) {

                    System.out.println("\n--- 搜尋結果 ---");
                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        String author = rs.getString("random_name");
                        String content = rs.getString("topic_content");
                        String time = rs.getString("topic_time");

                        System.out.println("發文者: " + author);
                        System.out.println("時間:   " + time);
                        System.out.println("內容:   " + content);
                        System.out.println("-----------------------");
                    }

                    if (!found) {
                        System.out.println("找不到包含 \"" + keyword + "\" 的文章。");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫連線錯誤！請確認 topic_data 表格是否存在。");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
