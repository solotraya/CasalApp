package ccastro.casal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import ccastro.casal.FingerPrint.FingerprintHandler;
import ccastro.casal.SQLite.ContracteBD;
import ccastro.casal.SQLite.DBInterface;

public class LoginActivity extends AppCompatActivity {
    DBInterface db;
    Button buttonEntrar, buttonExemples;
    EditText textUserName, textPassword;
    View v;
    Cursor cursor;
    String huella = null;
    public static String ID_TREBALLADOR=null, NOM_USUARI="Administrador";
    private KeyStore keyStore;
    private static final String KEY_NAME = "EDMTDev";
    private Cipher cipher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBInterface(this);
        db.obre();
        // TRABAJADOR admin PARA CUANDO ELIMINE INTRODUCCION DE EJEMPLOS
        db.InserirTreballador("Administrador"," ","admin","xxx");

        db.tanca();
        cargarPreferencias();
        buttonEntrar = (Button) findViewById(R.id.buttonEntrar);
        buttonExemples = (Button) findViewById(R.id.buttonExemples);
        textPassword = (EditText) findViewById(R.id.textPassword);
        textUserName = (EditText) findViewById(R.id.textUserName);
        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = textUserName.getText().toString();
                String password = textPassword.getText().toString();
                db = new DBInterface(getApplicationContext());
                db.obre();
                cursor = db.verificarLogin(userName,password);
                if ((cursor != null) && (cursor.getCount() > 0)){
                    v=view;
                    mouCursor(cursor); // Recogemos el id_usuario y nombre trabajador

                    if (huella == null && ! textUserName.getText().toString().equalsIgnoreCase("admin")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Quieres relacionar tu contraseña con tu huella dactilar?")
                                .setTitle("Atención!!")
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        huella = "NO";
                                        guardarPreferencias();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("Acceptar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                huella = "SI";
                                                Toast.makeText(v.getContext(),"Introduce huella digital", Toast.LENGTH_SHORT).show();
                                                guardarPreferencias();
                                                activarFingerPrint();
                                            }
                                        }
                                );


                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                } else {
                    Toast.makeText(view.getContext(),"Login Incorrecto!", Toast.LENGTH_SHORT).show();
                }
                db.tanca();

            }
        });
        buttonExemples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "S'han esborrat les dades de la base de dades i s'han tornat a crear.", Toast.LENGTH_SHORT).show();
                CrearExemplesBD();
            }
        });
    }
    public void guardarPreferencias(){
        SharedPreferences.Editor editor = getSharedPreferences("HUELLA_CONFIG", MODE_PRIVATE).edit();
        editor.putString("HUELLA",huella);
        editor.putString("ID_TRABAJADOR",ID_TREBALLADOR);
        editor.putString("NOM_USUARI",NOM_USUARI);
        editor.apply();
    }
    public void cargarPreferencias(){
        SharedPreferences prefs = getSharedPreferences("HUELLA_CONFIG", MODE_PRIVATE);
        huella = prefs.getString("HUELLA", null);
        if (huella != null) {
            if (huella.equalsIgnoreCase("SI")){
                NOM_USUARI = prefs.getString("NOM_USUARI", "");//"No name defined" is the default value.
                ID_TREBALLADOR = prefs.getString("ID_TRABAJADOR", ""); //0 is the default value.
                activarFingerPrint();
            }

        }
    }
    public void mouCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                ID_TREBALLADOR = cursor.getString(cursor.getColumnIndex(ContracteBD.Treballador._ID));
                NOM_USUARI = cursor.getString(cursor.getColumnIndex(ContracteBD.Treballador.NOM_TREBALLADOR))+" "+
                cursor.getString(cursor.getColumnIndex(ContracteBD.Treballador.COGNOMS_TREBALLADOR));
            } while (cursor.moveToNext());
        }
    }
    public void CrearExemplesBD(){
        db.obre();
        db.Esborra();

        // TODO ESTO HABRA QUE AÑADIRLO A MANO AL ENTRAR A LA APP
        // INSERTAMOS MESA LLEVAR EN LA POSICION 0, Y ESTA NUNCA SE PODRA ELIMINAR
        db.InserirMesa("Llevar");db.InserirMesa("Mesa 1");db.InserirMesa("Mesa 2");db.InserirMesa("Mesa 3");db.InserirMesa("Mesa 4");
        db.InserirMesa("Mesa 5");db.InserirMesa("Mesa 6");db.InserirMesa("Mesa 7");db.InserirMesa("Mesa 8");
        db.InserirMesa("Mesa 9");db.InserirMesa("Mesa 10");db.InserirMesa("Mesa 11");db.InserirMesa("Mesa 12");
        // ************* HAY QUE INSERTAR LOS DIFERENTES TIPOS DE MENU en PRODUCTO  y QUE NO SE PUEDA ELIMINAR, SI MODIFICAR *************
        db.InserirProducte("Menú Diario","5.50","Menu");   //ID = 1
        db.InserirProducte("Menú Diario","2.50","Menu 50%");   //ID = 2
        db.InserirProducte("Menú Diario","1.25","Menu 75%");   //ID = 3


        // nom, cognoms, username, password
        db.InserirTreballador("Diego","Castro Hurtado","diego","1986");
        db.InserirTreballador("Maria","Cañabate Méndez","maria","1986");
// ************* HAY QUE INSERTAR CLIENTE BARRAy QUE NO SE PUEDA ELIMINAR, SI MODIFICAR *************
        db.InserirClient("Cliente barra","","0",0,"0","0","Cliente de barra sin determinar");

        db.InserirClient("Manuela","Torres Cobijo","0",2,"0","1","Segundo de poquito y pan integral");
        db.InserirClient("Manel","Garcia","0",1,"0","1","");
        db.InserirClient("Remedios","Luque","2",0,"1","2","Vegetariana"); // Ayuntamiento
        db.InserirClient("Asunción","Rodriguez  Perez","2",3,"2","2","Vegetariana");
        db.InserirClient("Juan","Gomez Fuentes","1",4,"0","2","Poquito"); //LLevar
        db.InserirClient("Ramon","Pardo","1",5,"0","2","Normal");
        db.InserirClient("Manolo","Lopez","2",6,"0","0","Poquito");
        db.InserirClient("Ana","Tarres","0",10,"0","0","Poquito");
        db.InserirClient("Marta","Gonzalez","0",7,"0","1","Poquito");
        db.InserirClient("Nicanor","Sanchez","0",8,"0","0","Poquito");

        // nomProducte, preu, tipus

        db.InserirProducte("Café Solo","1.10","0");
        db.InserirProducte("Café con leche","1.20","0");
        db.InserirProducte("Carajillo","1.50","0");
        db.InserirProducte("Cocacola","1.30","1");
        db.InserirProducte("Agua","0.90","1");
        db.InserirProducte("Bocadillo grande","2.50","2");
        db.InserirProducte("Bocadillo pequeño","2","2");
        db.InserirProducte("Whisky cola","5","3");



        //id_client,id_treballador,dataVenta,Cobrada,TotalVenta
        db.InserirVenta(3,1,"2017 11 18","0","10:15");  // SIN ID_FACTURA!!
        db.InserirVenta(2,2,"2017 11 18","1","11:00");

                 //id_producte,id_venta/quantitatProducte
        db.InserirFactura(4,1,1);db.InserirFactura(5,1,2);db.InserirFactura(7,1,4);
        db.InserirFactura(5,2,2);db.InserirFactura(6,2,1);db.InserirFactura(8,2,3);


        //dia, assistencia(0 SI por defecto, 1 NO), pagado (0 NO, 1 SI),idCliente, idMesa

      //  db.InserirReserva_Cliente("2017 11 18","0","1",2,2);
      //   db.InserirReserva_Cliente("2017 11 18","0","0",4,2);
      //   db.InserirReserva_Cliente("2017 11 18","0","0",1,2); db.InserirReserva_Cliente("2017 11 19","0","0",2,2);
      //  db.InserirReserva_Cliente("2017 11 18","0","0",3,2);
        db.tanca();
    }


    public void activarFingerPrint() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (!fingerprintManager.isHardwareDetected())
            Toast.makeText(this, "Fingerprint authentication permission not enable", Toast.LENGTH_SHORT).show();
        else {
            if (!fingerprintManager.hasEnrolledFingerprints())
                Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
            else {
                if (!keyguardManager.isKeyguardSecure())
                    Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                else
                    genKey();

                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuthentication(fingerprintManager, cryptoObject);

                }
            }
        }
    }




    private boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+ KeyProperties.BLOCK_MODE_CBC+"/"+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey)keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            return true;
        } catch (IOException e1) {

            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {

            e1.printStackTrace();
            return false;
        } catch (CertificateException e1) {

            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {

            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {

            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {

            e1.printStackTrace();
            return false;
        }

    }

    private void genKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            );
            keyGenerator.generateKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (huella!=null)activarFingerPrint();
    }

    @Override
    public void onBackPressed() {
        // PROHIBO CERRAR LA APP AL DARLE ATRAS
    }
}
