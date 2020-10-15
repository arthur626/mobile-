package br.com.aaemobile.aaemobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.widget.Toast;



import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import br.com.aaemobile.aaemobile.sync.Permissões;


public class TelaLogin extends AppCompatActivity {

    private EditText emailLogar,senhaLogar;
    private Button btnLogar;
    public  String email;
    public String id;
    public String nome;
    public String tipo;


    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private static final String TAG = "PermissaoTAG";
    private String [] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle(R.string.app_name)
                .setMessage("Deseja sair do Aplicativo:")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }


   public void verificaDados() {

        // Caso ja tenha logado pela 1 vez, assim que abrir o APP dnv ja entra automaticamente

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        String id = pref.getString("id", "" );
        String name = pref.getString("name", "");
        String email= pref.getString("email", "");
        String token = pref.getString("token", "");
        if(!email.isEmpty()) {

            Intent intent = new Intent(TelaLogin.this, TelaPrincipal.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Validar permissões
        Permissões.validarPermissoes(permissoes, this, 1);

        //Abrir Tela Cheia
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        emailLogar = (EditText) findViewById(R.id.emailLogar);
        senhaLogar = (EditText) findViewById(R.id.senhaLogar);
        btnLogar = (Button) findViewById(R.id.btnLogar);

        verificaDados();

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailLogar.getText().toString();
                String password = senhaLogar.getText().toString();


                String URL = "http://helper.aplusnet.com.br/api/auth";


                // Verifica se tem campo vazio
                if(getverificaConexao() == true) {
                    if (email.isEmpty()) {
                        Toast.makeText(TelaLogin.this, "Campo email está em branco.", Toast.LENGTH_LONG).show();
                    } else if (password.isEmpty()) {
                        Toast.makeText(TelaLogin.this, "Campo senha está em branco.", Toast.LENGTH_LONG).show();
                    } else {
                        Ion.with(TelaLogin.this)
                                .load(URL)
                                .setBodyParameter("email", email)
                                .setBodyParameter("password", password)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        try {
                                            // Armazenar dados no APP
                                            String token = result.get("token").getAsString();
                                            //Pega dados na tabela users
                                            JsonObject rates = (JsonObject) result.get("user");
                                            String user_id = rates.get("id").getAsString();
                                            String name = rates.get("name").getAsString();
                                            String email = rates.get("email").getAsString();

                                            //Pega dados na tabela colaboradors
                                            JsonObject colaboradors = (JsonObject) result.get("colaborador");
                                            String id = colaboradors.get("id").getAsString();
                                            String matricula = colaboradors.get("matricula").getAsString();
                                            String celular = colaboradors.get("celular").getAsString();
                                            String tipo = colaboradors.get("supervisor").getAsString();

                                            SharedPreferences.Editor pref = getSharedPreferences("info", MODE_PRIVATE).edit();
                                            pref.putString("id", id);
                                            pref.putString("user_id", user_id);
                                            pref.putString("name", name);
                                            pref.putString("email", email);
                                            pref.putString("token", token);
                                            pref.putString("matricula", matricula);
                                            pref.putString("celular", celular);
                                            pref.putString("tipo", tipo);

                                            // Armazena as Preferencias
                                            pref.commit();

                                            Intent intent;
                                            intent = new Intent(TelaLogin.this, TelaPrincipal.class);
                                            Bundle dados = new Bundle();
                                            intent.putExtra("id", id);
                                            intent.putExtra("name", name);
                                            intent.putExtra("email", email);
                                            intent.putExtra("token", token);
                                            intent.putExtras(dados);
                                            startActivity(intent);

                                        } catch (Exception erro) {
                                            senhaLogar.setText("");
                                            Toast.makeText(TelaLogin.this, "Verifique sua matricula, email ou senha."  , Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Sem conexão com a Internet !", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public  boolean getverificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }
}
