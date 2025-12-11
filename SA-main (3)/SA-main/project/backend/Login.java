import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Login {

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

        String sql = "SELECT realname, email, password FROM personaldata WHERE email = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, inputEmail);

            rs = stmt.executeQuery();

            if (rs.next()) {

                String dbRealname = rs.getString("realname");
                String dbPassword = rs.getString("password");
                String dbEmail = rs.getString("email");

                if (inputPassword.equals(dbPassword)) {

                    System.out.println("\n登入成功！");
                    System.out.println("歡迎 " + dbRealname);

                    System.out.println("身分：一般使用者");

                } else {

                    System.out.println("\n登入失敗：帳號或密碼錯誤。");
                }
            } else {

                System.out.println("\n登入失敗：帳號或密碼錯誤。");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("找不到 MySQL Driver！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("資料庫操作錯誤！請檢查連線設定或 personaldata 表格是否存在。");
            e.printStackTrace();
        } finally {

            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
                scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}