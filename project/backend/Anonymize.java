import java.util.Scanner;
import java.util.Random;
import java.sql.*;

public class Anonymize{
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456";
    public static void main(String[] args) {
        String SQL_INSERT = "INSERT INTO comment_data (topic_id, comment_id, content, author_name, created_time) " +
        "VALUES (?, ?, ?, ?, ?)";


        //讀取DB array

        
        long currentTime = System.currentTimeMillis();     //測試random用到時候要拿掉，這邊應該要放在發文那邊，發文後直接透過這個紀錄時間
        int currentTimeSeconds = (int) (currentTime / 1000L);

        Random random = new Random(currentTime);
        int randomid = random.nextInt(100000000);
        System.out.printf("%08d",randomid);
        
        //儲存資料
        String randomid_string =String.format("%08d", randomid);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            //回傳database_array到DB
            stmt.setInt(1, 1); //先自訂    
            stmt.setInt(2, 1);       //先自訂           
            stmt.setString(3, "ANON_ID_INSERT"); 
            stmt.setString(4, randomid_string);  
            stmt.setInt(5, currentTimeSeconds);     

            stmt.executeUpdate();
            
            
            System.out.println("  寫入的匿名 ID: " + randomid);

        } catch (SQLException e) {
            e.printStackTrace();
        
    }

    }
}