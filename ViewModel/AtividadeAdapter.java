package br.com.aaemobile.aaemobile.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.aaemobile.aaemobile.R;
import br.com.aaemobile.aaemobile.Visitas.Atividade_Antes;
import br.com.aaemobile.aaemobile.banco.BancoGeral;
import br.com.aaemobile.aaemobile.banco.DatabaseHelper;

import static android.content.Context.MODE_PRIVATE;

public class AtividadeAdapter extends RecyclerView.Adapter<AtividadeAdapter.GroceryViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private String tiposervico,  frequencia_id, tiposervico_id, tiposolicitacaoDescricao, tiposervicoDescricao, checklist_id, local_id, dataplanejamento, equipamento_id, os_id, id_centrolucro, atividade;

    BancoGeral myBDGeral;
    DatabaseHelper myBDOperacao;


    public AtividadeAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;

        myBDGeral = new BancoGeral(mContext);
        myBDOperacao = new DatabaseHelper(mContext);

        SharedPreferences pref = mContext.getSharedPreferences("visita", MODE_PRIVATE);
        os_id = pref.getString("os_id", "");
        equipamento_id = pref.getString("equipamento_id", "" );
        checklist_id = pref.getString("checklist_id", "");
        local_id = pref.getString("local_id", "");
        dataplanejamento = pref.getString("dataplanejamento", "");
        tiposervico = pref.getString("tiposervico", "");
        id_centrolucro = pref.getString("id_centrolucro", "");
        frequencia_id = pref.getString("frequencia_id", "");


    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {
        public TextView descricao;
        public TextView txtStatusAtividade;
        public TextView horarioatividade;

        public GroceryViewHolder(View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.descricaoatividade);
            txtStatusAtividade = itemView.findViewById(R.id.txtStatusAtividade);
            horarioatividade = itemView.findViewById(R.id.horarioatividade);

        }
    }

    @Override
    public AtividadeAdapter.GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.atividade_row_line , parent, false);
        return new AtividadeAdapter.GroceryViewHolder(view);

    }

    @Override
    public void onBindViewHolder(AtividadeAdapter.GroceryViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }


        //Se a atividade tiver em aberto ficar VERMELHO

        Cursor dataLocal = myBDGeral.buscaAtividadeParaRealizar(checklist_id);
        while (dataLocal.moveToNext()) {

            String idAtividades = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_ID_ITEN));
            String descricaoAtividade = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_DESCRICAO_ITEN));
            String status = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_STATUS_ITEN));

            holder.descricao.setText(descricaoAtividade);

            if (status.equals("aberta")) {
                holder.txtStatusAtividade.setText("A");
                holder.txtStatusAtividade.setTextColor(Color.RED);
                holder.horarioatividade.setText("Inicio: 00:00:00 - Fim: 00:00:00");
            }
            //Se a atividade tiver em aberto ficar em amarelo
            else if(status.equals("pendente")) {
                holder.txtStatusAtividade.setText("P");
                holder.txtStatusAtividade.setTextColor(Color.YELLOW);

                //Pega horario inicio
                Cursor buscaAtividadePendente = myBDOperacao.buscaAtividadePendente(idAtividades, os_id);
                while (buscaAtividadePendente.moveToNext()) {

                    String inicio = buscaAtividadePendente.getString(10);
                    String horarioInicio = inicio.substring(inicio.length()-8);
                    holder.horarioatividade.setText("Inicio: " + horarioInicio + " - Fim: 00:00:00");
                }
                buscaAtividadePendente.close();

            }
            //Se a atividade tiver finalizada ficar de verde
            else if (status.equals("encerrada")) {

                holder.txtStatusAtividade.setText("E");
                holder.txtStatusAtividade.setTextColor(Color.GREEN);

                //Pega horario inicio e fim
                Cursor buscaAtividadeEncerrada = myBDOperacao.buscaAtividadeEncerrada(idAtividades, os_id);
                while (buscaAtividadeEncerrada.moveToNext()) {

                    String inicio = buscaAtividadeEncerrada.getString(10);
                    String fim = buscaAtividadeEncerrada.getString(11);
                    String horarioInicio = inicio.substring(inicio.length()-8);
                    String horarioFim = inicio.substring(fim.length()-8);

                    holder.horarioatividade.setText("Inicio: " + horarioInicio + " - Fim: " + horarioFim);
                }
                buscaAtividadeEncerrada.close();

            }


            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currentTime = sdf.format(dt);

            holder.itemView.setOnClickListener(v -> { // Linguagem Java 8

                Cursor data = myBDOperacao.verificaAtividade(os_id, checklist_id, idAtividades);
                if (data.moveToNext()) {

                    Intent intent = new Intent(mContext, Atividade_Antes.class);
                    Bundle dados = new Bundle();
                    dados.putString("os_id", os_id);
                    dados.putString("checklist_id", checklist_id);
                    dados.putString("equipamento_id", equipamento_id);
                    dados.putString("local_id", local_id);
                    dados.putString("dataplanejamento", dataplanejamento);
                    dados.putString("tiposervico", tiposervico);
                    dados.putString("id_centrolucro", id_centrolucro);
                    dados.putString("id_Atividade", idAtividades);
                    dados.putString("frequencia_id", frequencia_id);
                    dados.putString("atividade", descricaoAtividade);
                    intent.putExtras(dados);
                    mContext.startActivity(intent);
                }
                //Se não tiver ele irá da insert
                else {
                    boolean isInserted = myBDOperacao.iniciaAtividade(
                            os_id,
                            checklist_id,
                            idAtividades,
                            currentTime,
                            "no");

                    if (isInserted == true) {

                        Intent intent = new Intent(mContext, Atividade_Antes.class);
                        Bundle dados = new Bundle();
                        dados.putString("os_id", os_id);
                        dados.putString("checklist_id", checklist_id);
                        dados.putString("equipamento_id", equipamento_id);
                        dados.putString("local_id", local_id);
                        dados.putString("dataplanejamento", dataplanejamento);
                        dados.putString("tiposervico", tiposervico);
                        dados.putString("id_centrolucro", id_centrolucro);
                        dados.putString("id_Atividade", idAtividades);
                        dados.putString("frequencia_id", frequencia_id);
                        dados.putString("atividade", descricaoAtividade);
                        intent.putExtras(dados);
                        mContext.startActivity(intent);
                    }
                }

            });
        }
    }


    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        //try using this
        if(mCursor==null)
            return 0;
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

}