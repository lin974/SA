import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommentService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private static String generateAnonymousName() {
        StringBuilder sb = new StringBuilder("ANON-");
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHABET[RNG.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    public void addComment(int userId, String realName, String content) {
        String anonymousName = generateAnonymousName();
        saveCommentToDatabase(userId, realName, anonymousName, content);
    }

    private void saveCommentToDatabase(int userId, String realName, String anonymousName, String content) {
        String url = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC";
        String user = "root";
        String password = "123456";

        String sql = "INSERT INTO comments (user_id, real_name, anonymous_name, content) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, realName);
            stmt.setString(3, anonymousName);
            stmt.setString(4, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
