package server.StarWarsAPI;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import server.DefaultCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StarWarsApi{
    public static DefaultCard[] getDefaultCards(){
        DefaultCard[] defaultCards = new DefaultCard[10];
        JsonObject jsonObject = null;
        try
        {
            jsonObject = getJsonFromSearchQuery("people");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        JsonArray cardsResult = jsonObject.getAsJsonArray("results");

        for (int i = 0; i < cardsResult.size() && i < defaultCards.length ; i++) {
            defaultCards[i] = castToCard(cardsResult.get(i));
        }

        return defaultCards;
    }

    public static DefaultCard castToCard(JsonElement element){
        JsonObject jsonObject = (JsonObject)element;
        String name = ((JsonElement)jsonObject.get( "name") ).toString();
        String height = ((JsonElement)jsonObject.get( "height") ).toString();
        String skin_color = ((JsonElement)jsonObject.get( "skin_color") ).toString();
        String birth_year = ((JsonElement)jsonObject.get( "birth_year") ).toString();
        String gender = ((JsonElement)jsonObject.get( "gender") ).toString();

        return new DefaultCard(name,height,skin_color,birth_year,gender);
    }

    public static JsonObject getJsonFromSearchQuery(String searchquery) throws IOException {

        HttpGet httpGet = new HttpGet("https://swapi.dev/api/" + searchquery + "/");

        HttpClient httpClient = HttpClientBuilder.create().build();
        httpGet.addHeader("accept", "application/json");

        HttpResponse httpResponse = httpClient.execute(httpGet);

        if (httpResponse.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + httpResponse.getStatusLine().getStatusCode());
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader((httpResponse.getEntity().getContent())));

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(stringBuilder.toString(), JsonObject.class);

        bufferedReader.close();
     //   System.out.println(jsonObject.toString());
        return jsonObject;
    }




}
