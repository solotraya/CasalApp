package ccastro.casal.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ccastro.casal.SQLite.ContracteBD.Client;
import ccastro.casal.SQLite.ContracteBD.Factura;
import ccastro.casal.SQLite.ContracteBD.Mesa;
import ccastro.casal.SQLite.ContracteBD.Producte;
import ccastro.casal.SQLite.ContracteBD.Reserva_Cliente;
import ccastro.casal.SQLite.ContracteBD.Treballador;
import ccastro.casal.SQLite.ContracteBD.Venta;


/**
 * @author Carlos Alberto Castro Cañabate
 *
 * Classe per crear la base de dades amb ajuda de la classe AjudaBD
 * i retornar consultes de dades emmagatzemada a la base de dades.
 *
 */
public class DBInterface {
    public static final String TAG = "DBInterface";
    ConsultesSQL consulta=new ConsultesSQL();


    private final Context context;
    private AjudaBD ajuda;
    private SQLiteDatabase bd;

    /**
     * Mètode constructor de la classe DBInterface
     * @param con contexte
     */
    public DBInterface(Context con) {
        this.context = con;
        ajuda = new AjudaBD(context);
    }

    /**
     * Mètode per obrir la bd
     * @return this
     */
    public DBInterface obre() {
        bd = ajuda.getWritableDatabase();
        return this;
    }

    /**
     * Mètode per tancar la base de dades
     */
    public void tanca() {
        ajuda.close();
    }

    /**
     * Mètode per esborrar les taules de la base de dades
     */
    public void Esborra() {
        bd.execSQL("drop table if exists " + Venta.NOM_TAULA + " ;");
        bd.execSQL("drop table if exists " + Client.NOM_TAULA + " ;");
        bd.execSQL("drop table if exists " + Treballador.NOM_TAULA + " ;");
        bd.execSQL("drop table if exists " + Producte.NOM_TAULA + " ;");
        bd.execSQL("Drop table if exists " + Reserva_Cliente.NOM_TAULA);
        bd.execSQL("drop table if exists " + Factura.NOM_TAULA + " ;");
        bd.execSQL("drop table if exists " + Mesa.NOM_TAULA + " ;");
        ajuda.onCreate(bd);
    }

    /**
     * Mètode per retornar tots els clients
     * @return cursor amb els clients a retornar
     */
    public Cursor RetornaTotsElsProductes() {
        return bd.rawQuery(consulta.RetornaTotsElsProductes,null);
    }

    /**
     * Mètode per retornar tots els serveis sense filtrat.
     * @return cursor amb els serveis a retornar
     */
    public Cursor RetornaTotsElsClients() {
        return bd.rawQuery(consulta.RetornaTotsElsClients, null);
    }

    public Cursor RetornaVentes(String data){
        return bd.rawQuery(consulta.RetornaVentes(data),null);
    }

    public Cursor RetornaVentesDataEstatVenta(String estatVenta, String fecha){
        return bd.rawQuery(consulta.RetornaVentesDataEstatVenta(estatVenta,fecha),null);
    }

    public Cursor RetornaFacturaIdCliente(String idCliente){
        return bd.rawQuery(consulta.RetornaFacturaIdCliente(idCliente),null);
    }

    public Cursor RetornaFacturaId_Venta(String idVenta){
        return bd.rawQuery(consulta.RetornaFacturaId_Venta(idVenta),null);
    }

    public Cursor RetornaMesasReservadasData(String data){
        return bd.rawQuery(consulta.RetornaMesasReservadasData(data),null);
    }

    public Cursor RetornaClientsReservadosMesa(String idMesa, String data){
        return bd.rawQuery(consulta.RetornaClientsReservadosMesa(idMesa,data),null);
    }
    public Cursor  RetornaTaulaDefecteClient(String idCliente){
        return bd.rawQuery(consulta.RetornaTaulaDefecteClient(idCliente),null);
    }

    public Cursor RetornaTodasLasMesas() {
        return bd.rawQuery(consulta.RetornaTodasLasMesas,null);
    }

