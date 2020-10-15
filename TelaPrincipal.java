package br.com.aaemobile.aaemobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.banco.DatabaseHelper;
import br.com.aaemobile.aaemobile.model.Atividades;
import br.com.aaemobile.aaemobile.model.CL;
import br.com.aaemobile.aaemobile.model.Contact;
import br.com.aaemobile.aaemobile.model.Equipamento;
import br.com.aaemobile.aaemobile.model.OS;
import br.com.aaemobile.aaemobile.model.TipoServico;
import br.com.aaemobile.aaemobile.model.TipoSolicitacao;
import br.com.aaemobile.aaemobile.sync.BootReciever;


public class TelaPrincipal extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    static final String FTP_HOST = "162.241.104.141";
    static final String FTP_USER = " wwaplu";
    static final String FTP_PASS = "lc8EH%*)alacqvdjoD4z";

    public static final int PRIMARY_FOREGROUND_NOTIF_SERVICE_ID = 1001;
    private static final int REQUEST_READ = 1002;
    private static final String TAG = "TelaPrincipal";

    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    DatabaseHelper controller;
    protected Context context;
    BancoGeral myBDGeral;
    public JsonArrayRequest request, requestAtividades;
    public RequestQueue requestQueue, requestQueueAtividades;
    String email, name, colaborador_id, token, user_id, tipo;
    ProgressDialog progressBar;
    private Handler progressBarHandler = new Handler();
    private int progressBarStatus = 0;
    private long progress = 0;
    public boolean puxartodososlocais, syncautomatica;

    public static final int REQ_CODE_SPEAK = 100;
    public TextView txtDataSync, txtNameUsuario,
            txtDataeHora, txtQuantVisitasAbertas,txtQuantVisitasSync,
            txtQuantVisitasEncerradas, txtEnderecoUsuario,
            txtVersaoAtual, txtQuantImagens;


    private String gravarVisita = "http://helper.aplusnet.com.br/aplicativo/sync/gravarvisita.php";

//Criando a tela Principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        //Não abrir o teclado automatico
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        //Verifica se a opção de trazer novos dados está habilitado
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        syncautomatica = preferences.getBoolean("key_syncautomatica", true);
        puxartodososlocais = preferences.getBoolean("key_trazertodososdados", false);

        if (syncautomatica == true) {
            startService(new Intent(getBaseContext(), BootReciever.class));
        } else {
            //Não está habilitado sync automatica
        }

        controller = new DatabaseHelper(this);
        myBDGeral = new BancoGeral(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        callConection();

        //Vou pegar via SharedPrefereces para não da erro por conta Tipo e UserID serem passados apenas na Tela Login
        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        user_id = pref.getString("user_id", "");
        name = pref.getString("name", "");
        email = pref.getString("email", "");
        colaborador_id = pref.getString("id", "");
        token = pref.getString("token", "");
        tipo = pref.getString("tipo", "");

        txtNameUsuario = findViewById(R.id.txt_usuarionome);
        if (name != null) {
            txtNameUsuario.setText(name);
        }

        txtDataeHora = findViewById(R.id.tv_data);
        String data = new SimpleDateFormat("HH:mm:ss -  dd/MM/yyyy ").format(System.currentTimeMillis());
        txtDataeHora.setText(data);

        txtQuantVisitasAbertas = findViewById(R.id.tv_quantVisitasAbertas);
        txtQuantVisitasAbertas.setText(String.valueOf(myBDGeral.dbCoutaberta(colaborador_id)));

       //txtQuantVisitasSync = findViewById(R.id.tv_quantVisitasSync);
       //txtQuantVisitasSync.setText(String.valueOf(myBDGeral.dbCoutsync(colaborador_id)));

        txtQuantVisitasEncerradas = findViewById(R.id.tv_visitasencerradas);
        txtQuantVisitasEncerradas.setText(String.valueOf(myBDGeral.dbCoutencerrada(colaborador_id)));

        txtQuantImagens = findViewById(R.id.tv_quantImagens);

        txtEnderecoUsuario = findViewById(R.id.tv_LocalizacaoAtual);

        txtVersaoAtual = findViewById(R.id.tv_versionAPP);
        txtVersaoAtual.setText(BuildConfig.VERSION_NAME);

       // txtDataSync = findViewById(R.id.tv_ultimaSync);
       // txtDataSync.setText("Desenvolvimento");


        //Verifica quantidade Imagens enviadas
        verificarArmazenamento();


        BottomNavigationView navigation = findViewById(R.id.nav_viewTelaPrincipal);
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        navigation.getMenu().findItem(R.id.navigation_visitas).setTitle("VISITA(" + String.valueOf(myBDGeral.dbCountAbertas() + ")"));
        navigation.getMenu().findItem(R.id.navigation_syncVisita).setTitle("ENVIAR(" + String.valueOf(myBDGeral.dbCountEncerradas() + ")"));

    }
