import java.sql.*;
import java.util.Scanner;

public class Search {
    // ==========================================
    // 1. 資料庫連線設定
    // ==========================================
    // 使用 127.0.0.1 強制走 TCP 連線，避免 Socket 權限問題
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456"; // 請確認你的密碼是否正確

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 論壇文章搜尋系統 ===");
        System.out.print("請輸入搜尋關鍵字 (搜尋內容): ");
        String keyword = scanner.nextLine();

        // ==========================================
        // 2. SQL 查詢語法
        // ==========================================
        // 重要修正：欄位名稱改為 author_name (對應剛才重建的表格)
        String sql = "SELECT author_name, topic_content, topic_time FROM topic_data WHERE topic_content LIKE ?";

        try {
            // 載入 MySQL 驅動程式
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立連線與 Statement (使用 try-with-resources 自動關閉連線，不用擔心忘記 close)
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // 3. 設定搜尋參數 (%關鍵字%)
                // 例如輸入 "天氣"，這裡就會變成 "%天氣%"，表示前後不管有什麼字都抓出來
                stmt.setString(1, "%" + keyword + "%");

                // 4. 執行查詢
                try (ResultSet rs = stmt.executeQuery()) {
                    
                    System.out.println("\n--- 搜尋結果 ---");
                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        // 重要修正：這裡取資料的 key 也要改成 author_name
                        String author = rs.getString("author_name");
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
            System.out.println("錯誤：找不到 MySQL Driver！請確認有沒有匯入 mysql-connector-j jar 檔。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("錯誤：資料庫連線失敗！");
            System.out.println("請確認：1. MySQL 有打開嗎？ 2. 欄位名稱對嗎？ 3. 密碼對嗎？");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
