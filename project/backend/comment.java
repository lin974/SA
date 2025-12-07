import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

/* ========= 匿名：每次留言都不同 ========= */
class Anonymize {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    public static String newAnon() {
        StringBuilder sb = new StringBuilder("ANON-");
        for (int i = 0; i < 6; i++) sb.append(ALPH[RNG.nextInt(ALPH.length)]);
        return sb.toString();
    }
}

/* ========= Comment Model ========= */
class Comment {
    public int commentId;
    public final int topicId;
    public final int userId;          // 真實身份對應（內部用）
    public final String authorAnon;   // 當次匿名（公開顯示）
    public final String content;
    public final LocalDateTime createdAt;

    public Comment(int topicId, int userId, String authorAnon, String content, LocalDateTime createdAt) {
        this.topicId = topicId;
        this.userId = userId;
        this.authorAnon = authorAnon;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(int commentId, int topicId, int userId, String authorAnon, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.topicId = topicId;
        this.userId = userId;
        this.authorAnon = authorAnon;
        this.content = content;
        this.createdAt = createdAt;
    }
}

/* ========= Repository ========= */
interface CommentRepository {
    int save(Comment c);                 // 回傳 comment_id
    List<Comment> findByTopicId(int topicId);
}

class MySQLCommentRepository implements CommentRepository {
    @Override
    public int save(Comment c) {
        String sql = "INSERT INTO comments(topic_id, user_id, author_anon, content, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.topicId);
            ps.setInt(2, c.userId);
            ps.setString(3, c.authorAnon);
            ps.setString(4, c.content);
            ps.setTimestamp(5, Timestamp.valueOf(c.createdAt));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new RuntimeException("無法取得 comment_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("留言儲存失敗：" + e.getMessage(), e);
        }
    }

    @Override
    public List<Comment> findByTopicId(int topicId) {
        String sql = "SELECT comment_id, topic_id, user_id, author_anon, content, created_at " +
                     "FROM comments WHERE topic_id = ? ORDER BY created_at ASC";
        List<Comment> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, topicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                            rs.getInt("comment_id"),
                            rs.getInt("topic_id"),
                            rs.getInt("user_id"),
                            rs.getString("author_anon"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("留言查詢失敗：" + e.getMessage(), e);
        }
    }
}

/* ========= Service（你交付給整合者/網頁 API 的核心入口） ========= */
interface CommentService {
    int addComment(int userId, int topicId, String content);
    List<Comment> listComments(int topicId);
}

class CommentServiceImpl implements CommentService {
    private final CommentRepository repo;

    private static final int MAX_LEN = 500;

    public CommentServiceImpl(CommentRepository repo) {
        this.repo = repo;
    }

    @Override
    public int addComment(int userId, int topicId, String content) {
        if (userId <= 0) throw new IllegalArgumentException("請先登入");
        if (topicId <= 0) throw new IllegalArgumentException("topicId 不合法");

        String c = (content == null) ? "" : content.trim();
        if (c.isEmpty()) throw new IllegalArgumentException("留言不可空白");
        if (c.length() > MAX_LEN) throw new IllegalArgumentException("留言太長（上限 " + MAX_LEN + " 字）");

        String anon = Anonymize.newAnon(); // ✅ 每次留言都不同匿名
        Comment newC = new Comment(topicId, userId, anon, c, LocalDateTime.now());
        return repo.save(newC);
    }

    @Override
    public List<Comment> listComments(int topicId) {
        if (topicId <= 0) throw new IllegalArgumentException("topicId 不合法");
        return repo.findByTopicId(topicId);
    }
}
