import java.sql.*;

public class get{ // 為了區分，我們換個檔名 FixedQueryTest.java

    // ==========================================
    // Part 1: 設定區 (Configuration)
    // ==========================================
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "123456"; 

   
    static final int TARGET_TOPIC_ID = 1; 
    static final int TARGET_COMMENT_ID = 0; 


    static final String reaveal_name = SELECT realname, FROM personaldata WHERE topic_id = ? AND comment_id = ?";

    