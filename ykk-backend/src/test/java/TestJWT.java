import com.yikekong.YkkApplication;
import com.yikekong.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = YkkApplication.class)
@RunWith(SpringRunner.class)
public class TestJWT {

    private JwtUtil jwtUtil;
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJkOTM2ODY4MC1hNmI2LTRlZmYtOTZlNS05MzY0N2NlOTBmMTIiLCJzdWIiOiIxIiwiaXNzIjoic3lzdGVtIiwiaWF0IjoxNjkxNDIyMzM2LCJleHAiOjE2OTE0MjQxMzZ9.IJRY-4TW2LI2uvl3SiLKPY6hz6C_3-shxXPRbJ3c25g";

    @Test
    public void testCreate(){
        String token = JwtUtil.createJWT(1);
        System.out.println(token);
    }

    @Test
    public void testParse(){
        Integer i = JwtUtil.parseJWT(token, Integer.class);
        System.out.println(i);
    }
}
