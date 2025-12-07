import java.sql.*;
import java.util.Scanner;

public class Search {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 文章搜尋系統  ===");
        System.out.print("請輸入搜尋關鍵字 : ");
        String keyword = scanner.nextLine();

        String url = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
        String user = "root";   
        String pass = "你的密碼"; 

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);

            String sql = "SELECT topic_id, title, random_name, topic_time FROM topic_data WHERE title LIKE ?";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            rs = stmt.executeQuery();

            System.out.println("\n--- 搜尋結果 ---");
            boolean found = false;

            while (rs.next()) {
                found = true;
                String title = rs.getString("title");
                
                String author = rs.getString("random_name"); 
                String date = rs.getString("topic_time");
                
                System.out.println("標題: " + title);
                System.out.println("作者: " + author);
                System.out.println("時間: " + date);
                System.out.println("-----------------------");
            }

            if (!found) {
                System.out.println("找不到包含 \"" + keyword + "\" 的文章。");
            }

        } catch (Exception e) {
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
