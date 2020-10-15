
package br.com.aaemobile.aaemobile.Visitas;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import br.com.aaemobile.aaemobile.R;
import br.com.aaemobile.aaemobile.TelaAssinaturaTecnico;
import br.com.aaemobile.aaemobile.TelaAtividades;
import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.banco.DatabaseHelper;

import id.zelory.compressor.Compressor;

public class Atividade_Depois extends AppCompatActivity {


    DatabaseHelper myDb;
    BancoGeral myDBGeral;
    ImageView imageView, imageView2, imageView3;
    TextView txtLatitude, txtLongitude, txtObservacao, txtAtividade;
    private static final int CAMERA_PIC_REQUEST = 1111;
    private static final int CAMERA_PIC_REQUEST2 = 1111;
    private static final int CAMERA_PIC_REQUEST3 = 1111;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private String os_id, foto1a, foto2a, foto3a, foto1d, foto2d, foto3d, checklist_id, id_Atividade, email, idColaborador, name, token;
    private String update_Status, data, inicio, fim, frequencia_id;
    private String medicao2, dataehora, dataplanejamento, tiposervico, local_id, id_centrolucro, latitude, longitude, equipamento_id, sigla, observacaoantes, situacao, atividade, medida1;
    private int index;
    public static final int REQ_CODE_SPEAK = 100;
    private Uri currentURI;
    static Uri capturedImageUri = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();

        setContentView(R.layout.tela_atividadedepois);

        myDb = new DatabaseHelper(this);
        myDBGeral = new BancoGeral(this);

        imageView = (ImageView) findViewById(R.id.foto1d);
        imageView2 = (ImageView) findViewById(R.id.foto2d);
        imageView3 = (ImageView) findViewById(R.id.foto3d);
        txtObservacao = (TextView) findViewById(R.id.txtObservacaoDepois);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtAtividade = (TextView) findViewById(R.id.txtAtividadeDepois);

        // toolbar fancy stuff
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Atividade Depois");


        Intent intent = getIntent();
        Bundle dados = intent.getExtras();

        os_id = dados.getString("os_id");
        checklist_id = dados.getString("checklist_id");
        equipamento_id = dados.getString("equipamento_id");
        atividade = dados.getString("atividade");
        latitude = dados.getString("latitude");
        longitude = dados.getString("longitude");
        id_Atividade = dados.getString("id_Atividade");
        frequencia_id = dados.getString("frequencia_id");
        tiposervico = dados.getString("tiposervico");
        local_id = dados.getString("local_id");
        id_centrolucro = dados.getString("id_centrolucro");
        dataplanejamento = dados.getString("dataplanejamento");
        name = dados.getString("name");
        email = dados.getString("email");
        idColaborador = dados.getString("idColaborador");
        token = dados.getString("token");

        txtAtividade.setText(atividade);
        txtLatitude.setText(latitude);
        txtLongitude.setText(longitude);


        //Inicializar o Pop Up da Foto
        ImagePopup imagePopup2 = new ImagePopup(this);

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


        imagePopup2.setWindowHeight(420); // Optional
        imagePopup2.setWindowWidth(600); // Optional
        imagePopup2.setBackgroundColor(Color.WHITE);  // Optional
        imagePopup2.setFullScreen(true); // Optional
        imagePopup2.setHideCloseIcon(true);  // Optional
        imagePopup2.setImageOnClickClose(true);  // Optional

        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy");
        dataehora = sdf.format(dt);


        File imgFile = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg");
        File imgFile2 = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg");
        File imgFile3 = new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg");

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

        java.util.Date dt2 = new java.util.Date();
        java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.text.SimpleDateFormat sdfDataEncerramento = new java.text.SimpleDateFormat("yyyy/MM/dd");
        String currentTime = sdf2.format(dt2);
        String dataencerramento = sdfDataEncerramento.format(dt2);

        txtObservacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Depois.this)
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
                new AlertDialog.Builder(Atividade_Depois.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Verifica se a atividade é a de cartão de Ponto do Contrato OI = Atividade ID 430
                                File foto1d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg");

                                capturedImageUri = Uri.fromFile(foto1d);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 1);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView.setImageBitmap(null);
                                File imgFile = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg");
                                if(imgFile.exists()) {
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
                                    imagePopup2.initiatePopup(imageView.getDrawable()); // Load Image from Drawable
                                    imagePopup2.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Depois.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File foto2d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg");

                                capturedImageUri = Uri.fromFile(foto2d);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 2);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView2.setImageBitmap(null);
                                File imgFile = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg");
                                if(imgFile.exists()) {
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
                                    imagePopup2.initiatePopup(imageView2.getDrawable()); // Load Image from Drawable
                                    imagePopup2.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });


        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Atividade_Depois.this)
                        .setIcon(R.drawable.logo)
                        .setTitle(R.string.app_name)
                        .setMessage("Você Deseja:")
                        .setPositiveButton("Tirar Foto", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File foto3d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg");

                                capturedImageUri = Uri.fromFile(foto3d);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                startActivityForResult(intent, 3);

                            }
                        })
                        .setNegativeButton("Excluir Foto", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int witch) {

                                imageView3.setImageBitmap(null);
                                File imgFile = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg");
                                if(imgFile.exists()) {
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
                                    imagePopup2.initiatePopup(imageView3.getDrawable()); // Load Image from Drawable
                                    imagePopup2.viewPopup();

                                }
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_atividadesdepois, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentTime = sdf.format(dt);

        java.text.SimpleDateFormat sdfDataEncerramento =
                new java.text.SimpleDateFormat("yyyy/MM/dd");
        String dataencerramento = sdfDataEncerramento.format(dt);
        int int_tiposervico = Integer.parseInt(tiposervico);

        //Se clicar em continuar visita
        if (id == R.id.action_confirmar) {
            if (imageView.getDrawable() == null) {
                Toast.makeText(getApplicationContext(), "Você deve tirar pelo menos a 1ª Foto !", Toast.LENGTH_LONG).show();
            }
            //Se for uma visita corretiva ele irá direto para Assinatura
            else if(int_tiposervico == 2) {

                myDb.updateOS(
                        os_id,
                        txtObservacao.getText().toString(),
                        currentTime,
                        dataencerramento,
                        "/assets/os/" + os_id + "_assinaturaColaborador.jpg",
                        "no");

                //Encerra Atividade
                myDBGeral.updateStatusAtividade(
                        id_Atividade,
                        checklist_id,
                        "encerrada"
                );

                Intent intent = new Intent(Atividade_Depois.this, TelaAssinaturaTecnico.class);
                Bundle dados = new Bundle();
                dados.putString("os_id", os_id);
                dados.putString("checklist", checklist_id);
                dados.putString("equipamento_id", equipamento_id);
                dados.putString("local_id", local_id);
                dados.putString("idAtividade", id_Atividade);
                dados.putString("tiposervico", tiposervico);
                dados.putString("frequencia_id", frequencia_id);
                dados.putString("dataplanejamento", dataplanejamento);
                dados.putString("id_centrolucro", id_centrolucro);
                dados.putString("name", name);
                dados.putString("email", email);
                dados.putString("colaborador_id", idColaborador);
                dados.putString("token", token);
                intent.putExtras(dados);
                startActivity(intent);
            } else {
                myDb.updateOS(
                        os_id,
                        txtObservacao.getText().toString(),
                        currentTime,
                        dataencerramento,
                        "/assets/os/" + os_id + "_assinaturaColaborador.jpg",
                        "no");

                //Encerra Atividade
                myDBGeral.updateStatusAtividade(
                        checklist_id,
                        id_Atividade,
                        "encerrada"
                );

                Intent intent = new Intent(Atividade_Depois.this, TelaAtividades.class);
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
                dados.putString("colaborador_id", idColaborador);
                dados.putString("token", token);
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
                    medicao2 = input.getText().toString();

                    //Gravar Medição 1
                    myDb.updateMedicao2(
                            os_id,
                            checklist_id,
                            id_Atividade,
                            medicao2
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
        //Se clicar em voltar atividade Anterior
        if(id == android.R.id.home) {

            Intent intent = new Intent(Atividade_Depois.this, Atividade_Antes.class);
            Bundle dados = new Bundle();
            dados.putString("os_id", os_id);
            dados.putString("checklist_id", checklist_id);
            dados.putString("equipamento_id", equipamento_id);
            dados.putString("id_Atividade", id_Atividade);
            dados.putString("frequencia_id", frequencia_id);
            dados.putString("id_centrolucro", id_centrolucro);
            dados.putString("local_id", local_id);
            dados.putString("tiposervico", tiposervico);
            dados.putString("dataplanejamento", dataplanejamento);
            dados.putString("atividade", atividade);
            intent.putExtras(dados);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        String name = pref.getString("name", "");
        String email= pref.getString("email", "");
        String colaborador_id = pref.getString("id", "" );
        String token = pref.getString("token", "");

        Intent intent = new Intent(Atividade_Depois.this, TelaAtividades.class);
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
        dados.putString("token", token);
        intent.putExtras(dados);
        startActivity(intent);

    }



    public void lerVoz() {

        Intent intent4 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Favor descrever observação. Ex: Foi gasto 2 metros de cabo de rede. ");
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
                    File foto1d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto1d);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg"));
                        imageView.setImageBitmap(BitmapFactory.decodeFile(foto1d.getAbsolutePath()));

                        myDb.updateFotoCaminho1d(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "1D.jpg");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    File foto2d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto2d);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg"));
                        imageView2.setImageBitmap(BitmapFactory.decodeFile(foto2d.getAbsolutePath()));
                        myDb.updateFotoCaminho2d(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "2D.jpg");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    File foto3d = new  File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg");

                    try {
                        File compressedImage = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(foto3d);
                        compressedImage.renameTo(new File("/sdcard/PicturesHELPER/enviar/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg"));
                        imageView3.setImageBitmap(BitmapFactory.decodeFile(foto3d.getAbsolutePath()));

                        myDb.updateFotoCaminho3d(
                                os_id,
                                checklist_id,
                                id_Atividade,
                                "/assets/os/" + os_id + "_" + checklist_id + "_" + id_Atividade + "_" + "3D.jpg");

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

}