// sair do app
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle(R.string.app_name)
                .setMessage("Deseja sair do Aplicativo:")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Menu Principal");
        getMenuInflater().inflate(R.menu.main_activity__principal, menu);
        return true;
    }

  /*  public void emitirNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "_channel_01";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, "notification", importance);
            mChannel.enableLights(true);

            String quantidadeOS = String.valueOf(myBDGeral.dbCoutaberta(colaborador_id));
            int quantOS = Integer.parseInt(quantidadeOS);
            String data = new SimpleDateFormat("HH:mm:ss -  dd/MM/yyyy ").format(System.currentTimeMillis());

            Notification notification = new Notification.Builder(getApplicationContext(), id)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Você possui novas visitas para realizar.")
                    .setContentText("VisitasLocal em aberto: " + quantOS)
                    .setSubText("Ultima sync: " + data)
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(PRIMARY_FOREGROUND_NOTIF_SERVICE_ID, notification);
            }
        }
    }*/
//Verificar conexão com a internet
    public boolean verificaConexao() {
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

    //Menu de Opções
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Atualizar os Dados
        if (id == R.id.action_atualizarDados) {
            syncDados();
        }
        //Deletar Todos os Dados
        if (id == R.id.action_deletar) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.logo)
                    .setTitle(R.string.app_name)
                    .setMessage("Deseja apagar todos os telefone ? Verifique se não possui nenhuma visita para enviar.")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controller.deleteDados();
                            myBDGeral.deleteDados();
                            Toast.makeText(getApplicationContext(), "Favor atulizar para trazer novos dados.", Toast.LENGTH_LONG).show();
                            TelaPrincipal.super.onRestart();
                            Intent intent = new Intent(TelaPrincipal.this, TelaPrincipal.class);
                            Bundle dados = new Bundle();
                            dados.putString("name", name);
                            dados.putString("email", email);
                            dados.putString("id", colaborador_id);
                            dados.putString("token", token);
                            intent.putExtras(dados);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .show();
        }

        if (id == R.id.action_configuracoes) {
            Intent intent = new Intent(TelaPrincipal.this, TelaConfiguracoes.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", colaborador_id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);
        }

        if (id == R.id.action_trocarusuario) {
            Intent voltarLogin = new Intent(TelaPrincipal.this, TelaLogin.class);
            SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            startActivity(voltarLogin);
        }
        //Enviar LOG

        return super.onOptionsItemSelected(item);
    }
