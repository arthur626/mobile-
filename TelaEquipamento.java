package br.com.aaemobile.aaemobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import br.com.aaemobile.aaemobile.ViewModel.EquipamentoAdapter;
import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.model.MyDividerItemDecoration;

public class TelaEquipamento extends AppCompatActivity {

    private static final String TAG = TelaEquipamento.class.getSimpleName();

    private EquipamentoAdapter mAdapter;
    private SearchView searchView;
    private String local_id, centrolucro_id;
    BancoGeral bancoEquipamento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainequipamento);


        bancoEquipamento = new BancoGeral(this);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();

        local_id = dados.getString("local_id");
        centrolucro_id = dados.getString("centrolucro_id");


        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meus Equipamentos");

        RecyclerView recyclerView = findViewById(R.id.recycler_viewequipamento);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        mAdapter = new EquipamentoAdapter(this, bancoEquipamento.getDataEquipamentoOrdem(local_id));
        recyclerView.setAdapter(mAdapter);



        // white background notification bar
        whiteNotificationBar(recyclerView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }


        if(id == android.R.id.home) {
            SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
            String name = pref.getString("name", "");
            String email= pref.getString("email", "");
            String colaborador_id = pref.getString("id", "" );
            String token = pref.getString("token", "");


            Intent intent = new Intent(TelaEquipamento.this, TelaLocal.class);
            Bundle dados = new Bundle();
            dados.putString("local_id", local_id);
            dados.putString("centrolucro_id", centrolucro_id);
            dados.putString("name", name);
            dados.putString("email", email);
            dados.putString("id", colaborador_id);
            dados.putString("token", token);
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
        String tipo = pref.getString("tipo", "");

        Intent intent = new Intent(TelaEquipamento.this, TelaLocal.class);
        Bundle dados = new Bundle();
        dados.putString("local_id", local_id);
        dados.putString("centrolucro_id", centrolucro_id);
        dados.putString("name", name);
        dados.putString("email", email);
        dados.putString("id", colaborador_id);
        dados.putString("tipo", tipo);
        intent.putExtras(dados);
        startActivity(intent);
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

}

