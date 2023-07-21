package hyeonjin.calendar.web.session;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    public void createSession(Object value, HttpServletResponse response){
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie((mySessionCookie));
    }

    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null){
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }
        // Arrays.stream(arr) : 배열을 stream으로 바꿔줌.
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }

    public void expire(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null){
            sessionStore.remove(sessionCookie.getValue());
        }
    }
}
