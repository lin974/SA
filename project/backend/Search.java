import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchTopic {

    // =========================
    // DB connection
    // =========================
    static class DB {
        static final String URL  = "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC&useSSL=false&characterEncoding=utf8";
        static final String USER = "root";
        static final String PASS = "123456";

        static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASS);
        }
    }

    // =========================
    // Domain model (topic row)
    // =========================
    public static class Topic {
        private final String authorName;
        private final String content;
        private final String time;

        public Topic(String authorName, String content, String time) {
            this.authorName = authorName;
            this.content = content;
            this.time = time;
        }

        public String getAuthorName() { return authorName; }
        public String getContent()    { return content; }
        public String getTime()       { return time; }

        @Override
        public String toString() {
            return "author=" + authorName +
                   ", time=" + time +
                   ", content=" + content;
        }
    }

    // =========================
    // Repository
    // =========================
    public interface TopicSearchRepository {
        List<Topic> searchByKeyword(String keyword);
    }

    public static class MySQLTopicSearchRepository implements TopicSearchRepository {

        @Override
        public List<Topic> searchByKeyword(String keyword) {
            String sql = "SELECT random_name, topic_content, topic_time " +
                         "FROM topic_data WHERE topic_content LIKE ?";

            List<Topic> result = new ArrayList<>();

            try (Connection conn = DB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, "%" + keyword + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String author  = rs.getString("random_name");
                        String content = rs.getString("topic_content");
                        String time    = rs.getString("topic_time");
                        result.add(new Topic(author, content, time));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    // =========================
    // Service
    // =========================
    public interface TopicSearchService {
        List<Topic> search(String keyword);
    }

    public static class TopicSearchServiceImpl implements TopicSearchService {
        private final TopicSearchRepository repo;

        public TopicSearchServiceImpl(TopicSearchRepository repo) {
            this.repo = repo;
        }

        @Override
        public List<Topic> search(String keyword) {
            if (keyword == null || keyword.isBlank()) {
                throw new IllegalArgumentException("keyword must not be empty");
            }
            return repo.searchByKeyword(keyword.trim());
        }
    }
}
