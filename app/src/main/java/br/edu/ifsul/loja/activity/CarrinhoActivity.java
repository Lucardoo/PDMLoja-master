package br.edu.ifsul.loja.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifsul.loja.R;
import br.edu.ifsul.loja.adapter.CarrinhoAdapter;
import br.edu.ifsul.loja.model.ItemPedido;
import br.edu.ifsul.loja.model.Pedido;
import br.edu.ifsul.loja.setup.AppSetup;


public class CarrinhoActivity extends AppCompatActivity {
    private ListView lv_carrinho;
    private TextView tvClienteCarrinho;
    private TextView tvTotalPedidoCarrinho;
    public int i;
    private Double valorTotal = new Double(0);
    private List<ItemPedido> itens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carrinho);

        lv_carrinho = findViewById(R.id.lv_carrinho);

        tvClienteCarrinho = findViewById(R.id.tvClienteCarrinho);
        tvTotalPedidoCarrinho = findViewById(R.id.tvTotalPedidoCarrinho);
        tvClienteCarrinho.setText(String.valueOf("Cliente: " + AppSetup.cliente.getNome().concat(" " + AppSetup.cliente.getSobrenome())));

        lv_carrinho.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem("Atenção", "Você deseja excluir esse item?", position);

                return true;
            }
        });

        lv_carrinho.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editItem(position, "Editar item", "Você quer editar o item?");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_carrinho, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_salvar_pedido:
                saveOrder("Processando...", "\nTotal = " + NumberFormat.getCurrencyInstance().format(valorTotal) + ". Confirmar?");
                break;
            case R.id.menuitem_cancelar_pedido:
                cancelOrder("Cancelamento de Pedido", "Você realmente deseja cancelar o pedido?");
                break;
        }
        return true;
    }

    private void editItem(final int position, String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertmsg_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refreshStock(position);

                Intent intent = new Intent(CarrinhoActivity.this, ProdutoDetalheActivity.class);
                int index = AppSetup.carrinho.get(position).getProduto().getIndex();

                intent.putExtra("position", index);

                startActivity(intent);

                AppSetup.carrinho.remove(position);

                finish();
            }
        });

        builder.setNegativeButton(R.string.alertmsg_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private void refreshView(){
        lv_carrinho.setAdapter(new CarrinhoAdapter(CarrinhoActivity.this, AppSetup.carrinho));

        valorTotal = new Double(0);

        for(ItemPedido itemPedido : AppSetup.carrinho){
            valorTotal += itemPedido.getTotalItem();
        }

        tvTotalPedidoCarrinho.setText(NumberFormat.getCurrencyInstance().format(valorTotal));
    }

    private void refreshStock(final int position){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("vendas/produtos/").child(itens.get(position).getProduto().getKey()).child("quantidade");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long quantidade = (long) dataSnapshot.getValue();

                myRef.setValue(itens.get(position).getQuantidade() + quantidade);

                refreshView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveOrder(String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertmsg_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("vendas/pedidos/");

                Pedido pedido = new Pedido();


                pedido.setFormaDePagamento("espécie");
                pedido.setEstado("aberto");

                pedido.setTotalPedido(valorTotal);
                pedido.setSituacao(true);
                pedido.setItens(AppSetup.carrinho);
                pedido.setCliente(AppSetup.cliente);

                myRef.push().setValue(pedido);

                AppSetup.carrinho.clear();
                AppSetup.cliente = null;

                Toast.makeText(CarrinhoActivity.this, "Vendido com sucesso!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        builder.setNegativeButton(R.string.alertmsg_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CarrinhoActivity.this, "Venda cancelada!", Toast.LENGTH_SHORT).show();
            }
        });


        builder.show();
    }

    private void cancelOrder(String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertmsg_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int j = 0; j < itens.size(); j++){
                    refreshStock(i);
                }

                AppSetup.carrinho.clear();
                AppSetup.cliente = null;

                Toast.makeText(CarrinhoActivity.this, "Pedido cancelado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton(R.string.alertmsg_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CarrinhoActivity.this, "Operação cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void deleteItem(String titulo, String mensagem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertmsg_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSetup.carrinho.remove(position);
                Toast.makeText(CarrinhoActivity.this, "Produto removido!", Toast.LENGTH_SHORT).show();

                refreshView();
                refreshStock(position);
            }
        });

        builder.show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!AppSetup.carrinho.isEmpty()){
            refreshView();
        }
        itens = new ArrayList<>();
        itens.addAll(AppSetup.carrinho);
    }
}