import java.sql.*;
import java.util.Scanner;

public class Search {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 文章搜尋系統 ===");
        System.out.print("請輸入搜尋關鍵字 : ");
        String keyword = scanner.nextLine();

        String url = "jdbc:mysql://127.0.0.1:3306/SA_SQL_BASIC?serverTimezone=UTC";
        String user = "javauser";
        String pass = "123456";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT id, title, anon_name, created_at FROM topics WHERE title LIKE ?";
            
            stmt = conn.prepareStatement(sql);
        
            stmt.setString(1, "%" + keyword + "%");

            rs = stmt.executeQuery();

            System.out.println("\n--- 搜尋結果 ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                String author = rs.getString("anon_name"); 
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
