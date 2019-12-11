package vgrm.colorcode;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vgrmm on 2019-11-18.
 */

public class RequestOperator extends Thread {

    private int count = 120;

    public interface RequestOperatorListener{
        void success(ModelPost publication, int count);
        void failed(int responseCode);

    }

    private  RequestOperatorListener listener;
    private int responseCode;

    public void setListener(RequestOperatorListener listener) {this.listener = listener; }

    @Override
    public void run(){
        super.run();
        try{
            ModelPost publication = request();
            if(publication!=null) success(publication, count);
            else
                failed(responseCode);

        }catch (IOException e){
            failed(-1);
        }catch (JSONException e){
            failed(-2);
        }
    }

    private ModelPost request() throws IOException, JSONException{
        URL obj = new URL("http://jsonplaceholder.typicode.com/posts");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        responseCode=con.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        InputStreamReader streamReader;

        if(responseCode==200){
            streamReader=new InputStreamReader(con.getInputStream());

        }else
        {
            streamReader = new InputStreamReader(con.getErrorStream());
        }

        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine=in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());

        if(responseCode==200)
            return parsingJsonObject(response.toString());
        else return null;
    }

    public ModelPost parsingJsonObject(String response) throws JSONException{

        JSONArray array = new JSONArray(response);

        ModelPost post = new ModelPost();

        JSONObject object = array.getJSONObject(1);

        count = array.length();

        post.setId(object.optInt("id",0));
        post.setUserId(object.optInt("userId", 0));

        post.setTitle(object.getString("title"));
        post.setBodyText(object.getString("body"));
        return post;

    }

    private void failed(int code){
        if(listener!=null)listener.failed(code);
    }

    private void success(ModelPost publication, int count){
        if(listener!=null) listener.success(publication, count);
    }
}
