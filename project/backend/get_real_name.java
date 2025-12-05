import java.sql.*;

public class get_real_name {

    // ==========================================
    // Part 1: 設定區 (Configuration)
    // ==========================================
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456"; 

    // 要放id
    static final int target_topic_id = 1; 
    static final int target_comment_id = 0; 

    
    
    

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("連線成功！");

          String reveal_name_topic = "SELECT realname FROM topicdata WHERE topic_id = ?";
          String reveal_name_comment = "SELECT realname FROM commentdata WHERE topic_id = ? AND comment_id = ?";
          boolean comment = true;
          String real_name;
          if(comment){
            stmt = conn.prepareStatement(reveal_name_comment);
            stmt.setInt(1, target_topic_id);
            stmt.setInt(2, target_comment_id);


          }else{

          }
            

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 關閉資源
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