package br.com.aaemobile.aaemobile.ViewModel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import br.com.aaemobile.aaemobile.R;
import br.com.aaemobile.aaemobile.TelaAbrirCorretiva;
import br.com.aaemobile.aaemobile.TelaEquipamento;
import br.com.aaemobile.aaemobile.Visitas.VisitasLocal;
import br.com.aaemobile.aaemobile.banco.BancoGeral;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.GroceryViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    BancoGeral myBDGeral;



    public ContactsAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        myBDGeral = new BancoGeral(mContext);

    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView descricao;
        public TextView bairro;
        public TextView cidade;
        public TextView enderecolocal;
        public TextView quantVisitasLocal;

        public GroceryViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name);
            descricao = itemView.findViewById(R.id.phone);
            bairro = itemView.findViewById(R.id.bairrolocal);
            cidade = itemView.findViewById(R.id.cidadelocal);
            enderecolocal = itemView.findViewById(R.id.enderecolocal);
            quantVisitasLocal = itemView.findViewById(R.id.quantVisitasLocal);
        }
    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.local_row_item, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String id = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_ID_LOCAL));
        String name = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_CODIGO_LOCAL));
        String descricao = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_DESCRICAO_LOCAL));
        String bairro = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_BAIRRO_LOCAL));
        String cidade = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_CIDADE_LOCAL));
        String centrolucro_id = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_CENTROCUSTO_LOCAL));
        String enderecolocal = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_ENDERECO_LOCAL));
        String latitude_local = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_LATITUDE_LOCAL));
        String longitude_local = mCursor.getString(mCursor.getColumnIndex(BancoGeral.COL_LONGITUDE_LOCAL));


        holder.nameText.setText("Codigo: " + name);
        holder.descricao.setText("Descrição: " + descricao);
        holder.enderecolocal.setText("Endereço: " + enderecolocal);
        holder.bairro.setText("Bairro: " + bairro);
        holder.cidade.setText("Cidade: " + cidade);
        holder.quantVisitasLocal.setText("Visitas Abertas: " + String.valueOf(myBDGeral.dbCountAbertasLocalID(id)));

        holder.itemView.setOnClickListener(v -> { // Linguagem Java 8


            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Você deseja: ");
            builder.setIcon(R.drawable.logo);
            String[] opcoes = {"Ver visitas em aberto", "Vizualizar equipamentos"};
            builder.setItems(opcoes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        // Se para ver as Visitas em Aberto
                        case 0:
                            Intent intent = new Intent(mContext, VisitasLocal.class);
                            Bundle dados = new Bundle();
                            dados.putString("local_id", id);
                            dados.putString("centrolucro_id", centrolucro_id);
                            intent.putExtras(dados);
                            mContext.startActivity(intent);
                            break;

                        // Se marcar para abrir visita
                        //Coloquei por enquanto equipamento para abertura de OS LOCAL 9999
                      /*  case 1:
                            Intent intentAbrirVisita = new Intent(mContext, TelaAbrirCorretiva.class);
                            Bundle dadosAbrirVisita = new Bundle();
                            dadosAbrirVisita.putString("equipamento_id", "99999");
                            dadosAbrirVisita.putString("local_id", id);
                            dadosAbrirVisita.putString("centrolucro_id", centrolucro_id);
                            intentAbrirVisita.putExtras(dadosAbrirVisita);
                            mContext.startActivity(intentAbrirVisita);
                            break;*/

                        // Se marcar para ver Os TelaEquipamento do Local
                        case 1:
                            Intent intentEquipamentos = new Intent(mContext, TelaEquipamento.class);
                            Bundle dadosEquipamentos = new Bundle();
                            dadosEquipamentos.putString("local_id", id);
                            dadosEquipamentos.putString("centrolucro_id", centrolucro_id);
                            intentEquipamentos.putExtras(dadosEquipamentos);
                            mContext.startActivity(intentEquipamentos);
                            break;

                        // Se marcar para ver o Trajeto do local

                       /* case 3:
                            Intent intentTrajeto = new Intent(mContext, TrajetoLocal.class);
                            Bundle dadosTrajeto = new Bundle();
                            dadosTrajeto.putString("latitude_local", latitude_local);
                            dadosTrajeto.putString("longitude_local", longitude_local);
                            dadosTrajeto.putString("local_id", id);
                            dadosTrajeto.putString("centrolucro_id", centrolucro_id);
                            intentTrajeto.putExtras(dadosTrajeto);
                            mContext.startActivity(intentTrajeto);
                            break;*/
                    }
                }
            });
            AlertDialog dialogTipoSolicitacao = builder.create();
            dialogTipoSolicitacao.show();
        });
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