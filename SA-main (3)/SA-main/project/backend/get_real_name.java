import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class get_real_name {

  static class DB {
    static final String URL = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC&useSSL=false&characterEncoding=utf8";
    static final String USER = "root";
    static final String PASS = "123456";

    static Connection getConnection() throws SQLException {
      return DriverManager.getConnection(URL, USER, PASS);
    }
  }

  interface RealNameRepository {
    Optional<String> findAuthorNameByTopicId(int topicId);

    Optional<String> findAuthorNameByCommentId(int topicId, int commentId);
  }

  static class MySQLRealNameRepository implements RealNameRepository {

    @Override
    public Optional<String> findAuthorNameByTopicId(int topicId) {
      String sql = "SELECT author_name FROM topic_data WHERE topic_id = ?";
      try (Connection conn = DB.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, topicId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return Optional.ofNullable(rs.getString("author_name"));
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return Optional.empty();
    }

    @Override
    public Optional<String> findAuthorNameByCommentId(int topicId, int commentId) {
      String sql = "SELECT author_name FROM comment_data WHERE topic_id = ? AND comment_id = ?";
      try (Connection conn = DB.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, topicId);
        ps.setInt(2, commentId);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return Optional.ofNullable(rs.getString("author_name"));
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return Optional.empty();
    }
  }

  interface RealNameService {
    String getTopicAuthor(int topicId);

    String getCommentAuthor(int topicId, int commentId);
  }

  static class RealNameServiceImpl implements RealNameService {
    private final RealNameRepository repo;

    public RealNameServiceImpl(RealNameRepository repo) {
      this.repo = repo;
    }

    @Override
    public String getTopicAuthor(int topicId) {
      if (topicId <= 0)
        throw new IllegalArgumentException("無效的 topicId");

      return repo.findAuthorNameByTopicId(topicId)
          .orElse("未知作者或文章不存在");
    }

    @Override
    public String getCommentAuthor(int topicId, int commentId) {
      if (topicId <= 0 || commentId <= 0)
        throw new IllegalArgumentException("ID 參數不合法");

      return repo.findAuthorNameByCommentId(topicId, commentId)
          .orElse("未知作者或留言不存在");
    }
  }
}