package br.com.arquivei.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by henrique on 6/21/2016.
 */

public class HttpPostTask extends AsyncTask<String, Void, String> {


    protected String doInBackground(String... urls) {
        try {
            // Create data variable for sent values to server
            String data = URLEncoder.encode("txtData", "UTF-8")
                    + "=" + URLEncoder.encode("Deu certo", "UTF-8");

            data += "&" + URLEncoder.encode("txtQRCode", "UTF-8") + "="
                    + URLEncoder.encode("o32323232i", "UTF-8");

            data += "&" + URLEncoder.encode("txtValor", "UTF-8")
                    + "=" + URLEncoder.encode("Deu certo", "UTF-8");

            data += "&" + URLEncoder.encode("txtCNPJ", "UTF-8")
                    + "=" + URLEncoder.encode("Deu certo", "UTF-8");

            String response = "";
            BufferedReader reader = null;

            // Defined URL  where to send data
            URL url = new URL("http://easydonate.sa-east-1.elasticbeanstalk.com/database/armazenarNotas.php");

            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }

            response = sb.toString();
            reader.close();
            Log.i("HTTP", "Deu certo!");
            return response;

        } catch (Exception ex) {
            Log.i("HTTP", "Deu erro!");
            return "null";
        }
    }

    // String response = what is returned by doInBackground
    protected void onPostExecute(String response) {
    }
}