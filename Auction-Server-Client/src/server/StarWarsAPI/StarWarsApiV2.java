package server.StarWarsAPI;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import server.DefaultCard;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;

public class StarWarsApiV2 {

    public static DefaultCard[] getDefaultCards(int maxSize) {
        DefaultCard defaultCard[] = new DefaultCard[maxSize];
        if(maxSize>80){
            Server.logger.log(Level.SEVERE,"Failed to get http response [MAX INDEX = 80]" );
            return new DefaultCard[0];
        }
        try {
            System.out.println("--------HTTP GET RESULTS--------");
            for (int i = 1; i < maxSize + 1 ; ++i) {
                System.out.print(i + ": ");
                defaultCard[i - 1] = getDefaultCard(String.valueOf(i));
            }
        }catch (IOException e) {
            Server.logger.log(Level.SEVERE,"Failed to get http response",e );

        }
        return defaultCard;
    }

    public static DefaultCard getDefaultCard(String i ) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("https://swapi.dev/api/people/" + i+"/");
        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));

        String line = "";
        StringBuilder textView  =new StringBuilder();
        while ((line = rd.readLine()) != null) {
            textView.append(line);
        }

        Gson g = new Gson();
        DefaultCard defaultCard = g.fromJson(textView.toString(), DefaultCard.class);

        System.out.println(defaultCard.toStringV2());
        return  defaultCard;
    }
}