    public Cursor RetornaProductes(String tipusProducte){
        return bd.rawQuery(consulta.RetornaProductes(tipusProducte),null);
    }

    public Cursor verificarLogin(String userName, String password){
        return bd.rawQuery(consulta.verificarLogin(userName,password),null);
    }
    public Cursor AñadirProductoAFactura(String id_cliente, String idProducto){
         bd.rawQuery(consulta.AñadirProductoAFactura(id_cliente,idProducto),null);
         return null;
    }
    public Cursor EncontrarId_VentaFacturaSinPagar(String id_cliente){
        return bd.rawQuery(consulta.EncontrarId_VentaFacturaSinPagar(id_cliente),null);
    }
    public Cursor ObtenirQuantitatProductesFactura(String idVenta){
        return bd.rawQuery(consulta.ObtenirQuantitatProductesFactura(idVenta),null);
    }
    public Cursor ObtenirQuantitatReservesSenseIDVenta(String id_cliente){
        return bd.rawQuery(consulta.ObtenirQuantitatReservesSenseIDVenta(id_cliente),null);

    }
    public Cursor obtenirNumeroDeClients(String cadenaClient){
        return bd.rawQuery(consulta.obtenirNumeroDeClients(cadenaClient),null);
    }

    public Cursor obtenirCuantitatClienteBarraSinPagar(){
        return bd.rawQuery(consulta.obtenirCuantitatClienteBarraSinPagar(),null);
    }
    public void EliminarTotsElsClientsDeBarra(){
        String where = Client.NOM_CLIENT + " = 'Cliente Barra'";
        String[] selection = {};
        bd.delete(Client.NOM_TAULA,where,selection);
    }
    public void ActalitzaEstatVenta(String _id,String estatVenta) {
        Integer idVenta = Integer.parseInt(_id);
        ContentValues valores = new ContentValues();
        valores.put(Venta.VENTA_COBRADA, estatVenta);
        String where = Venta._ID + " = ? ";
        String[] selection = {""+idVenta};
        bd.update(Venta.NOM_TAULA, valores, where, selection);
    }

    public void ActalitzarPagoReservaDiaActual(String _id) {
        Integer idCliente = Integer.parseInt(_id);
        ContentValues valores = new ContentValues();
        valores.put(Reserva_Cliente.PAGADO, "1");
        String where = Reserva_Cliente.ID_CLIENTE + " = ? AND "+Reserva_Cliente.DIA_RESERVADO+" LIKE strftime('%Y %m %d','now')";
        String[] selection = {""+idCliente};
        bd.update(Reserva_Cliente.NOM_TAULA, valores, where, selection);
        Log.d("proba", "Actualitzat");
    }
    public void ActalitzarPagoReservaFecha(String _id,String fecha) {
        Integer idCliente = Integer.parseInt(_id);
        ContentValues valores = new ContentValues();
        valores.put(Reserva_Cliente.PAGADO, "1");
        String where = Reserva_Cliente.ID_CLIENTE + " = ? AND "+Reserva_Cliente.DIA_RESERVADO+" LIKE '"+fecha+"'";
        String[] selection = {""+idCliente};
        bd.update(Reserva_Cliente.NOM_TAULA, valores, where, selection);
        Log.d("proba pagdo: ", fecha);
    }
    public void ActalitzarPagoReservaFecha(String _id) {
        Integer idCliente = Integer.parseInt(_id);
        ContentValues valores = new ContentValues();
        valores.put(Reserva_Cliente.PAGADO, "1");
        String where = Reserva_Cliente.ID_CLIENTE + " = ? ";
        String[] selection = {""+idCliente};
        bd.update(Reserva_Cliente.NOM_TAULA, valores, where, selection);

    }
    public void ActualitzarAsistenciaReserva(String _id, String data) {
        Integer idCliente = Integer.parseInt(_id);
        ContentValues valores = new ContentValues();
        valores.put(Reserva_Cliente.ASISTENCIA, "1");
        String where = Reserva_Cliente.ID_CLIENTE + " = ? AND "+Reserva_Cliente.DIA_RESERVADO+" LIKE '"+data+"'";
        String[] selection = {""+idCliente};
        bd.update(Reserva_Cliente.NOM_TAULA, valores, where, selection);
        Log.d("proba", "Actualitzat");
    }
    public void ActualitzarFechaHoraFactura (Integer idVenta, String data, String hora){

        ContentValues valores = new ContentValues();
        valores.put(Venta.DATA_VENTA, data);
        valores.put(Venta.HORA_VENTA, hora);
        String where = Venta._ID+ " = ?";
        String[] selection = {""+idVenta};
        bd.update(Venta.NOM_TAULA, valores, where, selection);
        Log.d("proba", "Actualitzat");
    }


