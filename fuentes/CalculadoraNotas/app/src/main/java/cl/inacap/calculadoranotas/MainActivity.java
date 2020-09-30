package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    private EditText notaTxt;
    private EditText procentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>(); //nombre de la lista que está en el ingresar notas
    private ArrayAdapter<Nota> adapter; //esto es la declaración del adaptador, que es un dapater de tipo nota

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.procentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv =  findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this    //acá está la inicialización de ese adapter
                , android.R.layout.simple_list_item_1, notas); //para el proyecto debemos crear este layout personalizado
        this.notasLv.setAdapter(adapter);

        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> errores = new ArrayList<>();
                String notaStr = notaTxt.getText().toString().trim();
                String porcStr = procentajeTxt.getText().toString().trim();
                int porcentaje = 0; //le doy un inicializador para que no me tire error
                double nota = 0;    //en el proceso de ingresar nota (linea 62)
                try {
                    nota = Double.parseDouble(notaStr);
                    if(nota < 1.0 || nota > 7.0){
                        throw new NumberFormatException();
                    }
                }catch (NumberFormatException ex){
                    errores.add("-La nota debe ser un número entre 1.0 y 7.0");
                }
                try {
                    porcentaje = Integer.parseInt(porcStr);
                        if(porcentaje < 1 || porcentaje > 100){
                            throw new NumberFormatException();
                        }
                }catch (NumberFormatException ex){
                    errores.add("-El porcentaje debe ser un numero entre 1 y 100");
                }
                if(errores.isEmpty()){               //ingresar nota y mostrarla en el Listview,
                    //proceso de ingresar nota      // para esto necesito un ArrayAdapter que lo hice en las lineas 26 y 37
                    Nota n = new Nota();            //con esto ya podria poder renderizarse, tiene que estar asociado al ListView
                    n.setValor(nota);               //para asociar el adapter con el listview abajo del this.adapter (linea 39)
                    n.setPorcentaje(porcentaje);
                    notas.add(n);
                    adapter.notifyDataSetChanged(); //cada vez que modifique el recurso asociado al adaptador debo debo llamar a este metodo
                }else{
                    mostrarErrores(errores);
                }

            }
        });
    }

    private void mostrarErrores(List<String> errores){
        //lo que hará este metodo será 1) generar una cadena de texto con errores
        String mensaje = " ";
        for(String e: errores){
            mensaje+="-" + e + "\n";
        }
        //2) mostrar un mje de alerta.
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        //esto se conoce como chaining o encadenamiento
        alertBuilder.setTitle("Error de Validación") //le digo al alertbuilder que defina el titulo del dialogo
                .setMessage(mensaje) //depués que defina el contenido del mje
                .setPositiveButton("Aceptar", null)  // define el boton aceptar
                .create() //esto crea el alert
                .show(); //esto muestra el alert
    }
}