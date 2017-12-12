package ccastro.casal;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ccastro.casal.RecyclerView.HeaderAdapterMesa;
import ccastro.casal.RecyclerView.HeaderMesa;
import ccastro.casal.SQLite.ContracteBD;
import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;
import ccastro.casal.Utils.Utilitats;

public class MesaActivity extends AppCompatActivity{
    DBInterface db;

    private final String MENU_NORMAL="0";
    private final String MENU_MITAD="1";
    private final String MENU_CAURTO="2";
    private String tipoPago;
    private Spinner spinnerMesa;
    private LinearLayout layoutVistaMesas;
    private Button buttonnDataInicial, buttonAceptarReserva, buttonVistaMesas, buttonImagenMesas, buttonFechaAnterior, buttonFechaPosterior;
    private ImageView imageViewMesas;
    private String fechaInicio="", fechaFinal="0", fechaInicioConsulta;
    private Integer diaInicio=null, diaFinal = null, mesInicio,mesFinal,añoInicio,añoFinal;
    private ArrayList<String> fechasSeleccionadas;
    private String idCliente,nombreCliente;
    private Integer idMesa;
    private Integer totalDias; // LO USAMOS PARA SABER CUANTOS DIAS TIENE INTRODUCIDO EL ARRAYLIST DE FECHAS SELECCIONADAS
    private Integer quantitat; // LO USAMOS PARA SABER LA CANTIDAD DE DIAS QUE HAY QUE FACTURAR UNA VEZ RESERVADOS LOS DIAS DE MESA.
    private Integer contadorDia; // LO USAMOS PARA EMPEZAR LA CUENTA DEL DIA EN EL MES FINAL, HASTA LLEGAR A DIA FINAL.
    private boolean clientInserit; // LO USAMOS PARA SABER SI HA HABIDO INSERCION DE CLIENTE EN MESA
    private List<Button> buttonsMesas;
    private static final int[] BUTTON_IDS = {
            R.id.buttonLlevar, R.id.mesa1, R.id.mesa2, R.id.mesa3, R.id.mesa4, R.id.mesa5, R.id.mesa6, R.id.mesa7,
            R.id.mesa8,R.id.mesa9, R.id.mesa10, R.id.mesa11, R.id.mesa12,
    };
    boolean fechaInicialEscogida = false, fechaFinalEscogida=false;
    boolean dataFinal = true;
    String taulaPerDefecteClient;
    List<String> clientes = null;
    List<String> clientesSoloNombres = null;
    ArrayAdapter<String> adapterClientes;
    Long resultatInserirClient;
    ListView listViewClientes;
    TextView textViewFechaInicio,textViewTotalClientes,textViewTotalClientesComedor,textViewTotalClientesLlevar, textViewClienteSeleccionado, textViewTextoCliente, textViewFechaFinal, textViewFechaFinalTexto;
    android.support.v7.widget.Toolbar mToolbar;
    private HeaderAdapterMesa headerAdapterMesa;
    private ArrayList<HeaderMesa> myDataset;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private android.widget.SimpleCursorAdapter adapterMesa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesa);
        db = new DBInterface(this);
        fechaInicio = Utilitats.obtenerFechaActual(); // por defecto le metemos la fecha actual (DE HOY)
        fechaInicioConsulta = Utilitats.obtenerFechaActual();
        obtenerAñoMesDiaInicio(fechaInicio);
       // fechaFinal = Utilitats.obtenerFechaActual(); // por defecto le metemos la fecha actual (DE HOY)
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar_mesa);
        buttonsMesas = new ArrayList<Button>();
        iniciarMesas();

        textViewTotalClientesComedor = (TextView) findViewById(R.id.totalClientesComedor);
        textViewFechaInicio = (TextView) findViewById(R.id.fechaInicio);
        textViewTotalClientesLlevar = (TextView) findViewById(R.id.totalClientesLlevar);
        textViewTotalClientes = (TextView) findViewById(R.id.totalClientes);
        buttonImagenMesas = (Button) findViewById(R.id.buttonImagenMesas);
        textViewClienteSeleccionado = (TextView) findViewById (R.id.ClienteSeleccionado);
        textViewTextoCliente = (TextView)findViewById(R.id.TextViewClienteSeleccionado );
        textViewFechaFinal = (TextView) findViewById(R.id.fechaFinal);
        textViewFechaFinalTexto =(TextView) findViewById(R.id.TextViewFechaFinal);
        textViewFechaInicio.setText(Utilitats.getFechaFormatSpain(fechaInicio));
        buttonVistaMesas = (Button) mToolbar.findViewById(R.id.buttonVistaMesas) ;
        buttonAceptarReserva = (Button) mToolbar.findViewById(R.id.buttonAñadirCliente) ;
        buttonnDataInicial = (Button) mToolbar.findViewById(R.id.buttonDataIniciCliente) ;
        buttonFechaAnterior = (Button) findViewById(R.id.buttonFechaAnterior) ;
        buttonFechaPosterior = (Button) findViewById(R.id.buttonFechaPosterior) ;
        buttonFechaAnterior.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String fecha;
                do {
                    obtenerAñoMesDiaInicio(fechaInicioConsulta);
                    if (diaInicio!=1){
                        diaInicio = diaInicio - 1;
                    } else if ( mesInicio==2 || mesInicio==4 || mesInicio==6 || mesInicio==8 ||  mesInicio==9 || mesInicio==11){
                        diaInicio=31; mesInicio--;
                    } else if ( mesInicio==5 || mesInicio==7 || mesInicio==10 || mesInicio==12){
                        diaInicio=30; mesInicio--;
                    } else if (mesInicio==3){
                        if (añoInicio % 4 == 0 && añoInicio % 100 != 0 || añoInicio % 400 == 0) {
                            diaInicio=29; mesInicio--;
                        } else diaInicio=28; mesInicio--;

                    } else if (mesInicio==1){
                        diaInicio = 31;
                        mesInicio = 12;
                        añoInicio = añoInicio -1;
                    }
                    fechaInicioConsulta = añoInicio + " "+ mesInicio + " " + diaInicio;
                    fecha = añoInicio + "-"+ mesInicio + "-" + diaInicio;
                } while (!Utilitats.diaHabil(fecha,Calendar.SUNDAY));
                fechaInicio = fechaInicioConsulta;
                textViewFechaInicio.setText(Utilitats.getFechaFormatSpain(fechaInicioConsulta));
                actualizarRecyclerView();
                headerAdapterMesa.actualitzaRecycler(myDataset);
                dataFinal = false; fechaInicialEscogida = false;
                if (idCliente!=null) dataFinal=true;

            }
        });
        buttonFechaPosterior.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String fecha;
                do {
                    obtenerAñoMesDiaInicio(fechaInicioConsulta);
                    if (mesInicio==4 || mesInicio==6 || mesInicio==9 || mesInicio==11){
                        if (diaInicio!=30){ diaInicio++;
                        } else { diaInicio = 1; mesInicio++; }
                    } else if (mesInicio==1 || mesInicio==3 || mesInicio==5 || mesInicio==7 || mesInicio==8 || mesInicio==10 || mesInicio==12) {
                        if (diaInicio!=31){ diaInicio++; }
                        else {
                            diaInicio = 1;
                            if (mesInicio!=12){ mesInicio++; }
                            else { mesInicio=1; añoInicio++; }
                        }
                    } else if (mesInicio==2){
                        if (añoInicio % 4 == 0 && añoInicio % 100 != 0 || añoInicio % 400 == 0) {
                            if (diaInicio!=29){  diaInicio++; }
                            else { diaInicio =1; mesInicio++; }
                        } else {
                            if (diaInicio!=28){ diaInicio++; }
                            else { diaInicio =1; mesInicio++; }
                        }
                    }
                    fechaInicioConsulta = añoInicio + " "+ mesInicio + " " + diaInicio;
                    fecha = añoInicio + "-"+ mesInicio + "-" + diaInicio;
                } while (!Utilitats.diaHabil(fecha,Calendar.SUNDAY));
                    fechaInicio = fechaInicioConsulta;
                    textViewFechaInicio.setText(Utilitats.getFechaFormatSpain(fechaInicioConsulta));
                    actualizarRecyclerView();
                    headerAdapterMesa.actualitzaRecycler(myDataset);
                    dataFinal=false; fechaInicialEscogida = false;
                    if (idCliente!=null) dataFinal=true;


            }
        });
        buttonnDataInicial.setOnClickListener( new View.OnClickListener(){
                public void onClick(View view) {
                    if (idCliente==null){
                        Toast.makeText(MesaActivity.this, "Selecciona fecha Inicio", Toast.LENGTH_SHORT).show();
                        Calendar mcurrentDate = Calendar.getInstance();
                        int mYear = mcurrentDate.get(Calendar.YEAR);
                        int mMonth = mcurrentDate.get(Calendar.MONTH);
                        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog mDatePicker;
                        mDatePicker = new DatePickerDialog(MesaActivity.this, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                selectedmonth = selectedmonth + 1;
                                fechaInicioConsulta = "" + selectedyear + " " + selectedmonth + " " + selectedday;
                                diaInicio = selectedday; mesInicio = selectedmonth; añoInicio=selectedyear;
                                if (Integer.toString(selectedday).length()==1) {
                                    fechaInicio="" + selectedyear + " " + selectedmonth + " " +0+selectedday;
                                    if (Integer.toString(selectedmonth).length()==1){
                                        fechaInicio="" + selectedyear + " " +0+selectedmonth + " " +0+selectedday;
                                    }
                                } else {
                                    if (Integer.toString(selectedmonth).length()==1){
                                        fechaInicio="" + selectedyear + " " +0+selectedmonth + " " +selectedday;
                                    } else fechaInicio="" + selectedyear + " " + selectedmonth + " " + selectedday;
                                }

                                Log.d("FECHA MOSTRAR",fechaInicio);
                                String dataFormatSpain= Utilitats.getFechaFormatSpain(fechaInicio);
                                textViewFechaInicio.setText(dataFormatSpain);
                                actualizarRecyclerView();
                                headerAdapterMesa.actualitzaRecycler(myDataset);
                                fechaInicialEscogida = true;
                                if (idCliente == null){
                                    textViewClienteSeleccionado.setVisibility(View.GONE);
                                    textViewTextoCliente.setVisibility(View.GONE);
                                    textViewFechaFinalTexto.setVisibility(View.GONE);
                                    textViewFechaFinal.setVisibility(View.GONE);
                                } else dataFinal = true;
                            }
                        }, mYear, mMonth, mDay);
                        mDatePicker.setTitle("Selecciona Fecha");
                        mDatePicker.show();
                    }
                    else {
                        // TODO: SELECCION DE FECHA FINAL!

                            Toast.makeText(MesaActivity.this, "Selecciona fecha Final", Toast.LENGTH_SHORT).show();
                            Calendar currentDate = Calendar.getInstance();
                            int year = currentDate.get(Calendar.YEAR);
                            int month = currentDate.get(Calendar.MONTH);
                            int day = currentDate.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePicker;
                            datePicker = new DatePickerDialog(MesaActivity.this, new DatePickerDialog.OnDateSetListener() {
                                public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                                    month = month + 1;
                                    diaFinal = day; mesFinal = month; añoFinal = year;
                                    if (Integer.toString(day).length()==1) {
                                        fechaFinal = "" + year + " " + month + " " +0+day;
                                        if (Integer.toString(month).length()==1){
                                            fechaFinal = "" + year + " " +0+month + " " +0+day;
                                        }
                                    } else {
                                        if (Integer.toString(month).length()==1){
                                            fechaFinal = "" + year + " " +0+month + " " +day;
                                        } else fechaFinal = "" + year + " " + month + " " + day;
                                    }

                                    textViewFechaFinal.setText(Utilitats.getFechaFormatSpain(fechaFinal));
                                    textViewFechaFinal.setVisibility(View.VISIBLE);
                                    textViewFechaFinalTexto.setVisibility(View.VISIBLE);
                                    fechaFinalEscogida=true;

                                }
                            }, year, month, day);
                            datePicker.setTitle("Selecciona Fecha");
                            datePicker.show();
                    }
                }
            }
        );

        listViewClientes = (ListView)findViewById(R.id.listViewClientes);
        clientes= new ArrayList();
        clientesSoloNombres = new ArrayList();
        adapterClientes = new ArrayAdapter<String> (this,
                android.R.layout.simple_list_item_1, android.R.id.text1, clientesSoloNombres);
        // Assign adapter to ListView
        listViewClientes.setAdapter(adapterClientes);


        listViewClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO ESCONDEMOS EL TECLADO DEL MOVIL:
                view = MesaActivity.this.getCurrentFocus();
                view.clearFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                String nombre =(String) listViewClientes.getItemAtPosition(position);
                for (String client: clientes){
                    if (client.contains(nombre)){
                    nombre = client;
                    break;
                    }
                }
                String [] cogerIDCliente = nombre.split(" ");
                idCliente = cogerIDCliente[0];
                String [] cogerTipoPago = nombre.split(":");
                tipoPago = cogerTipoPago[1];
                nombreCliente = cogerTipoPago[0].split(" ",2)[1];
                textViewClienteSeleccionado.setText(nombreCliente);

                textViewTextoCliente.setVisibility(View.VISIBLE);
                textViewClienteSeleccionado.setVisibility(View.VISIBLE);
                Log.d("NOMBRE CLIENTE: ",nombreCliente);
                // TODO CONSULTA PARA CONSEGUIR LA MESA POR DEFECTO DEL CLIENTE
                obtenirTaulaDefecteClient();

                // TODO AHORA ESTARIA GENIA QUE DESPUES DE TENER EL ID CLIENTE
                nombreCliente = cogerIDCliente[1];

                Toast.makeText(view.getContext(), cogerIDCliente[0], Toast.LENGTH_SHORT).show();
                listViewClientes.setVisibility(View.GONE);
                // TODO Y SELECCIONAR MESA FAVORITA DE ESE CLIENTE EN EL SPINNER DE MESA
                spinnerMesa.setSelection(Integer.parseInt(taulaPerDefecteClient));
                adapterMesa.notifyDataSetChanged();
            }
        });


        /**
         * FALTA AHORA QUE HAGA RESERVA DE MESA A LOS CLIENTES DESDE FECHA INICIAL A FECHA FINAL. EXCEPTO FINES DE SEMANA
         * HAY QUE INTENTAR CONTROLARLO, Y SINO HACERLO DE MAXIMO 5 EN 5. EL LIMITE DE SELECCION
         * HABRA QUE CAMBIARLO
         */



        // TODO ESTO ES PARA EL BOTON DE AÑADIR RESERVA
        buttonAceptarReserva.setOnClickListener( new View.OnClickListener(){
             @Override
             public void onClick(View view) {
                 // TODO SI NO SE AÑADE FECHA LE PONEMOS FECHA ACTUAL

                 if (idCliente!=null ){
                     Integer fechaActualInt = Integer.parseInt(Utilitats.obtenerFechaActual().replaceAll("\\s",""));


                     Integer fechaInicialInt;
                     if (fechaInicialEscogida) fechaInicialInt = Integer.parseInt(fechaInicio.replaceAll("\\s",""));
                     else {
                         fechaInicialInt = obtenerFechaInicioNumerico();  // Nos sirve para comparar con fecha final
                     }
                     if (fechaFinalEscogida){
                         Integer fechaFinalInt = Integer.parseInt(fechaFinal.replaceAll("\\s",""));
                     } else {
                         fechaFinal = fechaInicio;
                     }
                     Integer fechaFinalInt = Integer.parseInt(fechaFinal.replaceAll("\\s",""));
                     Log.d("FECHA INICIAL",Integer.toString(fechaInicialInt));
                     Log.d("FECHA FINAL",Integer.toString(fechaFinalInt));

                     quantitat = 0;
                     clientInserit = false;
                     if (fechaFinalInt == 0) fechaFinalInt = fechaInicialInt;

                     if (fechaInicialInt >= fechaActualInt){ // SI LA FECHA INICIAL ES MAS GRANDE O IGUAL QUE LA FECHA ACTUAL

                         if (fechaInicialInt <= fechaFinalInt){   // SI LA FECHA INICIAL ES MAS PEQUEÑA O IGUAL QUE LA FECHA FINAL
                             db.obre();

                             // TODO, BUCLE NUEVO PARA AÑADIR TODOS LOS DIAS SELECCIONADOS
                             fechasSeleccionadas = new ArrayList();
                             totalDias = 0;
                             if (diaFinal == null ){  // SI SOLO TENEMOS UN DIA ELEGIDO
                                 obtenerAñoMesDiaInicio(fechaInicio);  // De serie buscamos cuales son los años mes y dia inico
                                 if (Utilitats.diaHabil(añoInicio+"-"+mesInicio+"-"+diaInicio,Calendar.SUNDAY)){
                                     Log.d("INICIO RESERVA:",Integer.toString(añoInicio)+" "+ Integer.toString(mesInicio)+" "+Integer.toString(diaInicio));
                                     fechasSeleccionadas.add(añoInicio+" "+mesInicio+" "+diaInicio);
                                     resultatInserirClient = db.InserirReserva_Cliente(fechasSeleccionadas.get(totalDias),"0","0",Integer.parseInt(idCliente),idMesa);
                                     if (resultatInserirClient!= -1) {
                                         clientInserit = true;
                                         quantitat++;
                                         fechaInicioConsulta = añoInicio+" "+mesInicio+" "+diaInicio;
                                     } else Toast.makeText(view.getContext(), nombreCliente+" ya tiene reserva el dia "+Utilitats.getFechaFormatSpain(fechasSeleccionadas.get(totalDias)), Toast.LENGTH_SHORT).show();
                                 } else Toast.makeText(MesaActivity.this, "Fin de semana cerrado", Toast.LENGTH_SHORT).show();
                             } else  { // TODO SI HAY VARIOS DIAS ELEGIDOS
                                 if (mesInicio == mesFinal){    // TODO SI LOS DIAS SON DEL MISMO MES Y AÑO
                                     while (diaInicio <= diaFinal){ introducirClienteMesa(); }
                                 } else if (mesFinal-mesInicio==1 || (mesFinal==1 && mesInicio==12)) {  // TODO SI LOS MESES NO SON IGUALES, PERO SON CONSECUTIVOS!!!
                                     boolean reservasHechas= false;
                                     while (reservasHechas==false){   // TODO SI EL MES INICIAL TIENE 30 DIAS
                                         if (mesInicio==4 || mesInicio==6 || mesInicio==9 || mesInicio==11){
                                             while (diaInicio <= 30){ introducirClienteMesa(); }  // Llegamos hasta al finals de mes
                                             contadorDia=1;     // Y luego empezamos desde el dia 1 hasta el diaFinal de mesFinal
                                             while (contadorDia <= diaFinal){ introducirClienteMesaMesFinal(); }
                                             reservasHechas = true;   // TODO SI EL MES INICIAL TENE 31 DIAS
                                         } else if (mesInicio==1 || mesInicio==3 || mesInicio==5 || mesInicio==7 || mesInicio==8 || mesInicio==10 || mesInicio==12){
                                             while (diaInicio <= 31){ introducirClienteMesa(); } // Llegamos hasta al finals de mes
                                             contadorDia=1;  // Y luego empezamos desde el dia 1 hasta el diaFinal de mesFinal
                                             while (contadorDia <= diaFinal){
                                                 if(mesInicio==12) añoInicio = añoFinal;
                                                 introducirClienteMesaMesFinal();
                                             }
                                             reservasHechas = true;
                                         }  else if (mesInicio==2){  // TODO SI EL AÑO ES BISIESTO TIENE 29 dias
                                             if (añoInicio % 4 == 0 && añoInicio % 100 != 0 || añoInicio % 400 == 0) {
                                                 while (diaInicio <= 29){ introducirClienteMesa(); }  // Llegamos hasta al finals de mes
                                                 contadorDia=1;     // Y luego empezamos desde el dia 1 hasta el diaFinal de mesFinal
                                                 while (contadorDia <= diaFinal){ introducirClienteMesaMesFinal(); }
                                                 reservasHechas = true;
                                             } else {  // TODO SI EL AÑO NO ES BISIESTO TIENE 28 dias
                                                 while (diaInicio <= 28){ introducirClienteMesa(); }  // Llegamos hasta al finals de mes
                                                 contadorDia=1;     // Y luego empezamos desde el dia 1 hasta el diaFinal de mesFinal
                                                 while (contadorDia <= diaFinal){ introducirClienteMesaMesFinal(); }
                                                 reservasHechas = true;
                                             }
                                         }
                                     }
                                 } else Toast.makeText(MesaActivity.this, "No se pueden hacer reservas para mas de 2 meses!", Toast.LENGTH_SHORT).show();
                             }
                             // TODO: SI HEMOS INSERTADO UN CLIENTE, CREAMOS FACTURA
                             if (clientInserit){
                                 actualizarRecyclerView();
                                 crearFacturaReservaMesa(quantitat);
                                 headerAdapterMesa.actualitzaRecycler(myDataset);
                                 Toast.makeText(MesaActivity.this, "Reserva realizada!", Toast.LENGTH_SHORT).show();
                                 if (idCliente != null){
                                     idCliente=null;
                                     diaFinal=null;mesFinal=null;añoFinal=null;
                                     textViewTextoCliente.setVisibility(View.GONE);
                                     textViewClienteSeleccionado.setVisibility(View.GONE);
                                     textViewFechaFinal.setVisibility(View.GONE);
                                     textViewFechaFinalTexto.setVisibility(View.GONE);
                                     fechaInicialEscogida = false; fechaFinalEscogida=false;
                                    // diaInicio=null;mesInicio=null;añoInicio=null;diaFinal=null;mesFinal=null;añoFinal=null;
                                 }
                             }
                             db.tanca();
                         } else Toast.makeText(MesaActivity.this, "Fecha Final mínima: "+Utilitats.getFechaFormatSpain(fechaInicio), Toast.LENGTH_SHORT).show();
                     } else Toast.makeText(MesaActivity.this, "Fecha Inicio mínima: "+Utilitats.getFechaFormatSpain(Utilitats.obtenerFechaActual()), Toast.LENGTH_SHORT).show();
                 } else Toast.makeText(MesaActivity.this, "Introduce cliente!", Toast.LENGTH_SHORT).show();
             }
         }
        );
        buttonImagenMesas.setOnClickListener( new View.OnClickListener(){
              @Override
              public void onClick(View view) {
                  imageViewMesas = (ImageView) findViewById(R.id.imageViewMesasBar);
                  if (imageViewMesas.getVisibility()==View.VISIBLE){
                      imageViewMesas.setVisibility(View.GONE);
                  } else {
                      imageViewMesas.setVisibility(View.VISIBLE);
                  }
              }
          }
        );
        buttonVistaMesas.setOnClickListener( new View.OnClickListener(){
               @Override
               public void onClick(View view) {

                   layoutVistaMesas = (LinearLayout) findViewById(R.id.layoutVistaMesas);
                   if (recyclerView.getVisibility()==View.VISIBLE){
                       recyclerView.setVisibility(View.GONE);
                       layoutVistaMesas.setVisibility(View.VISIBLE);
                   } else {
                       recyclerView.setVisibility(View.VISIBLE);
                       layoutVistaMesas.setVisibility(View.GONE);
                   }
               }
           }
        );
         // TODO  Retorna tots els clients, l'utilitzarem per a la llista que usa el SEARCH VIEW, cuando buscamos cliente!!!
         retornaClients();
    }

    public Integer obtenerFechaInicioNumerico(){
        String [] fecha = fechaInicio.split(" ");
        añoInicio = Integer.parseInt(fecha[0]);
        mesInicio = Integer.parseInt(fecha[1]);
        diaInicio = Integer.parseInt(fecha[2]);

        if (Integer.toString(diaInicio).length()==1) {
            fechaInicio="" + añoInicio+ " " + mesInicio + " " +0+diaInicio;
            if (Integer.toString(mesInicio).length()==1){
                fechaInicio="" + añoInicio + " " +0+mesInicio + " " +0+diaInicio;
            }
        } else {
            if (Integer.toString(mesInicio).length()==1){
                fechaInicio="" + añoInicio + " " +0+mesInicio + " " +diaInicio;
            } else fechaInicio="" + añoInicio + " " + mesInicio + " " + diaInicio;
        }
        return Integer.parseInt(fechaInicio.replaceAll("\\s",""));
    }
    public void listenersMesas (Button button) {
        CharSequence mesa = button.getText();
        Intent intent = new Intent(MesaActivity.this,ReservaActivity.class);
        if (mesa.toString().equalsIgnoreCase("LLEVAR")){
            intent.putExtra("ID_MESA","1");
            intent.putExtra("NOM_MESA","Mesa llevar");
        } else {
            Integer numMesa = Integer.parseInt(mesa.toString())+1;
            intent.putExtra("ID_MESA",Integer.toString(numMesa));
            intent.putExtra("NOM_MESA","Mesa "+mesa.toString());
        }

        intent.putExtra("DIA_RESERVADO",fechaInicioConsulta);
        startActivity(intent);
    }


    public void introducirClienteMesa(){
        if (Utilitats.diaHabil(añoInicio+"-"+mesInicio+"-"+diaInicio,Calendar.SUNDAY)){
            fechasSeleccionadas.add(añoInicio+" "+mesInicio+" "+diaInicio); // diaInicio ++
            // Log.d("FECHA SELECCIOANADA ", fechasSeleccionadas[totalDias] );
            resultatInserirClient = db.InserirReserva_Cliente(fechasSeleccionadas.get(totalDias),"0","0",Integer.parseInt(idCliente),idMesa);
            Log.d("Resultat inserir Client",Long.toString(resultatInserirClient));

            if (resultatInserirClient==-1){
                Toast.makeText(MesaActivity.this, nombreCliente+" ya tiene reserva el dia "+Utilitats.getFechaFormatSpain(fechasSeleccionadas.get(totalDias)), Toast.LENGTH_SHORT).show();
            } else {
                quantitat++;
                clientInserit = true;
            }
            totalDias = totalDias +1;
        }
        diaInicio++;
    }
    public void introducirClienteMesaMesFinal(){
        if (Utilitats.diaHabil(añoInicio+"-"+mesFinal+"-"+contadorDia,Calendar.SUNDAY)){
            fechasSeleccionadas.add(añoInicio+" "+mesFinal+" "+contadorDia); // diaInicio ++
            Log.d("FECHA SELECCIOANADA ",añoInicio+"-"+mesFinal+"-"+contadorDia  );
            resultatInserirClient = db.InserirReserva_Cliente(fechasSeleccionadas.get(totalDias),"0","0",Integer.parseInt(idCliente),idMesa);
            Log.d("INSERCIO: ",fechasSeleccionadas.get(totalDias));
            Log.d("Resultat inserir Client",Long.toString(resultatInserirClient));

            if (resultatInserirClient==-1){
                Toast.makeText(MesaActivity.this, nombreCliente+" ya tiene reserva el dia "+Utilitats.getFechaFormatSpain(fechasSeleccionadas.get(totalDias)), Toast.LENGTH_SHORT).show();
            } else {
                quantitat++;
                clientInserit = true;
            }
            totalDias = totalDias +1;
        }
        contadorDia++;
    }


    public void obtenerAñoMesDiaInicio(String fechaInicio){
        String [] fecha = fechaInicio.split(" ");
        añoInicio = Integer.parseInt(fecha[0]);
        mesInicio = Integer.parseInt(fecha[1]);
        diaInicio = Integer.parseInt(fecha[2]);
        Log.d("FECHA INICIO: ",fechaInicio);
    }
    public void obtenirTaulaDefecteClient (){
        if (idCliente!=null){
            db.obre();
            Cursor cursorTaulaDefecte = db.RetornaTaulaDefecteClient(idCliente);
            if (cursorTaulaDefecte.moveToFirst()) {
                do {
                    taulaPerDefecteClient = cursorTaulaDefecte.getString(cursorTaulaDefecte.getColumnIndex(ContracteBD.Client.MESA_FAVORITA));
                    Log.d("MESA POR DEFECTO: ", taulaPerDefecteClient);
                } while (cursorTaulaDefecte.moveToNext());
            }
        }
        db.tanca();



    }
    // TODO ESTO ES LO QUE SE VE EN EL SEARCH VIEW, CUANDO EMPEZAMOS A ESCRIBIR CLIENTE
    public void retornaClients(){

        db.obre();
        clientes.clear();
        clientesSoloNombres.clear();
        Cursor cursor= db.RetornaTotsElsClients();
        if (cursor.moveToFirst()) {
            do {    // Hacemos que no se pueda escoger a los cliente barra
                if (cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT)).contains("Cliente Barra")|| cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT)).contains("Cliente barra") ){
                    cursor.moveToNext();
                } else {
                    clientes.add(cursor.getString(cursor.getColumnIndex(ContracteBD.Client._ID))+" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT))
                            +" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.COGNOMS_CLIENT))+" :"+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.TIPO_PAGO)));
                    clientesSoloNombres.add(cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT))
                            +" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.COGNOMS_CLIENT)));
                }

            } while (cursor.moveToNext());
        }
        db.tanca();
    }


    public void iniciarSpinnerMesa(){
        db.obre();
        Cursor cursor = getCursorSpinnerMesa(db.RetornaTodasLasMesas());
        adapterMesa = new android.widget.SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                cursor,
                new String[]{"nombre_mesa"}, //Columna del cursor que volem agafar
                new int[]{android.R.id.text1}, 0);
        adapterMesa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Afegeix l'adapter al Spinner de treballadors
        spinnerMesa.setAdapter(adapterMesa);
        db.tanca();

        spinnerMesa.setOnItemSelectedListener(new myOnItemSelectedListener());
    }
    private Cursor getCursorSpinnerMesa(Cursor cursor) {
        MatrixCursor extras = new MatrixCursor(new String[]{"_id", "nombre_mesa"});
        //extras.addRow(new String[]{"0", "Tots"});
        Cursor[] cursors = {extras, cursor};
        return new MergeCursor(cursors);

    }

    public  void crearFacturaReservaMesa(int quantitat){
        db.obre();
        Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idCliente);
        Integer idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
        db.ActualitzarFechaHoraFactura(idVentaFactura,Utilitats.obtenerFechaActual(),Utilitats.obtenerHoraActual());
        //String idVenta = Integer.toString(idVentaFactura);
        Log.d("IDVENTA: ", Integer.toString(idVentaFactura));
        if (idVentaFactura==-1){ // Si no tienen una factura pendiente por pagar
            String hora = Utilitats.obtenerHoraActual();
            //       *** CAMBIAR POR FEHCA Y HORA ACTUAL ***
            db.InserirVenta(Integer.parseInt(idCliente),Integer.parseInt(LoginActivity.ID_TREBALLADOR),Utilitats.obtenerFechaActual(),"0",hora);
            cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idCliente);
            idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
           // idVenta = Integer.toString(idVentaFactura);
        }
        // CREAMOS FACTURA: AÑADIMOS MENU AL RESERVAR MESA

        if (tipoPago.equalsIgnoreCase("0")) db.InserirFactura(1,idVentaFactura,quantitat);
        else if (tipoPago.equalsIgnoreCase("1")) db.InserirFactura(2,idVentaFactura,quantitat);
        else if (tipoPago.equalsIgnoreCase("2")) db.InserirFactura(3,idVentaFactura,quantitat);
        //db.tanca();
        Cursor cursorQuantitatProducteFactura = db.ObtenirQuantitatProductesFactura(Integer.toString(idVentaFactura));
        Integer quantitatProductesFactura = Cursors.cursorQuantitat(cursorQuantitatProducteFactura);
        Log.d("QUANTITAT PRODUCTES ",Integer.toString(quantitatProductesFactura));
        if (quantitatProductesFactura == 0){ // SI NO HAY PRODUCTOS EN LA VENTA, LA REEMBOLSAMOS.
            db.ActalitzaEstatVenta(Integer.toString(idVentaFactura),"4");
            db.ActalitzarPagoReservaFecha(idCliente); // Ponemos el estado de la factura en pagado. Porque ha sido reembolsado.

        } else if (quantitatProductesFactura>0){  // SI AUN HAY MAS PRODUCTOS EN LA VENTA, FALTA PAGAR.
            db.ActalitzaEstatVenta(Integer.toString(idVentaFactura),"0");
        }
    }


    public void actualizarRecyclerView(){
        myDataset.clear();
        db.obre();
        // TODO Consulta principal que retorna les dates que es veuen al recycler de mesa

        Cursor cursor;
        if (diaInicio==null){
            Log.d("FECHA INICIO VIEW 1",fechaInicio);
            cursor = db.RetornaMesasReservadasData(fechaInicio);
        } else {
            Log.d("FECHA INICIO VIEW 2",fechaInicio);
            cursor = db.RetornaMesasReservadasData(fechaInicioConsulta);
        }


        myDataset = CursorBD(cursor);
        db.tanca();
    }

    public  ArrayList CursorBD(Cursor cursor){
        ocultarMesas();
        if(cursor.moveToFirst()){

            do {
                myDataset.add(new HeaderMesa(
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Mesa._ID)),
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Mesa.NOMBRE_MESA)),
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Reserva_Cliente.DIA_RESERVADO))

                ));
                String llevar = cursor.getString(cursor.getColumnIndex("columnaLlevar"));
                String total = cursor.getString(cursor.getColumnIndex("columnaTotal"));
                String mesas = cursor.getString(cursor.getColumnIndex("columnaMesas"));
