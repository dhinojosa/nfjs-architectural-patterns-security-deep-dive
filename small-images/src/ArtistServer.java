import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ArtistServer {
    private static Map<Integer, Artist> artistMap = new HashMap<>();

    static {
        artistMap.put(1, new Artist("Ozzy Osbourne", "1948-12-03", "Rock"));
        artistMap.put(2, new Artist("Etta James", "1938-01-25", "Blues"));
        artistMap.put(3, new Artist("Diana Ross", "1944-03-26", "Pop"));
        artistMap.put(4, new Artist("Hank Williams", "1923-09-17", "Country"));
        artistMap.put(5, new Artist("Beyoncé Knowles", "1981-09-04", "R&B"));
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/artist", new ArtistHandler());
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class ArtistHandler implements HttpHandler {
        private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");

            if (segments.length == 3) {
                try {
                    int id = Integer.parseInt(segments[2]);
                    if (artistMap.containsKey(id)) {
                        Artist artist = artistMap.get(id);
                        String response = mapper.writeValueAsString(artist);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        exchange.getResponseBody().write(response.getBytes());
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } catch (NumberFormatException e) {
                    exchange.sendResponseHeaders(400, -1); // Invalid ID format
                }
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
            exchange.close();
        }
    }

    static class Artist {
        public String name;
        public String birthdate;
        public String genre;

        public Artist(String name, String birthdate, String genre) {
            this.name = name;
            this.birthdate = birthdate;
            this.genre = genre;
        }
    }
}
