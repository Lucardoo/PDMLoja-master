package br.edu.ifsul.loja.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifsul.loja.R;
import br.edu.ifsul.loja.model.Cliente;

public class ClientesAdapter extends ArrayAdapter<Cliente> {

    private static final String TAG = "clientesAdapter";
    private final Context context;

    public ClientesAdapter(@NonNull Context context, @NonNull List<Cliente> clientes){
        super(context, 0, clientes);
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        final ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.cliente_adapter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Cliente cliente = getItem(position);
        holder.tvNome.setText(cliente.getNome() + " " + cliente.getSobrenome());
        holder.tvDetalhes.setText("Código: " + cliente.getCodigoDeBarras() + "\nCPF.: " + cliente.getCpf());
        holder.pbFotoCliente.setVisibility(ProgressBar.VISIBLE);
        holder.imvFoto.setImageResource(R.drawable.img_cliente_icon_524x524);
        if(cliente.getUrl_foto().equals("")){
            holder.pbFotoDoCliente.setVisibility(View.INVISIBLE);
        }else{
            //faz o downlod da foto aqui, usando API do serviço Storage
        }

        return convertView;

    }

    private class ViewHolder{
        final TextView tvNome;
        final TextView tvSobrenome;
        final TextView tvCpf;
        final ImageView imvFoto;
        final ProgressBar pbFotoDoCliente;
        final TextView tvDetalhes;

        public ViewHolder(View view){
            tvDetalhes = view.findViewById(R.id.tvDetalhesDoClienteAdapater);
            tvNome = view.findViewById(R.id.tvNomeClienteAdapter);
            tvSobrenome = view.findViewById(R.id.tvSobrenomeClienteAdapater);
            tvCpf = view.findViewById(R.id.tvCpfClienteAdapter);
            imvFoto = view.findViewById(R.id.imvFotoProdutoAdapter);
            pbFotoDoCliente = view.findViewById(R.id.pb_foto_clientes_adapter);
        }
    }

}
