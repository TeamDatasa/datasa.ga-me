package datasa.websocket;

import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        System.out.println(" WebSocket handshake 들어옴");

        // ws://localhost:8080/ws?userId=1
        String query = request.getURI().getQuery();
        Long userId = 1L; // default

        if (query != null && query.contains("userId=")) {
            try {
                userId = Long.parseLong(query.split("userId=")[1]);
            } catch (Exception ignored) {}
        }

        attributes.put("userId", userId);
        System.out.println("👉 WS userId = " + userId);

        return true;

    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // 필요하면 로그만
    }


    //나중에 로그인 붙이고
//    private String extractToken(ServerHttpRequest request) {
//        List<String> authHeaders = request.getHeaders().get("Authorization");
//        if (authHeaders == null || authHeaders.isEmpty()) return null;
//
//        String bearer = authHeaders.get(0);
//        if (!bearer.startsWith("Bearer ")) return null;
//
//        return bearer.substring(7);
//    }
}