    /**
     * Mètode per inserir Client
     * @param nom del Client
     * @param cognoms del Client
     * @param tipusClient del Client
     * @return posicio a taula client
     */
    public long InserirClient(String nom, String cognoms, String tipusClient,Integer mesaFavorita,
                              String tipoPago, String tipoComida, String observacions) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Client.NOM_CLIENT, nom);
        initialValues.put(Client.COGNOMS_CLIENT, cognoms);
        initialValues.put(Client.TIPUS_CLIENT, tipusClient);
        initialValues.put(Client.MESA_FAVORITA, mesaFavorita);
        initialValues.put(Client.TIPO_PAGO, tipoPago);
        initialValues.put(Client.TIPO_COMIDA, tipoComida);
        initialValues.put(Client.OBSERVACIONS_CLIENT, observacions);
        return bd.insert(Client.NOM_TAULA, null, initialValues);
    }

    public long InserirTreballador(String nom, String cognoms, String userName, String password) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Treballador.NOM_TREBALLADOR, nom);
        initialValues.put(Treballador.COGNOMS_TREBALLADOR, cognoms);
        initialValues.put(Treballador.USER_NAME, userName);
        initialValues.put(Treballador.PASSWORD, password);
        return bd.insert(Treballador.NOM_TAULA, null, initialValues);
    }

    public long InserirProducte(String nom, String preu, String tipusProducte) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Producte.NOM_PRODUCTE, nom);
        initialValues.put(Producte.PREU_PRODUCTE, preu);
        initialValues.put(Producte.TIPUS_PRODUCTE, tipusProducte);
        return bd.insert(Producte.NOM_TAULA, null, initialValues);
    }
    public long InserirVenta(Integer idClient,Integer idTreballador, String dataVenta, String ventaCobrada, String horaVenta) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Venta.ID_CLIENT, idClient);
        initialValues.put(Venta.ID_TREBALLADOR, idTreballador);
        initialValues.put(Venta.DATA_VENTA, dataVenta);
        initialValues.put(Venta.VENTA_COBRADA, ventaCobrada);
        initialValues.put(Venta.HORA_VENTA, horaVenta);
        return bd.insert(Venta.NOM_TAULA, null, initialValues);
    }
    public long InserirFactura(Integer idProducte, Integer idVenta, Integer quantitatProducte) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Factura.ID_PRODUCTE, idProducte);
        initialValues.put(Factura.ID_VENTA, idVenta);
        initialValues.put(Factura.QUANTITAT_PRODUCTE, quantitatProducte);
        return bd.insert(Factura.NOM_TAULA, null, initialValues);
    }

    public long InserirMesa(String nombreMesa) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Mesa.NOMBRE_MESA, nombreMesa);
        return bd.insert(Mesa.NOM_TAULA, null, initialValues);
    }

    public long InserirReserva_Cliente (String dia_reservado, String asistencia,String pagado, Integer idCliente, Integer idMesa) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(Reserva_Cliente.DIA_RESERVADO,dia_reservado);
        initialValues.put(Reserva_Cliente.ASISTENCIA,asistencia);
        initialValues.put(Reserva_Cliente.PAGADO,pagado);
        initialValues.put(Reserva_Cliente.ID_CLIENTE,idCliente);
        initialValues.put(Reserva_Cliente.ID_MESA,idMesa);
        return bd.insert(Reserva_Cliente.NOM_TAULA, null, initialValues);
    }
}
