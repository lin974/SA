import java.sql.*;
import java.time.LocalDateTime;
import java.security.SecureRandom;

/* ========= DB（如果你們專案已有 DB 類別，請刪掉這段避免重複） ========= */
class DB {
    static final String URL =
            "jdbc:mysql://127.0.0.1:3306/SA_DB?serverTimezone=UTC&useSSL=false&characterEncoding=utf8";
    static final String USER = "root";
    static final String PASS = "123456";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

/* ========= 匿名：每次發文都不同 ========= */
class Anonymize {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    public static String newAnon() {
        StringBuilder sb = new StringBuilder("ANON-");
        for (int i = 0; i < 6; i++) sb.append(ALPH[RNG.nextInt(ALPH.length)]);
        return sb.toString();
    }
}

/* ========= Topic Model ========= */
class Topic {
    public int topicId;
    public final String title;
    public final String content;
    public final int authorId;         // 真實身份對應（內部用）
    public final String authorAnon;    // 當次匿名（公開顯示）
    public final LocalDateTime createdAt;

    public Topic(String title, String content, int authorId, String authorAnon, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorAnon = authorAnon;
        this.createdAt = createdAt;
    }
}

/* ========= Repository ========= */
interface TopicRepository {
    int save(Topic t); // 回傳 topic_id
}

class MySQLTopicRepository implements TopicRepository {
    @Override
    public int save(Topic t) {
        String sql = "INSERT INTO topics(title, content, author_id, author_anon, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.title);
            ps.setString(2, t.content);
            ps.setInt(3, t.authorId);
            ps.setString(4, t.authorAnon);
            ps.setTimestamp(5, Timestamp.valueOf(t.createdAt));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new RuntimeException("無法取得 topic_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("貼文儲存失敗：" + e.getMessage(), e);
        }
    }
}

/* ========= Service（你交付給整合者/網頁 API 的核心入口） ========= */
interface TopicService {
    int createTopic(int userId, String title, String content);
}

class TopicServiceImpl implements TopicService {
    private final TopicRepository repo;
    private static final int TITLE_MAX = 200;
    private static final int CONTENT_MAX = 5000;

    public TopicServiceImpl(TopicRepository repo) {
        this.repo = repo;
    }

    @Override
    public int createTopic(int userId, String title, String content) {
        if (userId <= 0) throw new IllegalArgumentException("請先登入");

        String t = (title == null) ? "" : title.trim();
        String c = (content == null) ? "" : content.trim();

        if (t.isEmpty()) throw new IllegalArgumentException("標題不可空白");
        if (c.isEmpty()) throw new IllegalArgumentException("內容不可空白");
        if (t.length() > TITLE_MAX) throw new IllegalArgumentException("標題太長（上限 " + TITLE_MAX + " 字）");
        if (c.length() > CONTENT_MAX) throw new IllegalArgumentException("內容太長（上限 " + CONTENT_MAX + " 字）");

        // ✅ 每次發文產生新的匿名
        String anon = Anonymize.newAnon();

        Topic newTopic = new Topic(t, c, userId, anon, LocalDateTime.now());
        return repo.save(newTopic);
    }
}
