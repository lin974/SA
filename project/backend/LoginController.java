// LoginController.java
@RestController
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String email,
                                     @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        // 模擬資料庫查詢
        String dbEmail = "test@example.com";
        String dbPassword = "1234";
        String dbRealname = "怡臻";

        if (email.equals(dbEmail) && password.equals(dbPassword)) {
            result.put("status", "success");
            result.put("message", "登入成功");
            result.put("realname", dbRealname);
        } else {
            result.put("status", "fail");
            result.put("message", "帳號或密碼錯誤");
        }

        return result;
    }
}
