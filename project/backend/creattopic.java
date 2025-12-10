import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private static String generateAnonymousName() {
        StringBuilder sb = new StringBuilder("ANON-");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET[RNG.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    public void postMessage(int userId, String title, String message) {
        String anonymousName = generateAnonymousName();
        saveToDatabase(anonymousName, title, message);
    }

    private void saveToDatabase(String anonymousName, String title, String message) {
        String url = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
        String user = "root";
        String password = "123456";

        String sql = "INSERT INTO posts (username, title, message) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, anonymousName);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // 記得在真實應用中做更完善的錯誤處理
        }
    }
}





