package br.com.aaemobile.aaemobile.sync;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import br.com.aaemobile.aaemobile.TelaLogin;


public class Permissões {

    public static void Request_Camera(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.CAMERA},code);
    }

    public static void Request_Dados(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.READ_EXTERNAL_STORAGE},code);
    }


    public static void Request_WriteDados(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},code);
    }

    public static void Request_FINE_LOCATION(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.ACCESS_FINE_LOCATION},code);
    }

    public static void Request_SEND_SMS(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.SEND_SMS},code);
    }

    public static void Request_WRITE_SMS(TelaLogin act, int code)
    {
        ActivityCompat.requestPermissions(act, new
                String[]{Manifest.permission.BROADCAST_SMS},code);
    }



    public static boolean validarPermissoes(String[] permissoes, TelaLogin activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23 ){

            List<String> listaPermissoes = new ArrayList<>();

            /*Percorre as permissões passadas,
            verificando uma a uma
            * se já tem a permissao liberada */
            for ( String permissao : permissoes ){
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if ( !temPermissao ) listaPermissoes.add(permissao);
            }

            /*Caso a lista esteja vazia, não é necessário solicitar permissão*/
            if ( listaPermissoes.isEmpty() ) return true;
            String[] novasPermissoes = new String[ listaPermissoes.size() ];
            listaPermissoes.toArray( novasPermissoes );

            //Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode );

        }

        return true;

    }
}


