package br.com.arquivei.model;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Henrique on 1/26/2016.
 * Classe que manipula os dados do banco de dados
 */
public class DAO {

    private ArrayList<NotaFiscal> todasNotas;

    /* Lista de observers - Observer Pattern */
    private static List<DatabaseChangedListener> listeners = new ArrayList<DatabaseChangedListener>();


    /* Singleton Pattern */
    public static DAO sInstance = null;
    public static DAO getInstance(){
        if (sInstance == null)
            sInstance = new DAO();
        return sInstance;
    }


    public boolean insertNota(Context c, NotaFiscal e){
        SQLiteDatabase db = new Database(c).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Database.COL_QRCODE, e.getQRcode());
        values.put(Database.COL_CNPJ, e.getCnpj());
        values.put(Database.COL_VALOR, e.getValor());
        values.put(Database.COL_DATA, e.getData());
        values.put(Database.COL_STATUS, e.getStatus());

        long flag = db.insert(Database.TABLE_NAME, null, values);
        if ( flag == -1 ){
            Log.i("DatabaseAccessObject", "Erro ao inserir no banco de dados");
            db.close();
            return false;
        } else{
            notifyListeners();
            db.close();
            return true;
        }
    }

    public ArrayList<NotaFiscal> getNotasPendentes(Context c){
        ArrayList<NotaFiscal> aux = new ArrayList<NotaFiscal>();
        SQLiteDatabase db = new Database(c).getReadableDatabase();
        Cursor cursor;

        String selectQuery = "SELECT  * FROM " + Database.TABLE_NAME + " WHERE " + Database.COL_STATUS + " LIKE '" + NotaFiscal.STATUS_PENDING + "'";
        cursor = db.rawQuery(selectQuery, null);

        String qr, valor, cnpj, data, status;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                qr = cursor.getString(cursor.getColumnIndex(Database.COL_QRCODE));
                cnpj = cursor.getString(cursor.getColumnIndex(Database.COL_CNPJ));
                data = cursor.getString(cursor.getColumnIndex(Database.COL_DATA));
                valor = cursor.getString(cursor.getColumnIndex(Database.COL_VALOR));
                status = cursor.getString(cursor.getColumnIndex(Database.COL_STATUS));

                NotaFiscal nota = new NotaFiscal(qr, cnpj, data, valor, status);
                aux.add(nota);
            } while (cursor.moveToNext());
        }

        db.close();
        return aux;
    }

    public ArrayList<NotaFiscal> getNotasEnviadas(Context c){
        ArrayList<NotaFiscal> aux = new ArrayList<NotaFiscal>();
        SQLiteDatabase db = new Database(c).getReadableDatabase();
        Cursor cursor;

        String selectQuery = "SELECT  * FROM " + Database.TABLE_NAME + " WHERE " + Database.COL_STATUS + " LIKE '" + NotaFiscal.STATUS_OK + "'";
        cursor = db.rawQuery(selectQuery, null);

        String valor, cnpj, data, status, qr;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                qr = cursor.getString(cursor.getColumnIndex(Database.COL_QRCODE));
                cnpj = cursor.getString(cursor.getColumnIndex(Database.COL_CNPJ));
                data = cursor.getString(cursor.getColumnIndex(Database.COL_DATA));
                valor = cursor.getString(cursor.getColumnIndex(Database.COL_VALOR));
                status = cursor.getString(cursor.getColumnIndex(Database.COL_STATUS));

                NotaFiscal nota = new NotaFiscal(qr, cnpj, data, valor, status);
                aux.add(nota);
            } while (cursor.moveToNext());
        }

        db.close();
        return aux;
    }

    public int confirmarNotasEnviadas(Context c){
        SQLiteDatabase db = new Database(c).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Database.COL_STATUS, NotaFiscal.STATUS_OK);
        return db.update(Database.TABLE_NAME, values, Database.COL_STATUS + " = '" + NotaFiscal.STATUS_PENDING + "'", null);
    }

    public static void registerDatabaseListener(DatabaseChangedListener newListener){
        listeners.add(newListener);
        Log.i("DAO", "novo observer registrado " + listeners.toString());
    }

    public static void unregisterDatabaseListener(DatabaseChangedListener exListener){
        listeners.remove(exListener);
        Log.i("DAO", "observer unregistrado");
    }

    private void notifyListeners(){
        for (DatabaseChangedListener listener : listeners){
            listener.onDataChanged();
        }
    }

    public interface DatabaseChangedListener {
        void onDataChanged();
    }


}
