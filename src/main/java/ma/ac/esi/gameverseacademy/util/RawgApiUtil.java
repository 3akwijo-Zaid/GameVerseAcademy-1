package ma.ac.esi.gameverseacademy.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
public class RawgApiUtil {
    private static final String API_KEY = "c0e04ee5aaaa49e7901bfcbaebf5da56";
    private static final String BASE_URL = "https://api.rawg.io/api/games";
    public static String fetchCoverImage(String gameTitle) {
        if (gameTitle == null || gameTitle.trim().isEmpty()) return null;
        try {
            String encoded = URLEncoder.encode(gameTitle, "UTF-8");
            String urlStr = BASE_URL + "?key=" + API_KEY + "&search=" + encoded + "&page_size=1";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                String json = sb.toString();
                int bgIdx = json.indexOf("\"background_image\":\"");
                if (bgIdx != -1) {
                    int start = bgIdx + 20;
                    int end = json.indexOf("\"", start);
                    if (end != -1) return json.substring(start, end).replace("\\/", "/");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
