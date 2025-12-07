import java.sql.*;
import java.util.Scanner;

public class Search {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 文章搜尋系統 ===");
        System.out.print("請輸入搜尋關鍵字 (例如: 天氣, Java): ");
        String keyword = scanner.nextLine();

        String url = "jdbc:mysql://127.0.0.1:3306/SA_SQL_BASIC?serverTimezone=UTC";
        String user = "javauser";
        String pass = "123456";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 1. 註冊 Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 建立連線
            conn = DriverManager.getConnection(url, user, pass);

            // 3. 準備 SQL 查詢 (使用 LIKE 做模糊搜尋)
            // 注意：我們只選取 id, title, anon_name, created_at。不選 real_name 以保護隱私。
            String sql = "SELECT id, title, anon_name, created_at FROM topics WHERE title LIKE ?";
            
            stmt = conn.prepareStatement(sql);
            
            // 設定參數 (前後加上 % 代表模糊比對)
            stmt.setString(1, "%" + keyword + "%");

            // 4. 執行查詢
            rs = stmt.executeQuery();

            // 5. 顯示結果
            System.out.println("\n--- 搜尋結果 ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                String author = rs.getString("anon_name"); // 顯示匿名
                String date = rs.getString("created_at");
                
                System.out.println("標題: " + title);
                System.out.println("作者: " + author);
                System.out.println("時間: " + date);
                System.out.println("-----------------------");
            }

            if (!found) {
                System.out.println("找不到包含 \"" + keyword + "\" 的文章。");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！請確認 jar 檔是否已加入。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫連線或查詢錯誤！");
            e.printStackTrace();
        } finally {
            // 6. 關閉資源 (標準寫法需在 finally 關閉以避免記憶體洩漏)
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
