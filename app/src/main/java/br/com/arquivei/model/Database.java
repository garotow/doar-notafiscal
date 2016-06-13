package br.com.arquivei.model;

/**
 * Created by henri on 5/13/2016.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Database extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    public static final int DB_VERSION = 3;

    // Database Name
    public static final String DB_NAME = "arquivei.db";
    public static final String TABLE_NAME = "notas_table";

    // Colunas
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "nome";
    public static final String COL_CNPJ = "cnpj";
    public static final String COL_VALOR = "valor";
    public static final String COL_DATA = "data";
    public static final String COL_STATUS = "status";

    // Constructor
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // *****************************************************
    // Funct: onCreate(SQLiteDatabase db)
    // Descr: Called when the database is created for the first time
    // *****************************************************
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_NAME + " (\n" +
                COL_ID + " integer NOT NULL primary key autoincrement,\n" +
                COL_NAME + " TEXT,\n" +
                COL_CNPJ + " TEXT,\n" +
                COL_VALOR + " REAL, \n" +
                COL_DATA + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COL_STATUS + " TEXT " +
                ")";


        db.execSQL(create);
        Log.i("Database", "onCreate Database called: \n" + create);
    }

    // *****************************************************
    // Funct: onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    // Descr: Called when the database needs to be upgraded.
    // *****************************************************
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.i("Database", "onUpgrade Database  called");
    }
}

