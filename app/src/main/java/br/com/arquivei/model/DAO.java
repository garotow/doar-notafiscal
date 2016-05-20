package br.com.arquivei.model;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    /*
    Singleton Pattern
     */
    public static DAO sInstance = null;
    /*
    Lista de observers - Observer Pattern
     */
    private static List<DatabaseChangedListener> listeners = new ArrayList<DatabaseChangedListener>();

    public static DAO getInstance(){
        if (sInstance == null)
            sInstance = new DAO();

        return sInstance;
    }

    public boolean insertNota(Context c, NotaFiscal e){
        SQLiteDatabase db = new Database(c).getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(Database.COL_NAME, e.getName());
//        values.put(Database.COL_DESCRIPTION, e.getDescription());
//        values.put(Database.COL_ICON, e.getIconName());
//        values.put(Database.COL_DATE, e.getDateFormat());
//        values.put(Database.COL_DURATION, e.getDuration());

        long flag = db.insert(Database.TABLE_EVENTOS_NAME, null, values);
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

    public ArrayList<NotaFiscal> getAllEvents(Context c){
        ArrayList<NotaFiscal> list = new ArrayList<NotaFiscal>();
        String selectQuery = "SELECT  * FROM " + Database.TABLE_EVENTOS_NAME;
        SQLiteDatabase db = new Database(c).getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotaFiscal nota = new NotaFiscal();
//                evento.setName(cursor.getString(cursor.getColumnIndex(Database.COL_NAME)));
//                evento.setDescription(cursor.getString(cursor.getColumnIndex(Database.COL_DESCRIPTION)));
//                evento.setIconName(cursor.getString(cursor.getColumnIndex(Database.COL_ICON)));
//                evento.setDate(cursor.getString(cursor.getColumnIndex(Database.COL_DATE)));
//                evento.setDuration(cursor.getInt(cursor.getColumnIndex(Database.COL_DURATION)));

                // Adding event to list
                list.add(nota);
            } while (cursor.moveToNext());
        }

        db.close();
        return list;
    }

    public ArrayList<NotaFiscal> getEventsAt(Context c, Date date){
        ArrayList<NotaFiscal> list = new ArrayList<NotaFiscal>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        String formatedDate = dateFormat.format(date);
        String selectQuery = "SELECT * FROM " + Database.TABLE_EVENTOS_NAME + " WHERE " + Database.COL_DATE + " LIKE '" + formatedDate + "%'";
        SQLiteDatabase db = new Database(c).getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Evento evento = new Evento();
//                evento.setName(cursor.getString(cursor.getColumnIndex(Database.COL_NAME)));
//                evento.setDescription(cursor.getString(cursor.getColumnIndex(Database.COL_DESCRIPTION)));
//                evento.setIconName(cursor.getString(cursor.getColumnIndex(Database.COL_ICON)));
//                evento.setDate(cursor.getString(cursor.getColumnIndex(Database.COL_DATE)));
//                evento.setDuration(cursor.getInt(cursor.getColumnIndex(Database.COL_DURATION)));

                // Adding event to list
              //  list.add(evento);
            } while (cursor.moveToNext());
        }

        db.close();
        return list;
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