//                Log.d("MESAS: ",mesas);
                //TODO METODO PARA MOSTRAR LAS MESAS GRAFICAMENTAS
                if (mesas!=null) mostrarMesas(Integer.parseInt(mesas));


                int comedor = Integer.parseInt(total)-Integer.parseInt(llevar);
                textViewTotalClientesLlevar.setText(llevar);
                textViewTotalClientes.setText(total);
                textViewTotalClientesComedor.setText(Integer.toString(comedor));
            } while(cursor.moveToNext());

        } else {
            textViewTotalClientesLlevar.setText("0");
            textViewTotalClientes.setText("0");
            textViewTotalClientesComedor.setText("0");
        }

        return myDataset;
    }
    public void ocultarMesas(){
        for( Button mesa : buttonsMesas) {
            mesa.setVisibility(View.INVISIBLE);
        }
    }
    public void mostrarMesas(Integer mesas){
        buttonsMesas.get(mesas-1).setVisibility(View.VISIBLE);
    }

    class myOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        /**
         * @author Carlos Alberto Castro Cañabate
         *
         * Mètode per fer una acció una vegada seleccionat un treballador a l'spinner de treballadors.
         * @param adapterView adaptador
         * @param view spinner
         * @param position posició a l'spinner
         * @param id correspón a la columna _id de treballadors
         */
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Cursor cursor = null;

           ((TextView) view).setTextColor(Color.WHITE);  // COLOR DEL TEXTO SELECCIONADO DEL TOOLBAR

            idMesa = new BigDecimal(id).intValueExact();
