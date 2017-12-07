package project.cis350.upenn.edu.wywg;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sanjanasarkar on 4/2/17.
 */

public class WeatherServiceAsync extends AsyncTask<Void, Void, Void> {
    private final Location location;
    private String key = "&APPID=b4e7c1d080829efa7a3a6a9110f3091a";

    public WeatherServiceAsync(Location loc) {
        this.location = loc;
    }

    @Override
    protected Void doInBackground(Void... urls) {
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        Log.v("WeatherServiceAsync", "Inside doInBackground");
        try {
            //http://api.openweathermap.org/data/2.5/weather?lat=20&lon=21&APPID=b4e7c1d080829efa7a3a6a9110f3091a
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" +
                    (int) location.getLatitude() + "&lon=" + (int) location.getLongitude() + key);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String response = stringBuilder.toString();
                JSONObject jsonResult = null;
                Log.v("WeatherServiceAsync Res", response);
                try {

                    jsonResult = new JSONObject(response);
                } catch (JSONException e) {
                    Log.e("WeatherServiceAsync","doInBackground jsonObject from response failed.");
                    e.printStackTrace();
                }

                // parse out the temperature from the JSON result
                double temperature = -1000;
                try {
                    temperature = jsonResult.getJSONObject("main").getDouble("temp");
                } catch (JSONException e) {
                    Log.v("WeatherServiceAsync","doInBackground parse out temperature failed.");
                    e.printStackTrace();
                }
                temperature = ConvertTemperatureToFarenheit(temperature);
                if(temperature < -459.67) {
                    Log.v("WeatherServiceAsync","Illegale Temp, assume not overwritten.");
                    throw new IllegalStateException();
                }
                location.setTemperature(temperature);
            }
            finally{
                urlConnection.disconnect();
                Log.v("WeatherServiceAsync","End of doInBackground");
                return null;
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            Log.e("WeatherService", "Error in doBackground ");
        }
        return null;
    }

    private double ConvertTemperatureToFarenheit(double temperature) {
        return (temperature - 273) * (9/5) + 32;
    }
}
