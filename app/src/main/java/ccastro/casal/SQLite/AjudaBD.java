package ccastro.casal.SQLite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ccastro.casal.SQLite.ContracteBD.Client;
import ccastro.casal.SQLite.ContracteBD.Factura;
import ccastro.casal.SQLite.ContracteBD.Producte;
import ccastro.casal.SQLite.ContracteBD.Venta;
import ccastro.casal.SQLite.ContracteBD.Mesa;
import ccastro.casal.SQLite.ContracteBD.Reserva_Cliente;
import ccastro.casal.SQLite.ContracteBD.Treballador;


import static android.content.ContentValues.TAG;


/*****************************************************************************************
 *********************************  CREACIÓ DE BASE DE DADES *****************************
 *****************************************************************************************
 ****************************************************************************************/


/**
 * La classe SQLiteOpenHelper, que és una classe que serveix
 * d’ajuda per gestionar la creació de bases de dades i gestió de versions.
 * AjudaBD  hereta d’aquesta d’SQLiteOpenHelper s'’ocupa d’obrir la base de dades si aquesta existeix
 * o crear-la en cas contrari, i actualitzar-la si és necessari.
 */
public class AjudaBD extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "casalcivic.db";
    private static final int DATABASE_VERSION = 4;

    public AjudaBD(Context con)
    {
        super(con, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Crea les taules a la basse de dades, l'ordre és important, per tal de no crear
     * conflictes amb les claus foranes: primer Treballador, després Serveis i per últim Reserves
     * @param db
     */
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(BD_CREATE_PRODUCTE);
        db.execSQL(BD_CREATE_MESA); // IMPORTANTE EL ORDEN, el cliente tiene mesa favorita
        db.execSQL(BD_CREATE_CLIENT);
        db.execSQL(BD_CREATE_RESERVA_CLIENTE);
        db.execSQL(BD_CREATE_TREBALLADOR);
        db.execSQL(BD_CREATE_VENTA);
        db.execSQL(BD_CREATE_FACTURA);
    }

    public static final String BD_CREATE_MESA = "CREATE TABLE IF NOT EXISTS " + Mesa.NOM_TAULA + "("
            + Mesa._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Mesa.NOMBRE_MESA + " TEXT NOT NULL);";

    public static final String BD_CREATE_RESERVA_CLIENTE = "CREATE TABLE IF NOT EXISTS " + Reserva_Cliente.NOM_TAULA + "("
            + Reserva_Cliente._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Reserva_Cliente.DIA_RESERVADO + " TEXT NOT NULL, "
            + Reserva_Cliente.ASISTENCIA+ " TEXT, "
            + Reserva_Cliente.PAGADO+ " TEXT, "  // CAMBIAR A FACTURADO. SE PAGAN LAS VENTAS NO LAS RESERVAS.
            + Reserva_Cliente.ID_CLIENTE+ " INTEGER NOT NULL, "
            + Reserva_Cliente.ID_MESA+ " INTEGER NOT NULL, "
            + "FOREIGN KEY("+ Reserva_Cliente.ID_CLIENTE+") REFERENCES " + Client.NOM_TAULA +"(" + Client._ID +"),"
            + "FOREIGN KEY("+ Reserva_Cliente.ID_MESA+") REFERENCES " + Mesa.NOM_TAULA +"(" + Mesa._ID +"), "
            + "UNIQUE ("+Reserva_Cliente.ID_CLIENTE+","+Reserva_Cliente.DIA_RESERVADO+") ON CONFLICT IGNORE);";

    public static final String BD_CREATE_CLIENT = "CREATE TABLE IF NOT EXISTS " + Client.NOM_TAULA + "("
            + Client._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Client.NOM_CLIENT + " TEXT NOT NULL, "
            + Client.COGNOMS_CLIENT + " TEXT NOT NULL, "
            + Client.TIPUS_CLIENT + " TEXT NOT NULL, "
            + Client.MESA_FAVORITA+ " INTEGER, "
            + Client.TIPO_PAGO + " TEXT NOT NULL, "
            + Client.TIPO_COMIDA+ " TEXT NOT NULL, "
            + Client.OBSERVACIONS_CLIENT + " TEXT NOT NULL, "
            + "FOREIGN KEY("+ Client.MESA_FAVORITA+") REFERENCES " + Mesa.NOM_TAULA +"(" + Mesa._ID +"), "
            + "UNIQUE ("+Client.NOM_CLIENT+","+Client.COGNOMS_CLIENT+") ON CONFLICT IGNORE);";

    public static final String BD_CREATE_PRODUCTE = "CREATE TABLE IF NOT EXISTS " + Producte.NOM_TAULA + "("
            + Producte._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Producte.NOM_PRODUCTE + " TEXT NOT NULL, "
            + Producte.PREU_PRODUCTE + " TEXT NOT NULL, "
            + Producte.TIPUS_PRODUCTE + " TEXT NOT NULL);";
            // TIPUS DE PRODCUTE 0: CAFE / INFUSIONES  1: REFRESCOS  2: ALIMENTOS

    public static final String BD_CREATE_TREBALLADOR = "CREATE TABLE IF NOT EXISTS " + Treballador.NOM_TAULA + "("
            + Treballador._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Treballador.NOM_TREBALLADOR + " TEXT NOT NULL, "
            + Treballador.COGNOMS_TREBALLADOR + " TEXT NOT NULL, "
            + Treballador.USER_NAME + " TEXT NOT NULL, "
            + Treballador.PASSWORD + " TEXT NOT NULL);";

    public static final String BD_CREATE_FACTURA = "CREATE TABLE IF NOT EXISTS " + Factura.NOM_TAULA + "("
            + Factura._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Factura.ID_PRODUCTE + " INTEGER NOT NULL, "
            + Factura.ID_VENTA + " INTEGER NOT NULL, "
            + Factura.QUANTITAT_PRODUCTE + " INTEGER NOT NULL, "
            + "FOREIGN KEY("+ Factura.ID_PRODUCTE+") REFERENCES " + Client.NOM_TAULA +"(" + Client._ID +"),"
            + "FOREIGN KEY("+ Factura.ID_VENTA+") REFERENCES " + Venta.NOM_TAULA +"(" + Venta._ID +"));";

    public static final String BD_CREATE_VENTA = "CREATE TABLE IF NOT EXISTS " + Venta.NOM_TAULA + "("
            + Venta._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Venta.ID_CLIENT + " INTEGER NOT NULL, "
            + Venta.ID_TREBALLADOR + " INTEGER NOT NULL, "
            + Venta.DATA_VENTA +  " TEXT NOT NULL, "
            + Venta.VENTA_COBRADA + " TEXT NOT NULL, "
            + Venta.HORA_VENTA +   " TEXT NOT NULL, "
            + "FOREIGN KEY("+ Venta.ID_TREBALLADOR+") REFERENCES " + Treballador.NOM_TAULA +"(" + Treballador._ID +"),"
            + "FOREIGN KEY("+ Venta.ID_CLIENT+") REFERENCES " + Client.NOM_TAULA +"(" + Client._ID +"));";


    /**
     * Elimina les taules i les torna a crear.
     * L'ordre és important, primer Venta
     * per tal de no crear un conflicte amb les claus foranes.
     */
    public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
        Log.w(TAG, "Actualitzant Base de dades versió " + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
        db.execSQL("Drop table if exists " + Factura.NOM_TAULA);
        db.execSQL("Drop table if exists " + Venta.NOM_TAULA);
        db.execSQL("Drop table if exists " + Treballador.NOM_TAULA);
        db.execSQL("Drop table if exists " + Mesa.NOM_TAULA);
        db.execSQL("Drop table if exists " + Client.NOM_TAULA);
        db.execSQL("Drop table if exists " + Producte.NOM_TAULA);
        db.execSQL("Drop table if exists " + Reserva_Cliente.NOM_TAULA);

        onCreate(db);
    }

}