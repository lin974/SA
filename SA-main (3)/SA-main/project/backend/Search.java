import java.sql.*;
import java.util.Scanner;

public class Search {
    // 1. 連線設定 (跟你的 Register.java 保持完全一致)
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 論壇文章搜尋系統 ===");
        System.out.print("請輸入搜尋關鍵字 (搜尋內容): ");
        String keyword = scanner.nextLine();

        // 2. SQL 查詢語法
        // 針對 topic_data 表格進行模糊搜尋 (搜尋 topic_content 欄位)
        // 注意：這裡假設你的表格是 topic_data，欄位是 random_name, topic_content, topic_time
        String sql = "SELECT random_name, topic_content, topic_time FROM topic_data WHERE topic_content LIKE ?";

        try {
            // 載入驅動
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立連線與 Statement (使用 try-with-resources 自動關閉)
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // 3. 設定搜尋參數 (%關鍵字%)
                stmt.setString(1, "%" + keyword + "%");

                // 4. 執行查詢
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
