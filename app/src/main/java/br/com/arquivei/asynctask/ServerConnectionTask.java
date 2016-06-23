package br.com.arquivei.asynctask;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import br.com.arquivei.model.DAO;
import br.com.arquivei.model.NotaFiscal;
import cn.pedant.SweetAlert.SweetAlertDialog;



/**
 * Created by henrique on 6/17/2016.
 */
public class ServerConnectionTask extends AsyncTask<Void, Void, String> {
    // Constantes
    private static final String SERVER_URL = "http://easydonate.sa-east-1.elasticbeanstalk.com/database/armazenarNotas.php";
    private static final String LOADING_MESSAGE = "Enviando...";
    private static final String POST_SUCCED = "sucesso";
    private static final String POST_FAILED = "falhou";

    private Context mContext;
    private SweetAlertDialog pDialog; // Dialogo de Progresso
    private serverConnectionListener mListener;
    private ArrayList<NotaFiscal> mNotas;
    private String postData; // String que será enviada

    /* Construtor */
    public ServerConnectionTask(Context c, ArrayList<NotaFiscal> notas, serverConnectionListener listener) {
        this.mContext = c;
        this.mNotas = notas;
        this.mListener = listener;
        pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(LOADING_MESSAGE);
    }


    /* Antes de começar a task prepara o Dialogo de Progresso */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            // Para cada nota fiscal que precisa ser enviada
            for (NotaFiscal temp : mNotas) {
                // Defined URL  where to send data
                URL url = new URL(SERVER_URL);
                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());


                wr.write(getPostData(temp));
                wr.flush();


                /*
                 * Get the server response
                 * Mesmo nao sendo necessário pegar resposta do server, se remover essa parte não irá funcionar,
                 * não sei porque
                 */

                String response = "";
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                response = sb.toString();
                reader.close();
            }

            return POST_SUCCED;

        } catch (Exception e) {
            Log.i("HTTP", "Deu erro!" + e.getMessage());
            return POST_FAILED;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //Update the UI
        if (result == POST_SUCCED) {
            pDialog.setTitleText("Parabéns! Notas enviadas!")
                    .setConfirmText("OK")
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

            DAO dao = DAO.getInstance();
            dao.confirmarNotasEnviadas(mContext);
            mListener.onConnectionFinish(true);
        } else {
            pDialog.setTitleText("Sem Internet!")
                    .setContentText("Conecte-se e tente novamente mais tarde.")
                    .setConfirmText("OK")
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            mListener.onConnectionFinish(false);
        }

    }


    /* Converte a lista de notas fiscais em uma String que será enviada no POST HTTP */
    private String getPostData(NotaFiscal nota) {
        // Create data variable for sent values to server
        String data = "";
        try {

            data = URLEncoder.encode("txtData", "UTF-8")
                    + "=" + URLEncoder.encode(nota.getData(), "UTF-8");

            data += "&" + URLEncoder.encode("txtQRCode", "UTF-8") + "="
                    + URLEncoder.encode(nota.getQRcode(), "UTF-8");

            data += "&" + URLEncoder.encode("txtValor", "UTF-8")
                    + "=" + URLEncoder.encode(nota.getValor(), "UTF-8");

            data += "&" + URLEncoder.encode("txtCNPJ", "UTF-8")
                    + "=" + URLEncoder.encode(nota.getCnpj(), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data;
    }


    public interface serverConnectionListener {
        void onConnectionFinish(boolean result);
    }
}
