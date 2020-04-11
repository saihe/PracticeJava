import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import static java.util.Objects.isNull;

public class Application {
  public static void main(String[] args) {
    try {
      String url = args.length > 0 ? isNull(args[0]) ? "http://localhost" : args[0] : "http://localhost";
      String param = "{\"data\": \"リクエストしました\"}";
      HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(param);
      wr.flush();
      wr.close();
      int responseCode = con.getResponseCode();
      System.out.println("\nSending 'POST' request to URL : " + url);
      System.out.println("Post parameters : " + param);
      System.out.println("Response Code : " + responseCode);

      BufferedReader in = new BufferedReader(
              new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      //print result
      System.out.println(response.toString());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
