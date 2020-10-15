package br.com.aaemobile.aaemobile.Visitas;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import br.com.aaemobile.aaemobile.R;
import br.com.aaemobile.aaemobile.TelaAtividades;
import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.banco.DatabaseHelper;

import id.zelory.compressor.Compressor;

public class Atividade_Antes extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    DatabaseHelper myDb;
    BancoGeral myDBGeral;
    ImageView imageView, imageView2, imageView3;
    TextView txtLatitude, txtLongitude, txtObservacao, txtAtividade;
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int CAMERA_PIC_REQUEST2 = 1111;
    private static final int CAMERA_PIC_REQUEST3 = 1111;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    public static final int MEDIA_TYPE_IMAGE = 1;
    private String os_id, foto1a, foto2a, foto3a, foto1d, foto2d, foto3d, checklist_id, dataehora;
    private String medicao1, update_Status, data, inicio, fim;
    private String id_centrolucro, latitude, frequencia_id, longitude, equipamento_id, atividade, id_Atividade, tiposervico, local_id, dataplanejamento;
    private int index;
    public static final int REQ_CODE_SPEAK = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_TAKEPICTURE = {Manifest.permission.CAMERA};
    private static String[] PERMISSIONS_READ_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private Uri currentURI;
    static Uri capturedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.tela_atividadeantes);

        myDb = new DatabaseHelper(this);
        myDBGeral = new BancoGeral(this);

        imageView = (ImageView) findViewById(R.id.foto1a);
        imageView2 = (ImageView) findViewById(R.id.foto2a);
        imageView3 = (ImageView) findViewById(R.id.foto3a);
        txtObservacao = (TextView) findViewById(R.id.txtObservacaoAntes);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtAtividade = (TextView) findViewById(R.id.txtAtividade);

        // toolbar fancy stuff
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Antes");

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        os_id = dados.getString("os_id");
        equipamento_id = dados.getString("equipamento_id");
        checklist_id = dados.getString("checklist_id");
        id_Atividade = dados.getString("id_Atividade");
        id_centrolucro = dados.getString("id_centrolucro");
        local_id = dados.getString("local_id");
        frequencia_id = dados.getString("frequencia_id");
        tiposervico = dados.getString("tiposervico");
        dataplanejamento = dados.getString("dataplanejamento");
        atividade = dados.getString("atividade");

        //Coloca a atividade
        txtAtividade.setText(atividade);


        File imgFile = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg");
        File imgFile2 = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg");
        File imgFile3 = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg");

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }
        if (imgFile2.exists()) {
            Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
            imageView2.setImageBitmap(myBitmap2);
        }
        if (imgFile3.exists()) {
            Bitmap myBitmap3 = BitmapFactory.decodeFile(imgFile3.getAbsolutePath());
            imageView3.setImageBitmap(myBitmap3);
        }
        Cursor data = myDb.getOS(os_id, id_Atividade);
        if (data.moveToNext()) {
            latitude = data.getString(14);
            longitude = data.getString(15);
            String observacao = data.getString(12);

            txtLatitude.setText(latitude);
            txtLongitude.setText(longitude);
            txtObservacao.setText(observacao);
        }


        //Pegar Localização Colaborador
        callConection();

        //Inicializar o Pop Up da Foto
        ImagePopup imagePopup = new ImagePopup(this);

        Picasso.Builder picassoBuilder = new Picasso.Builder(this);

        picassoBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e("PICASSO", uri.toString(), exception);
            }
        });
        Picasso picasso = picassoBuilder.build();
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
        }

        imagePopup.setWindowHeight(420); // Optional
        imagePopup.setWindowWidth(600); // Optional
        imagePopup.setBackgroundColor(Color.WHITE);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        txtObservacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Antes.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você deseja: ")
                        .setPositiveButton("Falar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                lerVoz();
                            }
                        })
                        .setNegativeButton("Digitar Observação", null)
                        .show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Antes.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Verifica se a atividade é a de cartão de Ponto do Contrato OI = Atividade ID 430
                                File foto1a = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg");

                                capturedImageUri = Uri.fromFile(imgFile);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 1);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView.setImageBitmap(null);
                                File imgFile = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg");
                                if (imgFile.exists()) {
                                    imgFile.delete();
                                }
                            }
                        })

                        .setNeutralButton("Vizualizar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (imageView.getDrawable() == null) {
                                    Toast.makeText(getApplicationContext(), "Você ainda não tirou a 1ª Foto", Toast.LENGTH_LONG).show();
                                } else {
                                    imagePopup.initiatePopup(imageView.getDrawable()); // Load Image from Drawable
                                    imagePopup.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });


        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Antes.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File foto2a = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg");

                                capturedImageUri = null;
                                capturedImageUri = Uri.fromFile(foto2a);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 2);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView2.setImageBitmap(null);
                                File imgFile = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg");
                                if (imgFile.exists()) {
                                    imgFile.delete();
                                }
                            }
                        })
                        .setNeutralButton("Vizualizar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (imageView2.getDrawable() == null) {
                                    Toast.makeText(getApplicationContext(), "Você ainda não tirou a 2ª Foto", Toast.LENGTH_LONG).show();
                                } else {
                                    imagePopup.initiatePopup(imageView2.getDrawable()); // Load Image from Drawable
                                    imagePopup.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });


        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Antes.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File foto3a = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg");

                                capturedImageUri = Uri.fromFile(foto3a);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 3);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView3.setImageBitmap(null);
                                File imgFile = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg");
                                if (imgFile.exists()) {
                                    imgFile.delete();
                                }
                            }
                        })

                        .setNeutralButton("Vizualizar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (imageView3.getDrawable() == null) {
                                    Toast.makeText(getApplicationContext(), "Você ainda não tirou a 3ª Foto", Toast.LENGTH_LONG).show();
                                } else {
                                    imagePopup.initiatePopup(imageView3.getDrawable()); // Load Image from Drawable
                                    imagePopup.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_atividadesantes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = sdf.format(dt);

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        String name = pref.getString("name", "");
        String email = pref.getString("email", "");
        String colaborador_id = pref.getString("id", "");
        String token = pref.getString("token", "");

        //Se clicar em continuar visita
        if (id == R.id.action_confirmar) {
            if (imageView.getDrawable() == null) {
                Toast.makeText(getApplicationContext(), "Você deve tirar pelo menos a 1ª Foto !", Toast.LENGTH_LONG).show();
            } else {
                myDb.updateAtividade(
                        os_id,
                        checklist_id,
                        id_Atividade,
                        txtObservacao.getText().toString(),
                        txtLatitude.getText().toString(),
                        txtLongitude.getText().toString(),
                        "C",
                        "no"
                );
                Intent intent = new Intent(Atividade_Antes.this, Atividade_Depois.class);
                Bundle dados = new Bundle();
                dados.putString("os_id", os_id);
                dados.putString("checklist_id", checklist_id);
                dados.putString("equipamento_id", equipamento_id);
                dados.putString("atividade", atividade);
                dados.putString("latitude", txtLatitude.getText().toString());
                dados.putString("longitude", txtLongitude.getText().toString());
                dados.putString("id_Atividade", id_Atividade);
                dados.putString("id_centrolucro", id_centrolucro);
                dados.putString("local_id", local_id);
                dados.putString("frequencia_id", frequencia_id);
                dados.putString("tiposervico", tiposervico);
                dados.putString("dataplanejamento", dataplanejamento);
                dados.putString("name", name);
                dados.putString("email", email);
                dados.putString("idColaborador", colaborador_id);
                dados.putString("token", token);
                intent.putExtras(dados);
                startActivity(intent);
            }
            return true;
        }
        // Se clicar em Não se Aplica
        else if (id == R.id.action_naoseaplica) {
            //Verifica se é Corretiva, se for não pode por NA
            int tiposervicoINT = Integer.parseInt(tiposervico);
            if (tiposervicoINT == 2) {
                Toast.makeText(getApplicationContext(), "Você está em uma visita corretiva. ", Toast.LENGTH_LONG).show();
            } else {
                myDb.updateNA(
                        os_id,
                        checklist_id,
                        id_Atividade,
                        foto1a = "",
                        foto2a = "",
                        foto3a = "",
                        foto1d = "",
                        foto2d = "",
                        foto3d = "",
                        currentTime,
                        "NA",
                        "NA",
                        txtLatitude.getText().toString(),
                        txtLongitude.getText().toString(),
                        "NA",
                        currentTime,
                        update_Status = "no");

                //Encerra Atividade
                myDBGeral.updateStatusAtividade(
                        checklist_id,
                        id_Atividade,
                        "encerrada"
                );

                Intent intent = new Intent(Atividade_Antes.this, TelaAtividades.class);
                Bundle dados = new Bundle();
                dados.putString("os_id", os_id);
                dados.putString("checklist_id", checklist_id);
                dados.putString("equipamento_id", equipamento_id);
                dados.putString("local_id", local_id);
                dados.putString("frequencia_id", frequencia_id);
                dados.putString("tiposervico", tiposervico);
                dados.putString("dataplanejamento", dataplanejamento);
                dados.putString("centrocusto_id", id_centrolucro);
                dados.putString("name", name);
                dados.putString("email", email);
                dados.putString("colaborador_id", colaborador_id);
                dados.putString("tipo", token);
                intent.putExtras(dados);
                startActivity(intent);
            }
            return true;
        }
        //Caso clique para inserir Medição
        else if (id == R.id.action_medicao) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.app_name);
            alert.setIcon(R.drawable.logo);
            alert.setCancelable(false);
            alert.setMessage("Informe a medição: ");
            final EditText input = new EditText(this);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            alert.setPositiveButton("Gravar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    medicao1 = input.getText().toString();

                    //Gravar Medição 1
                    myDb.updateMedicao1(
                            os_id,
                            checklist_id,
                            id_Atividade,
                            medicao1
                    );

                }
            });
            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    dialog.dismiss();

                }
            });
            alert.show();

            return true;
        }

        //Se pressionar para voltar atividade
        if(id == android.R.id.home) {
            Intent intent = new Intent(Atividade_Antes.this, TelaAtividades.class);
            Bundle dados = new Bundle();
            dados.putString("os_id", os_id);
            dados.putString("checklist_id", checklist_id);
            dados.putString("equipamento_id", equipamento_id);
            dados.putString("local_id", local_id);
            dados.putString("tiposervico", tiposervico);
            dados.putString("frequencia_id", frequencia_id);
            dados.putString("dataplanejamento", dataplanejamento);
            dados.putString("centrocusto_id", id_centrolucro);
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("colaborador_id", colaborador_id);
            dados.putString("token", token);
            intent.putExtras(dados);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PIC_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Atividade_Antes.this,  "Não foi possivel salvar foto", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Override
    public void onBackPressed() {

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        String name = pref.getString("name", "");
        String email= pref.getString("email", "");
        String colaborador_id = pref.getString("id", "" );
        String token = pref.getString("token", "");

        Intent intent = new Intent(Atividade_Antes.this, TelaAtividades.class);
        Bundle dados = new Bundle();
        dados.putString("os_id", os_id);
        dados.putString("checklist_id", checklist_id);
        dados.putString("equipamento_id", equipamento_id);
        dados.putString("local_id", local_id);
        dados.putString("tiposervico", tiposervico);
        dados.putString("frequencia_id", frequencia_id);
        dados.putString("dataplanejamento", dataplanejamento);
        dados.putString("centrocusto_id", id_centrolucro);
        dados.putString("name", name);
        dados.putString("email", email);
        dados.putString("colaborador_id", colaborador_id);
        dados.putString("token", token);
        intent.putExtras(dados);
        startActivity(intent);
    }


    public void lerVoz() {

        Intent intent4 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Favor descrever observação. Ex: Abertura Loja 10:00. ");
        try {
            startActivityForResult(intent4, REQ_CODE_SPEAK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "O seu telefone não suporta a utilização do microfone. ", Toast.LENGTH_LONG).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:

                    File foto1a = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto1a);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg"));
                        imageView.setImageBitmap(BitmapFactory.decodeFile(foto1a.getAbsolutePath()));

                        myDb.updateFotoCaminho1a(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1A.jpg");

                        //Colocar status da Atividade de Pendente
                        myDBGeral.updateStatusAtividade(checklist_id, id_Atividade, "pendente");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    File foto2a = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto2a);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg"));
                        imageView2.setImageBitmap(BitmapFactory.decodeFile(foto2a.getAbsolutePath()));

                        myDb.updateFotoCaminho2a(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2A.jpg");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    File foto3a = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto3a);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg"));
                        imageView3.setImageBitmap(BitmapFactory.decodeFile(foto3a.getAbsolutePath()));

                        myDb.updateFotoCaminho3a(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3A.jpg");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case REQ_CODE_SPEAK:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultado = result.get(0);
                    txtObservacao.setText(resultado);
                    break;
            }
        }
    }
    //CODE GOOGLE MAPS - LOCALIZAÇÃO
    private synchronized  void callConection() {
        mGoogleApiClient = new GoogleApiClient.Builder(Atividade_Antes.this)
                .addOnConnectionFailedListener(Atividade_Antes.this)
                .addConnectionCallbacks(Atividade_Antes.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ContextCompat.checkSelfPermission(Atividade_Antes.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(Atividade_Antes.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if(l != null) {
            Log.i("LOG", "Latitude" + l.getLatitude());
            Log.i("LOG", "Longtitude" + l.getLongitude());

            txtLatitude.setText( "" + l.getLatitude());
            txtLongitude.setText("" + l.getLongitude());
        }else {
            txtLatitude.setVisibility(View.INVISIBLE);
            txtLongitude.setVisibility(View.INVISIBLE);
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
        Log.i("LOG", "onConnectionFailed(" + connectionResult +")");
    }

}

