package com.evolutionnext.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class Server {
    private static final String SECRET_KEY = "NmVjYmQ0NzM2Y2NlMjdiOWY1OGQ1ZTg3Zjk3NGY0YmIxNjdiZjQyM2JiYjE4MzYy";
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.createContext("/login", new LoginHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            logger.debug("Received request: {}", httpExchange.getRequestURI());
            String response;
            String token = getAuthorizationToken(httpExchange);
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            String serverName = System.getenv().getOrDefault("SERVER_NAME", "localhost");

            if (token == null) {
                logger.debug("token not found");
                response = "No token provided. Please login.";
                httpExchange.sendResponseHeaders(401, response.length());
                flushResponse(httpExchange, response);
                return;
            } else {
                try {
                    Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                    response = String.format("Token received by %s: Claim for %s, issued at %s, expires at %s%n"
                        , serverName, claims.getSubject(), claims.getIssuedAt(), claims.getExpiration());
                } catch (Exception e) {
                    response = "Invalid token";
                    httpExchange.sendResponseHeaders(401, response.length());
                }
            }
            httpExchange.sendResponseHeaders(200, response.length());
            flushResponse(httpExchange, response);
        }
    }

    private static void flushResponse(HttpExchange httpExchange, String response) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if (!"POST".equals(httpExchange.getRequestMethod())) {
                String response = "Method not allowed";
                httpExchange.sendResponseHeaders(405, response.length());
                flushResponse(httpExchange, response);
                return;
            }

            // Parse JSON input
            InputStream is = httpExchange.getRequestBody();
            JsonNode jsonNode = objectMapper.readTree(is);
            String username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();

            // Simple hardcoded credentials for demonstration
            String validUsername = "user@example.com";
            String validPassword = "password123";

            if (validUsername.equals(username) && validPassword.equals(password)) {
                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                logger.debug("Issuing a token");
                String token = Jwts.builder()
                    .subject("user@example.com")
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusSeconds(3600)))
                    .claim("roles", List.of("admin", "editor"))
                    .signWith(key).compact();

                logger.debug("Set response headers and body");
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.getResponseHeaders().set("Authorization", "Bearer " + token);
                String response = String.format("{\"message\": \"Login successful\", \"token\": \"%s\"}", token);
                httpExchange.sendResponseHeaders(200, response.length());
                logger.debug("Prepared response headers and body");

                logger.debug("Prepare OutputStream for response");
                flushResponse(httpExchange, response);
                logger.debug("OutputStream closed");
                logger.debug("Request completed");
            } else {
                String response = "{\"error\": \"Invalid credentials\"}";
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(401, response.length());
                flushResponse(httpExchange, response);

            }
        }
    }

    private static String getAuthorizationToken(HttpExchange exchange) {
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
