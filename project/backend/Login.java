import java.sql.*;
import java.util.Scanner;

public class Login {
    // ==========================================
    // 資料庫連線設定 (與 Register.java 相同)
    // ==========================================
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        System.out.println("=== 登入系統 ===");
        System.out.print("請輸入 email: ");
        String inputEmail = scanner.nextLine();

        System.out.print("請輸入密碼: ");
        String inputPassword = scanner.nextLine();

        // 1. 準備 SQL 查詢語句，根據 email 查找用戶的所有資訊
        // 假設 personaldata 表格包含 realname, email, password 欄位
        String sql = "SELECT realname, email, password FROM personaldata WHERE email = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.prepareStatement(sql);

            // 設置參數：根據輸入的 email 查詢
            stmt.setString(1, inputEmail);

            // 執行查詢
            rs = stmt.executeQuery();

            if (rs.next()) {
                // 資料庫中找到此 email
                String dbRealname = rs.getString("realname");
                String dbPassword = rs.getString("password");
                String dbEmail = rs.getString("email");
                
                // 註：您的 personaldata 表格目前沒有 role 欄位，故暫時不判斷身份
                // 為了模擬 admin/user 邏輯，您可以選擇性在 personaldata 裡加入 role 欄位

                // 2. 驗證密碼 (在實際應用中，這裡應對密碼進行雜湊比對)
                if (inputPassword.equals(dbPassword)) {
                    
                    System.out.println("\n登入成功！");
                    System.out.println("歡迎 " + dbRealname);
                    
                    // 由於 personaldata 沒有 role 欄位，這裡固定顯示一般用戶
                    System.out.println("身分：一般使用者"); 
                    
                    // 如果您想增加管理者功能，請在 personaldata 表格中加入 role 欄位

                } else {
                    // 密碼不正確
                    System.out.println("\n登入失敗：帳號或密碼錯誤。");
                }
            } else {
                // 資料庫中找不到此 email
                System.out.println("\n登入失敗：帳號或密碼錯誤。");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫操作錯誤！請檢查連線設定或 personaldata 表格是否存在。");
            e.printStackTrace();
        } finally {
            // 確保所有資源關閉 (rs, stmt, conn)
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}