//Armazenamento de fotos
    public void verificarArmazenamento() {

        if (ContextCompat.checkSelfPermission(TelaPrincipal.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);

        File fEnviado = new File("/storage/emulated/0/PicturesHELPER/enviados/");
        File fEnviar = new File("/storage/emulated/0/PicturesHELPER/enviar/");
        int count = 0;
        int countEnviar = 0;

        if (fEnviado.exists() && fEnviar.exists()) {

            for (File file : fEnviado.listFiles()) {
                count++;
                //Verifica a quantidade de dias pela gravação dos arquivos
                long diff = new Date().getTime() - file.lastModified();
                int dias = 1;
                if (diff > dias * 24 * 60 * 60 * 1000) {
                    file.delete();
                }
            }
            for (File file : fEnviar.listFiles()) {
                countEnviar++;
                System.out.println("Quantidade Imagens Para enviar: " + countEnviar);
                txtQuantImagens.setText(String.valueOf(countEnviar));
            }
        } else {
            fEnviado.mkdirs();
            fEnviar.mkdirs();
        }
    }

    public void enviarFotosRestantes() {

        final int quantFotosAEnviar = Integer.parseInt(txtQuantImagens.getText().toString());

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Sincronização em andamento, favor aguardar... ");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgressDrawable(TelaPrincipal.this.getResources().getDrawable(R.drawable.custom_progress));
        progressBar.setProgress(0);
        progressBar.setMax(quantFotosAEnviar);
        progressBar.show();
        progressBarStatus = 0;
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < quantFotosAEnviar) {
                    //  progressBarStatus = doSomeTasks();
                    try {
                        Thread.sleep(4000);

                        //Inicio FTP
                        FTPClient ftpClient = new FTPClient();
                        try {

                            ftpClient.connect(FTP_HOST);
                            ftpClient.setSoTimeout(10000);
                            ftpClient.enterLocalPassiveMode();


                            if (ftpClient.login(FTP_USER, FTP_PASS) ){
                                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                                ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                                final File folder = new File("/storage/emulated/0/PicturesHELPER/enviar/");
                                ftpClient.changeWorkingDirectory("/public_html/helper/public/assets/os/");

                                for (final File fileEntry : folder.listFiles()) {
                                    //verificar conexão FTP
                                    if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                                        if (progressBarStatus < quantFotosAEnviar) {
                                            try {
                                                FileInputStream fs = new FileInputStream(fileEntry);
                                                if (!fileEntry.isDirectory()) {
                                                    String fileName = fileEntry.getName();
                                                    if (ftpClient.storeFile(fileName, fs)) {
                                                        File from = new File("/storage/emulated/0/PicturesHELPER/enviar/" + fileName);
                                                        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "HELPER") + "/enviados/";
                                                        File myDir = new File(root);
                                                        myDir.mkdirs();
                                                        File to = new File(root + fileName);
                                                        from.renameTo(to);
                                                        fs.close();
                                                        progressBarStatus++;
                                                        progressBar.setProgress(progressBarStatus);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                progressBarStatus = quantFotosAEnviar;
                                            }
                                        }
                                    }else{
                                        ftpClient.disconnect();
                                        Toast.makeText(getApplicationContext(), "Conexão recusada! ", Toast.LENGTH_LONG).show();
                                        System.exit(0);
                                    }
                                }
                            }else{
                                ftpClient.disconnect();
                                Toast.makeText(getApplicationContext(), "Conexão recusada! ", Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //FIM FTP
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.dismiss();
                            //emitirNotificacao();
                            TelaPrincipal.super.onRestart();
                            Intent intent = new Intent(TelaPrincipal.this, TelaPrincipal.class);
                            Bundle dados = new Bundle();
                            dados.putString("name", name);
                            dados.putString("email", email);
                            dados.putString("id", colaborador_id);
                            dados.putString("token", token);
                            intent.putExtras(dados);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }).start();

    }
    public void ProgressBarStatus() {

        progressBar = new ProgressDialog(TelaPrincipal.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Sincronizando Dados, favor aguardar alguns segundos. "); // set message in progressbar dialog
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgressDrawable(TelaPrincipal.this.getResources().getDrawable(
                R.drawable.custom_progress));
        progressBar.setProgress(0); //set min value of progress bar
        progressBar.setMax(100); // set max value of progress bar
        progressBar.show(); // display progress bar
        //reset progress bar status
        progressBarStatus = 0;
        //reset progress
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // process some tasks
                    progressBarStatus = doSomeTasks();
                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                // Progress completed ?!?!,
                if (progressBarStatus >= 100) {
                    // sleep 2000 milliseconds, so that you can see the 100%
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();
                    //emitirNotificacao();
                    TelaPrincipal.super.onRestart();
                    Intent intent = new Intent(TelaPrincipal.this, TelaPrincipal.class);
                    Bundle dados = new Bundle();
                    dados.putString("name", name);
                    dados.putString("email", email);
                    dados.putString("id", colaborador_id);
                    dados.putString("token", token);
                    intent.putExtras(dados);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }


    public int doSomeTasks() {
        progress++;
        if (progress == 1000) {
            return 10;
        } else if (progress == 2000) {
            return 20;
        } else if (progress == 3000) {
            return 30;
        } else if (progress == 4000) {
            return 40;
        } else if (progress == 5000) {
            return 50;
        } else if (progress == 6000) {
            return 60;
        } else if (progress == 7000) {
            return 70;
        } else if (progress == 8000) {
            return 80;
        } else if (progress == 9000) {
            return 90;
        } else if (progress == 10000) {
        }
        return 100;
    }

    public void syncDados() {

        if (verificaConexao() == true) {

            String URL = "http://helper.aplusnet.com.br/aplicativo/atualizarOperacao.php?colaborador_id=" + colaborador_id;
            ProgressBarStatus();

            request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            CL cl = new CL();
                            cl.setIdCL(jsonObject.getString("idCL"));
                            cl.setCentrocusto(jsonObject.getString("centrocusto"));
                            cl.setDescricao(jsonObject.getString("descricao"));

                            Cursor data = myBDGeral.verificaCL(jsonObject.getString("idCL"));
                            if (data.moveToNext()) {
                                myBDGeral.updateCL(
                                        cl.getIdCL(),
                                        cl.getCentrocusto(),
                                        cl.getDescricao());
                            } else {
                                myBDGeral.insertCentroCusto(cl.getIdCL(), cl.getCentrocusto(), cl.getDescricao());
                            }

                            Contact local = new Contact();
                            local.setId(jsonObject.getString("idLocal"));
                            local.setCodigolocal(jsonObject.getString("codigolocal"));
                            local.setCentrocusto_id(jsonObject.getString("centrocusto_idLocal"));
                            local.setDescricaolocal(jsonObject.getString("descricaolocal"));
                            local.setBairro(jsonObject.getString("bairro"));
                            local.setCidade(jsonObject.getString("cidade"));
                            local.setSigla(jsonObject.getString("sigla"));
                            local.setEstado(jsonObject.getString("estado"));
                            local.setLatitude(jsonObject.getString("latitude"));
                            local.setLongitude(jsonObject.getString("longitude"));
                            local.setTempogasto(jsonObject.getString("tempogasto"));
                            local.setRegiaoID(jsonObject.getString("regiaoID"));
                            local.setRegiaoDescricao(jsonObject.getString("regiaoDescricao"));
                            local.setPoloatendimentosID(jsonObject.getString("poloatendimentosID"));
                            local.setPoloatenidmentosDescricao(jsonObject.getString("poloatendimentosDescricao"));
                            local.setContatoID(jsonObject.getString("contatoID"));
                            local.setContatoCLID(jsonObject.getString("contatoCLID"));
                            local.setContatoNome(jsonObject.getString("contatoNome"));
                            local.setContatoEndereco(jsonObject.getString("contatoEndereco"));
                            local.setContatoLatitude(jsonObject.getString("contatoLatitude"));
                            local.setContatoLongitude(jsonObject.getString("contatoLongitude"));
                            local.setAreaconstruida(jsonObject.getString("areaconstruida"));
                            local.setAreacapina(jsonObject.getString("areacapina"));
                            local.setEnderecolocal(jsonObject.getString("enderecolocal"));

                            Cursor dataLocal = myBDGeral.verificaLocal(jsonObject.getString("idLocal"));
                            if (dataLocal.moveToNext()) {
                            } else {
                                myBDGeral.insertLocal(
                                        local.getId(),
                                        local.getCodigolocal(),
                                        local.getCentrocusto_id(),
                                        local.getDescricaolocal(),
                                        local.getBairro(),
                                        local.getCidade(),
                                        local.getLatitude(),
                                        local.getLongitude(),
                                        local.getSigla(),
                                        local.getEstado(),
                                        local.getTempogasto(),
                                        local.getRegiaoID(),
                                        local.getRegiaoDescricao(),
                                        local.getPoloatendimentosID(),
                                        local.getPoloatenidmentosDescricao(),
                                        local.getContatoID(),
                                        local.getContatoCLID(),
                                        local.getContatoNome(),
                                        local.getContatoEndereco(),
                                        local.getContatoLatitude(),
                                        local.getContatoLongitude(),
                                        local.getAreaconstruida(),
                                        local.getAreacapina(),
                                        local.getEnderecolocal(),
                                        local.getFrequencia(),
                                        local.getRaio(),
                                        local.getSituacao());
                            }
                            myBDGeral.updateLocal(
                                    local.getId(),
                                    local.getCodigolocal(),
                                    local.getCentrocusto_id(),
                                    local.getDescricaolocal(),
                                    local.getBairro(),
                                    local.getCidade(),
                                    local.getLatitude(),
                                    local.getLongitude(),
                                    local.getSigla(),
                                    local.getEstado(),
                                    local.getTempogasto(),
                                    local.getRegiaoID(),
                                    local.getRegiaoDescricao(),
                                    local.getPoloatendimentosID(),
                                    local.getPoloatenidmentosDescricao(),
                                    local.getContatoID(),
                                    local.getContatoCLID(),
                                    local.getContatoNome(),
                                    local.getContatoEndereco(),
                                    local.getContatoLatitude(),
                                    local.getContatoLongitude(),
                                    local.getAreaconstruida(),
                                    local.getAreacapina(),
                                    local.getEnderecolocal());

                            dataLocal.close();

                            Equipamento equipamento = new Equipamento();
                            equipamento.setId(jsonObject.getString("idEquipamento"));
                            equipamento.setCodigoequipamento(jsonObject.getString("codigoequipamento"));
                            equipamento.setDescricaoequipamento(jsonObject.getString("descricaoequipamento"));
                            equipamento.setCentrocusto_id(jsonObject.getString("centrocusto_idEquipamento"));
                            equipamento.setLocal_id(jsonObject.getString("local_idEquipamento"));
                            equipamento.setModelo(jsonObject.getString("modelo"));
                            equipamento.setTag(jsonObject.getString("tag"));
                            equipamento.setNumeroserie(jsonObject.getString("numeroserie"));
                            equipamento.setBtu(jsonObject.getString("btu"));
                            equipamento.setFabricante(jsonObject.getString("fabricante"));
                            equipamento.setTipoequipamento(jsonObject.getString("tipoequipamento"));
                            equipamento.setFornecedor(jsonObject.getString("fornecedor"));
                            Cursor dataEquipamento = myBDGeral.verificaEquipamento(jsonObject.getString("idEquipamento"));
                            if (dataEquipamento.moveToNext()) {
                                myBDGeral.updateEquipamento(
                                        equipamento.getId(),
                                        equipamento.getCodigoequipamento(),
                                        equipamento.getDescricaoequipamento(),
                                        equipamento.getCentrocusto_id(),
                                        equipamento.getLocal_id(),
                                        equipamento.getModelo(),
                                        equipamento.getTag(),
                                        equipamento.getNumeroserie(),
                                        equipamento.getBtu(),
                                        equipamento.getFabricante(),
                                        equipamento.getTipoequipamento(),
                                        equipamento.getFornecedor());
                            } else {
                                myBDGeral.insertEquipamento(
                                        equipamento.getId(),
                                        equipamento.getCodigoequipamento(),
                                        equipamento.getDescricaoequipamento(),
                                        equipamento.getCentrocusto_id(),
                                        equipamento.getLocal_id(),
                                        equipamento.getFabricante(),
                                        equipamento.getTipoequipamento(),
                                        equipamento.getFornecedor());
                            }
                            myBDGeral.updateEquipamento(
                                    equipamento.getId(),
                                    equipamento.getCodigoequipamento(),
                                    equipamento.getDescricaoequipamento(),
                                    equipamento.getCentrocusto_id(),
                                    equipamento.getLocal_id(),
                                    equipamento.getModelo(),
                                    equipamento.getTag(),
                                    equipamento.getNumeroserie(),
                                    equipamento.getBtu(),
                                    equipamento.getFabricante(),
                                    equipamento.getTipoequipamento(),
                                    equipamento.getFornecedor());
                            dataEquipamento.close();

                            OS os = new OS();
                            os.setId(jsonObject.getString("idOS"));
                            os.setTiposolicitacao(jsonObject.getString("tiposolicitacao_os"));
                            os.setTiposervico(jsonObject.getString("tiposervico_os"));
                            os.setFrequencia_id(jsonObject.getString("frequencia_id"));
                            os.setFrequencia_descricao(jsonObject.getString("frequencia_descricao"));
                            os.setChecklist_id(jsonObject.getString("checklist_id"));
                            os.setCentrocusto_id(jsonObject.getString("centrocusto_id"));
                            os.setLocal_id(jsonObject.getString("local_id"));
                            os.setEquipamento_id(jsonObject.getString("equipamento_id"));
                            os.setEquipe1(jsonObject.getString("equipe1"));
                            os.setDataplanejamento(jsonObject.getString("dataplanejamento"));
                            os.setDescricaopadrao(jsonObject.getString("descricaopadrao"));
                            os.setCodigochamado(jsonObject.getString("codigocliente"));
                            os.setFlag_os(jsonObject.getString("flag_os"));

                            Cursor dataOS = myBDGeral.verificaOS(jsonObject.getString("idOS"));
                            if (dataOS.moveToNext()) {
                                myBDGeral.updateOS(
                                        os.getId(),
                                        os.getLocal_id(),
                                        os.getCentrocusto_id(),
                                        os.getTiposolicitacao(),
                                        os.getTiposervico(),
                                        os.getFrequencia_id(),
                                        os.getFrequencia_descricao(),
                                        os.getEquipamento_id(),
                                        os.getChecklist_id(),
                                        os.getEquipe1(),
                                        os.getDataplanejamento(),
                                        os.getDescricaopadrao(),
                                        os.getFlag_os(),
                                        os.getCodigochamado()
                                );
                                myBDGeral.updateSituacaoOSSistematica(
                                        os.getId(),
                                        "A");

                            } else {
                                myBDGeral.insertOS(
                                        os.getId(),
                                        os.getLocal_id(),
                                        os.getCentrocusto_id(),
                                        os.getTiposolicitacao(),
                                        os.getTiposervico(),
                                        os.getFrequencia_id(),
                                        os.getFrequencia_descricao(),
                                        os.getEquipamento_id(),
                                        os.getChecklist_id(),
                                        os.getEquipe1(),
                                        os.getDataplanejamento(),
                                        os.getDescricaopadrao(),
                                        "aberta",
                                        os.getCodigochamado(),
                                        os.getFlag_os());

                                myBDGeral.updateSituacaoOSSistematica(
                                        os.getId(),
                                        "A");

                            }
                            dataOS.close();

                            TipoSolicitacao tipoSolicitacao = new TipoSolicitacao();
                            tipoSolicitacao.setId(jsonObject.getString("tiposolicitacao_id"));
                            tipoSolicitacao.setDescricao(jsonObject.getString("tiposolicitacao_descricao"));

                            Cursor dataTipoSolicitacao = myBDGeral.verificaTipoSolicitacao(jsonObject.getString("tiposolicitacao_id"));
                            if (dataTipoSolicitacao.moveToNext()) {
                                myBDGeral.updateTipoSolicitacao(
                                        tipoSolicitacao.getId(),
                                        tipoSolicitacao.getDescricao()
                                );
                            } else {
                                myBDGeral.insertTipoSolicitacao(
                                        tipoSolicitacao.getId(),
                                        tipoSolicitacao.getDescricao());
                            }
                            dataTipoSolicitacao.close();

                            TipoServico tipoServico = new TipoServico();
                            tipoServico.setId(jsonObject.getString("tiposervico_id"));
                            tipoServico.setDescricao(jsonObject.getString("tiposervico_descricao"));

                            Cursor dataTipoServico = myBDGeral.verificaTipoServico(jsonObject.getString("tiposervico_id"));
                            if (dataTipoServico.moveToNext()) {
                                myBDGeral.updateTipoServico(
                                        tipoServico.getId(),
                                        tipoServico.getDescricao()
                                );
                            } else {
                                myBDGeral.insertTipoServico(
                                        tipoServico.getId(),
                                        tipoServico.getDescricao());
                            }
                            dataTipoServico.close();


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue = Volley.newRequestQueue(TelaPrincipal.this);
            int socketTimeout = 20000;
            RetryPolicy policy2 = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy2);
            requestQueue.add(request);
            String URL_ATIVIDADES = "http://helper.aplusnet.com.br/aplicativo/atualizarAtividades.php?colaborador_id=" + colaborador_id;
            requestAtividades = new JsonArrayRequest(URL_ATIVIDADES, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            Atividades atividades = new Atividades();
                            atividades.setItenID(jsonObject.getString("itenID"));
                            atividades.setItenchecklist(jsonObject.getString("itenchecklist"));
                            atividades.setItenDescricao(jsonObject.getString("itenDescricao"));
                            Cursor dataItem = myBDGeral.verificaAtividadesJson(jsonObject.getString("itenID"));
                            if (dataItem.moveToNext()) {
                                myBDGeral.updateAtividades(
                                        atividades.getItenID(),
                                        atividades.getItenchecklist(),
                                        atividades.getItenDescricao());
                            } else {
                                myBDGeral.insertAtividades(
                                        atividades.getItenID(),
                                        atividades.getItenchecklist(),
                                        atividades.getItenDescricao(),
                                        "aberta");
                            }

                            //Verifica se a opção de todos os locais está habiltiado
                            if (puxartodososlocais == true) {
                                syncDadosSupervisor();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueueAtividades = Volley.newRequestQueue(TelaPrincipal.this);
            int socketTimeout2 = 20000;
            RetryPolicy policy3 = new DefaultRetryPolicy(socketTimeout2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            requestAtividades.setRetryPolicy(policy3);
            requestQueueAtividades.add(requestAtividades);
        } else if (verificaConexao() == false) {
            Toast.makeText(getApplicationContext(), "Sem conexão com a internet. ", Toast.LENGTH_LONG).show();
        }
    }
    public void syncDadosSupervisor() {

        if (verificaConexao() == true) {

            String URL = "http://helper.aplusnet.com.br/aplicativo/atualizarDadosSupervisor.php?user_id=" + user_id;

            request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    JSONObject jsonObject = null;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            CL cl = new CL();
                            cl.setIdCL(jsonObject.getString("idCL"));
                            cl.setCentrocusto(jsonObject.getString("centrocusto"));
                            cl.setDescricao(jsonObject.getString("descricao"));

                            Cursor data = myBDGeral.verificaCL(jsonObject.getString("idCL"));
                            if (data.moveToNext()) {
                                myBDGeral.updateCL(
                                        cl.getIdCL(),
                                        cl.getCentrocusto(),
                                        cl.getDescricao());
                            } else {
                                myBDGeral.insertCentroCusto(cl.getIdCL(), cl.getCentrocusto(), cl.getDescricao());
                            }


                            Contact local = new Contact();
                            local.setId(jsonObject.getString("idLocal"));
                            local.setCodigolocal(jsonObject.getString("codigolocal"));
                            local.setCentrocusto_id(jsonObject.getString("centrocusto_idLocal"));
                            local.setDescricaolocal(jsonObject.getString("descricaolocal"));
                            local.setBairro(jsonObject.getString("bairro"));
                            local.setCidade(jsonObject.getString("cidade"));
                            local.setSigla(jsonObject.getString("sigla"));
                            local.setEstado(jsonObject.getString("estado"));
                            local.setLatitude(jsonObject.getString("latitude"));
                            local.setLongitude(jsonObject.getString("longitude"));
                            local.setTempogasto(jsonObject.getString("tempogasto"));
                            local.setRegiaoID(jsonObject.getString("regiaoID"));
                            local.setRegiaoDescricao(jsonObject.getString("regiaoDescricao"));
                            local.setPoloatendimentosID(jsonObject.getString("poloatendimentosID"));
                            local.setPoloatenidmentosDescricao(jsonObject.getString("poloatendimentosDescricao"));
                            local.setContatoID(jsonObject.getString("contatoID"));
                            local.setContatoCLID(jsonObject.getString("contatoCLID"));
                            local.setContatoNome(jsonObject.getString("contatoNome"));
                            local.setContatoEndereco(jsonObject.getString("contatoEndereco"));
                            local.setContatoLatitude(jsonObject.getString("contatoLatitude"));
                            local.setContatoLongitude(jsonObject.getString("contatoLongitude"));
                            local.setAreaconstruida(jsonObject.getString("areaconstruida"));
                            local.setAreacapina(jsonObject.getString("areacapina"));
                            local.setEnderecolocal(jsonObject.getString("enderecolocal"));

                            Cursor dataLocal = myBDGeral.verificaLocal(jsonObject.getString("idLocal"));
                            if (dataLocal.moveToNext()) {
                            } else {
                                myBDGeral.insertLocal(
                                        local.getId(),
                                        local.getCodigolocal(),
                                        local.getCentrocusto_id(),
                                        local.getDescricaolocal(),
                                        local.getBairro(),
                                        local.getCidade(),
                                        local.getLatitude(),
                                        local.getLongitude(),
                                        local.getSigla(),
                                        local.getEstado(),
                                        local.getTempogasto(),
                                        local.getRegiaoID(),
                                        local.getRegiaoDescricao(),
                                        local.getPoloatendimentosID(),
                                        local.getPoloatenidmentosDescricao(),
                                        local.getContatoID(),
                                        local.getContatoCLID(),
                                        local.getContatoNome(),
                                        local.getContatoEndereco(),
                                        local.getContatoLatitude(),
                                        local.getContatoLongitude(),
                                        local.getAreaconstruida(),
                                        local.getAreacapina(),
                                        local.getEnderecolocal(),
                                        local.getFrequencia(),
                                        local.getRaio(),
                                        local.getSituacao());
                            }
                            myBDGeral.updateLocal(
                                    local.getId(),
                                    local.getCodigolocal(),
                                    local.getCentrocusto_id(),
                                    local.getDescricaolocal(),
                                    local.getBairro(),
                                    local.getCidade(),
                                    local.getLatitude(),
                                    local.getLongitude(),
                                    local.getSigla(),
                                    local.getEstado(),
                                    local.getTempogasto(),
                                    local.getRegiaoID(),
                                    local.getRegiaoDescricao(),
                                    local.getPoloatendimentosID(),
                                    local.getPoloatenidmentosDescricao(),
                                    local.getContatoID(),
                                    local.getContatoCLID(),
                                    local.getContatoNome(),
                                    local.getContatoEndereco(),
                                    local.getContatoLatitude(),
                                    local.getContatoLongitude(),
                                    local.getAreaconstruida(),
                                    local.getAreacapina(),
                                    local.getEnderecolocal());

                            dataLocal.close();

                            Equipamento equipamento = new Equipamento();
                            equipamento.setId(jsonObject.getString("idEquipamento"));
                            equipamento.setCodigoequipamento(jsonObject.getString("codigoequipamento"));
                            equipamento.setDescricaoequipamento(jsonObject.getString("descricaoequipamento"));
                            equipamento.setCentrocusto_id(jsonObject.getString("centrocusto_idEquipamento"));
                            equipamento.setLocal_id(jsonObject.getString("local_idEquipamento"));
                            equipamento.setModelo(jsonObject.getString("modelo"));
                            equipamento.setTag(jsonObject.getString("tag"));
                            equipamento.setNumeroserie(jsonObject.getString("numeroserie"));
                            equipamento.setBtu(jsonObject.getString("btu"));
                            equipamento.setFabricante(jsonObject.getString("fabricante"));
                            equipamento.setTipoequipamento(jsonObject.getString("tipoequipamento"));
                            equipamento.setFornecedor(jsonObject.getString("fornecedor"));
                            Cursor dataEquipamento = myBDGeral.verificaEquipamento(jsonObject.getString("idEquipamento"));
                            if (dataEquipamento.moveToNext()) {
                                myBDGeral.updateEquipamento(
                                        equipamento.getId(),
                                        equipamento.getCodigoequipamento(),
                                        equipamento.getDescricaoequipamento(),
                                        equipamento.getCentrocusto_id(),
                                        equipamento.getLocal_id(),
                                        equipamento.getModelo(),
                                        equipamento.getTag(),
                                        equipamento.getNumeroserie(),
                                        equipamento.getBtu(),
                                        equipamento.getFabricante(),
                                        equipamento.getTipoequipamento(),
                                        equipamento.getFornecedor());
                            } else {
                                myBDGeral.insertEquipamento(
                                        equipamento.getId(),
                                        equipamento.getCodigoequipamento(),
                                        equipamento.getDescricaoequipamento(),
                                        equipamento.getCentrocusto_id(),
                                        equipamento.getLocal_id(),
                                        equipamento.getFabricante(),
                                        equipamento.getTipoequipamento(),
                                        equipamento.getFornecedor());
                            }
                            myBDGeral.updateEquipamento(
                                    equipamento.getId(),
                                    equipamento.getCodigoequipamento(),
                                    equipamento.getDescricaoequipamento(),
                                    equipamento.getCentrocusto_id(),
                                    equipamento.getLocal_id(),
                                    equipamento.getModelo(),
                                    equipamento.getTag(),
                                    equipamento.getNumeroserie(),
                                    equipamento.getBtu(),
                                    equipamento.getFabricante(),
                                    equipamento.getTipoequipamento(),
                                    equipamento.getFornecedor());
                            dataEquipamento.close();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            requestQueue = Volley.newRequestQueue(TelaPrincipal.this);
            int socketTimeout = 20000;
            RetryPolicy policy2 = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy2);
            requestQueue.add(request);
        }
    }

    //CODE GOOGLE MAPS - LOCALIZAÇÃO
    private synchronized void callConection() {
        mGoogleApiClient = new GoogleApiClient.Builder(TelaPrincipal.this)
                .addOnConnectionFailedListener(TelaPrincipal.this)
                .addConnectionCallbacks(TelaPrincipal.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ContextCompat.checkSelfPermission(TelaPrincipal.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (l != null) {
            Log.i("LOG", "Latitude" + l.getLatitude());
            Log.i("LOG", "Longtitude" + l.getLongitude());

            // get current locality based on lat lng
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                String address = addresses.get(0).getAddressLine(0);
                txtEnderecoUsuario.setText(address);

                SharedPreferences.Editor pref = getSharedPreferences("salvarLocalizacao", MODE_PRIVATE).edit();
                pref.putString("endereco", txtEnderecoUsuario.getText().toString());
                pref.putString("latitude", Double.toString(l.getLatitude()));
                pref.putString("longitude", Double.toString(l.getLongitude()));
                // Armazena as Preferencias
                pref.commit();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "O seu telefone não suporta a utilização do gps. ", Toast.LENGTH_LONG).show();
            }
        } else {
            txtEnderecoUsuario.setText("Posição não encontrada.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SPEAK:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultado = result.get(0);
                    Toast.makeText(getApplicationContext(), "Resultado: " + resultado, Toast.LENGTH_LONG).show();

                    break;
            }
        }
    }

    public void criarNovaVisita() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> osList = myBDGeral.getOrdemServicos();
        if (osList.size() != 0) {
            Toast.makeText(getApplicationContext(), "Você possui: " + myBDGeral.gravarNovaOS2(), Toast.LENGTH_LONG).show();
            params.put("userJson", myBDGeral.gravarNovaOS2());
            client.post("http://helper.aplusnet.com.br/aplicativo/sync/gerarnovavisita.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    try {
                        JSONArray arr = new JSONArray(response);
                        System.out.println(arr.length());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            System.out.println(obj.get("id"));
                            System.out.println(obj.get("status"));
                            myBDGeral.updateSituacaoOrdemServico(obj.get("id").toString(), obj.get("status").toString());
                        }
                        enviarFotosRestantes();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Erro, favor alguardar alguns minutos. ", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Erro: 404, favor comunir ao TIMG!", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Erro: 500, favor comunicar TIMG.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ocorreu algum erro.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            enviarFotosRestantes();
        }
//        enviarFotosRestantes();
    }

    public void verificarSePossuiMedicao() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> medicoesList = controller.getMedicoes();
        if (medicoesList.size() != 0) {
            if (controller.dbSyncMedicao() != 0) {
                params.put("userJson", controller.gravarMedicao());
                client.post("http://helper.aplusnet.com.br/aplicativo/sync/gravarmedicao.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println(response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            System.out.println(arr.length());
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                System.out.println(obj.get("ordemservico_id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatusMedicao(obj.get("ordemservico_id").toString(), obj.get("status").toString());
                            }
                            criarNovaVisita();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Erro, favor alguardar alguns minutos. ", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "Erro: 404, favor comunir ao TIMG!", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Erro: 500, favor comunicar TIMG.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Ocorreu algum erro.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                criarNovaVisita();
            }
        }
        criarNovaVisita();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      /*  if (id == R.id.navigation_mapa) {
            //Intent intent = new Intent(TelaPrincipal.this, TelaMapa.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", colaborador_id);
            dados.putString("token", token);
            //intent.putExtras(dados);
            //startActivity(intent);
        }*/
        if (id == R.id.navigation_visitas) {
           Intent intent = new Intent(TelaPrincipal.this, TelaContrato.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("colaborador_id", colaborador_id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);
            }


            if (id == R.id.navigation_syncVisita) {
               // System.exit(0);
                int quantFotos = Integer.parseInt(txtQuantImagens.getText().toString());
                if (verificaConexao() == true) {
                    enviarFotosRestantes();
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    ArrayList<HashMap<String, String>> userList = controller.getAllUsers();
                    if (userList.size() != 0) {
                        if (controller.dbSyncNO() != 0) {
                            Toast.makeText(this, "Iniciando a sincronização, favor aguardar....", Toast.LENGTH_SHORT).show();
                            params.put("userJson", controller.composeJSONfromSQLite());
                            client.post(gravarVisita, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response) {
                                    System.out.println(response);
                                    try {
                                        JSONArray arr = new JSONArray(response);
                                        System.out.println(arr.length());
                                        for (int i = 0; i < arr.length(); i++) {
                                            JSONObject obj = (JSONObject) arr.get(i);
                                            System.out.println(obj.get("ordemservico_id"));
                                            System.out.println(obj.get("status"));
                                            controller.updateSyncStatus(obj.get("ordemservico_id").toString(), obj.get("status").toString());
                                            myBDGeral.updateSyncStatus(obj.get("ordemservico_id").toString());
                                        }
                                        verificarSePossuiMedicao();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Toast.makeText(getApplicationContext(), "Erro, favor alguardar alguns miinutos. ", Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Throwable error,
                                                      String content) {
                                    // TODO Auto-generated method stub
                                    if (statusCode == 404) {
                                        Toast.makeText(getApplicationContext(), "Erro: 404, favor comunir ao TIMG!", Toast.LENGTH_LONG).show();
                                    } else if (statusCode == 500) {
                                        Toast.makeText(getApplicationContext(), "Erro: 500, favor comunicar TIMG.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ocorreu algum erro.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        //Verifica se possui alguma foto ainda pendente para enviar
                        else if (quantFotos > 0) {
                            enviarFotosRestantes();
                        } else {
                            Toast.makeText(getApplicationContext(), "Você não possui nenhuma visita e " + quantFotos + " fotos para enviar. ", Toast.LENGTH_LONG).show();
                        }
                    } else if (quantFotos > 0) {
                        enviarFotosRestantes();
                    } else {
                        Toast.makeText(getApplicationContext(), "Você não possui nenhuma visita e " + quantFotos + " fotos para enviar. ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Sem conexão com a Internet !", Toast.LENGTH_LONG).show();
                }
            }

        /*if (id == R.id.nav_tutorial) {

            Intent intent = new Intent(TelaPrincipal.this, TutorialActivity.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", colaborador_id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);


        } else if (id == R.id.nav_modelorf) {

            Intent intent = new Intent(TelaPrincipal.this, TelaModeloRF.class);
            Bundle dados = new Bundle();
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", colaborador_id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);
        }*/

            //Fim Itens Menu
            // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            //drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