//            Toast.makeText(view.getContext(),"ID: "+ Long.toString(id), Toast.LENGTH_SHORT).show();
            // CUANDO SE SELECCIONE CLIENTE, PONER AUTOMATICAMENTE SPINNER CON LA MESA_POR_DEFECTO DEL CLIENTE.
            // BUSCAR COMO INTRODUCIR UN SEARCHVIEW DENTRO DE EL TOOLBAR PARA QUE SEA UN WIDGET MAS
        }

        /**
         * Mètode per realitzar una acció quan no hi ha rés seleccionat. Al nostre cas sempre hi ha selecció.
         * @param adapterView adaptador
         */
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_mesa_widgets, menu);
        MenuItem item = menu.findItem(R.id.searchViewClientes);
        final SearchView searchView = (SearchView)item.getActionView();


        MenuItem itemSpinnerMesa = menu.findItem(R.id.spinnerMesa);
        spinnerMesa = (Spinner)itemSpinnerMesa.getActionView();

        iniciarSpinnerMesa();
        searchView.setQueryHint("Nombre Cliente...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO MOSTRAMOS TOOLBAR:
                mToolbar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                listViewClientes.setVisibility(View.VISIBLE);
                // TODO: MOSTRAMOS TEXTO FILTRADO DE EL ADAPTER DE CLIENTES
                // TODO: BUSCAR COMO DAR FORMATO AL LISTVIEW DE CLIENTES PARA QUE SEA MAS CHULO
                adapterClientes.getFilter().filter(newText);

                return true;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }
    public void iniciarMesas(){
        for(int id : BUTTON_IDS) {
            Button button = (Button)findViewById(id);
            buttonsMesas.add(button);
        }
        buttonsMesas.get(0).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(0)); }
        });
        buttonsMesas.get(1).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(1)); }
        });
        buttonsMesas.get(2).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(2)); }
        });
        buttonsMesas.get(3).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(3)); }
        });
        buttonsMesas.get(4).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(4)); }
        });
        buttonsMesas.get(5).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(5)); }
        });
        buttonsMesas.get(6).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(6)); }
        });
        buttonsMesas.get(7).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(7)); }
        });
        buttonsMesas.get(8).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(8)); }
        });
        buttonsMesas.get(9).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(9)); }
        });
        buttonsMesas.get(10).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(10)); }
        });
        buttonsMesas.get(11).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(11)); }
        });
        buttonsMesas.get(12).setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) { listenersMesas(buttonsMesas.get(12)); }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        myDataset = new ArrayList<>();
        headerAdapterMesa= new HeaderAdapterMesa(myDataset);
        db = new DBInterface(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_consulta);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(headerAdapterMesa);

        actualizarRecyclerView();
        headerAdapterMesa.actualitzaRecycler(myDataset);
        Log.d("FECHA INICIO 2: ",fechaInicio);

    }


}
