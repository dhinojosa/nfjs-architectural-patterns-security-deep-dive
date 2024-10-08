package com.evolutionnext.jwt;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Server {
    private static final String SECRET_KEY = "your_secret_key";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response;
            String token = getAuthorizationToken(t);
            SecretKey key = Jwts.SIG.HS256.key().build();

            if (token == null) {
                token = Jwts.builder().subject("localhost").signWith(key).compact();
                response = String.format("Token issued by %s: %s",
                    System.getenv("SERVER_NAME"), token);
            } else {
                try {
                    var parser = Jwts.parser().verifyWith(key).json(new Deserializer<Map<String, ?>>() {
                        @Override
                        public Map<String, ?> deserialize(byte[] bytes) throws DeserializationException {
                            return Map.of();
                        }

                        @Override
                        public Map<String, ?> deserialize(Reader reader) throws DeserializationException {
                            return Map.of();
                        }
                    }).build().parse(token).accept(Jwt.UNSECURED_CLAIMS);
                    Claims payload = parser.getPayload();
                    LocalDate date = payload.get("date", LocalDate.class);


                    response = String.format("Token received by %s: %s", System.getenv("SERVER_NAME"), claims.getSubject());
                } catch (Exception e) {
                    response = "Invalid token";
                    t.sendResponseHeaders(401, response.length());
                }
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String getAuthorizationToken(HttpExchange exchange) {
            List<String> authHeaders = exchange.getRequestHeaders().get("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                return null;
            }
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return null;
        }
    }
}
