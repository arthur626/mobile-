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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.aaemobile.aaemobile.ViewModel.CentroLucroAdapter;
import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.model.CL;
import br.com.aaemobile.aaemobile.model.MyDividerItemDecoration;

public class TelaContrato extends AppCompatActivity {

    private List<CL> contactList;
    private CentroLucroAdapter mAdapter;
    private SearchView searchView;
    private RecyclerView recyclerView;
    BancoGeral bancoGeral;
    private ArrayAdapter adapter;
    private String name, colaborador_id, email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_contrato);



        bancoGeral = new BancoGeral(this);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();
        name = dados.getString("name");
        email = dados.getString("email");
        colaborador_id = dados.getString("colaborador_id");
        token = dados.getString("token");

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Meus Contratos");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_contrato);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        mAdapter = new CentroLucroAdapter(this, bancoGeral.getDataCL());
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
            Intent intent = new Intent(TelaContrato.this, TelaPrincipal.class);
            Bundle dados = new Bundle();
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

        Intent intent = new Intent(TelaContrato.this, TelaPrincipal.class);
        Bundle dados = new Bundle();
        dados.putString("name", name);
        dados.putString("email", email);
        dados.putString("id", colaborador_id);
        dados.putString("token", token);
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

