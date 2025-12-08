import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;

public class CreateTopic {

    // === 資料庫設定 ===
    // ⚠️ 請確認您的資料庫名稱是 SA_SQL_BASIC 還是 SA_DB
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/SA_SQL_BASIC?serverTimezone=UTC";
    static final String DB_USER = "javauser";
    static final String DB_PASS = "123456";

    static Scanner scanner = new Scanner(System.in);
    
    // 關鍵變數：用來記住現在是誰在使用系統 (模擬網頁 Session)
    static String currentLoginUser = ""; 

    public static void main(String[] args) {
        
        // === 步驟 0: 模擬登入 (對應前端右上角的顯示) ===
        System.out.println("=== 系統啟動 ===");
        System.out.print("請先登入您的真實姓名 (模擬登入): ");
        currentLoginUser = scanner.nextLine();
        System.out.println("歡迎，" + currentLoginUser + "！您現在可以開始發文了。\n");

        while (true) {
            System.out.println("==================================");
            System.out.println("   匿名發文系統 (目前使用者: " + currentLoginUser + ")");
            System.out.println("==================================");
            System.out.println("1. 發布新文章 (包含自動匿名化)");
            System.out.println("2. 文章列表 (一般訪客視角)");
            System.out.println("3. 管理員後台 (查看真實姓名)");
            System.out.println("0. 離開");
            System.out.print("請輸入選項: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createTopic(); // 呼叫發文功能
                    break;
                case "2":
                    readTopic();   // 呼叫讀取功能
                    break;
                case "3":
                    verifyAdmin(); // 呼叫管理員驗證
                    break;
                case "0":
                    System.out.println("系統關閉。");
                    return;
                default:
                    System.out.println("無效輸入");
            }
        }
    }

    // === 功能 1: 發文 + 匿名化 (二合一) ===
    public static void createTopic() {
        System.out.println("\n--- [發布新文章] ---");
        
        // 1. 介面輸入 (只問標題與內容)
        System.out.print("文章標題 (必須): ");
        String title = scanner.nextLine();

        System.out.print("寫下你的想法 (必須): ");
        String content = scanner.nextLine();

        // 2. 系統背景處理 (使用者看不到)
        // [合成點 A] 抓取真實姓名
        String realName = currentLoginUser;
        
        // [合成點 B] 產生匿名代號 (Anonymize)
        String randomName = "User" + new Random().nextInt(999999);

        // 3. 資料庫寫入
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // [合成點 C] 將內容、匿名、真名一次寫入
            String sql = "INSERT INTO topic_data (title, topic_content, random_name, real_name, topic_time) VALUES (?, ?, ?, ?, NOW())";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, randomName); // 寫入匿名
            stmt.setString(4, realName);   // 寫入真名 (隱藏欄位)

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ 發布成功！");
                System.out.println("系統已自動為您匿名為: " + randomName);
            }
        } catch (Exception e) {
            System.out.println("❌ 資料庫錯誤: " + e.getMessage());
        }
    }

    // === 功能 2: 文章列表 (一般人看) ===
    public static void readTopic() {
        System.out.println("\n--- [文章列表] ---");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // 只撈取 random_name，不撈取 real_name
            String sql = "SELECT title, topic_content, random_name, topic_time FROM topic_data ORDER BY topic_time DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("--------------------------------");
                System.out.println("標題: " + rs.getString("title"));
                System.out.println("作者: " + rs.getString("random_name"));
                System.out.println("時間: " + rs.getString("topic_time"));
                System.out.println("[模擬按鈕] 查看真實姓名 -> 🚫 無權限");
            }
            System.out.println("--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === 功能 3: 管理員後台 ===
    public static void verifyAdmin() {
        System.out.print("請輸入管理員密碼 (預設 admin123): ");
        if ("admin123".equals(scanner.nextLine())) {
            adminReadTopic();
        } else {
            System.out.println("❌ 密碼錯誤");
        }
    }

    public static void adminReadTopic() {
        System.out.println("\n--- [管理員模式] ---");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // 管理員可以看到 real_name
            String sql = "SELECT * FROM topic_data ORDER BY topic_time DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("--------------------------------");
                System.out.println("標題: " + rs.getString("title"));
                System.out.println("前台顯示: " + rs.getString("random_name"));
                System.out.println("🛑 真實姓名: " + rs.getString("real_name"));
            }
            System.out.println("--------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
