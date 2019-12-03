package br.edu.ifsul.loja.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import br.edu.ifsul.loja.R;
import br.edu.ifsul.loja.setup.AppSetup;

public class CarrinhoActivity extends AppCompatActivity {

    private static final String TAG = "carrinhoActivity";
    private ListView lvCarrinho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);
        Log.d(TAG, "Carrinho=" + AppSetup.carrinho);

        //ativa o botão home na actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //tratamento de eventos
        lvCarrinho = findViewById(R.id.lv_carrinho);

        lvCarrinho.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CarrinhoActivity.this, "Clique curto.", Toast.LENGTH_SHORT).show();
            }
        });

        lvCarrinho.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CarrinhoActivity.this, "Clique longo.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //inflar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_carrinho, menu);
        return true; //chamada do método termina aqui
    }

    //tratar eventos do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_salvar_pedido:
                Toast.makeText(this, "Salvar", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.menuitem_cancelar_pedido:
                Toast.makeText(this, "Cancelar", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true; //chamada do método termina aqui
    }


    //excluir um item

    //editar um item

    //cancelar pedido

    //salvar pedido

    //rollback no estoque

    //limpar setup
}
