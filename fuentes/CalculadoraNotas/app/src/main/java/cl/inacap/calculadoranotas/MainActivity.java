package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    private int porcentajeActual=0;//acá defino una variable para el tema del %(que no sea más que el 100%)
    private EditText notaTxt;
    private EditText procentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>(); //nombre de la lista que está en el ingresar notas
    private ArrayAdapter<Nota> adapter; //esto es la declaración del adaptador, que es un dapater de tipo nota
    private TextView promedioTxt;//acá agrego las referencias para el linear layout hecho para el promedio
    private LinearLayout promedioLl; //acá la referencia del linear layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.promedioTxt = findViewById(R.id.promedioTxt); //acá hago de que el promedioTxt tenga un findView...
        this.promedioLl = findViewById(R.id.promedioLl);    //busco el promedioTxt/Ll y lo guardo como referencia
        this.notaTxt = findViewById(R.id.notaTxt);
        this.procentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv =  findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this    //acá está la inicialización de ese adapter
                , android.R.layout.simple_list_item_1, notas); //para el proyecto debemos crear este layout personalizado
        this.notasLv.setAdapter(adapter);
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notaTxt.setText("");//limpiar los EditText
                procentajeTxt.setText("");
                promedioLl.setVisibility(View.INVISIBLE);//ocultar los linearlayout de resultado
                porcentajeActual = 0; //volver el porcentaje acumulado a 0
                notas.clear();//limpiar lista
                adapter.notifyDataSetChanged();//notificar al adapter
            }
        });

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


                if(errores.isEmpty()) {
                    //ingresar nota y mostrarla en el Listview,para esto necesito un ArrayAdapter que lo hice en las lineas 26 y 37
                    if (porcentaje + porcentajeActual > 100) {    //esto me indica que el %es mayor que 100
                        Toast.makeText(MainActivity.this
                                , "el porcentaje no puede ser mayor que 100"
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        Nota n = new Nota();    //proceso de ingresar nota  con esto ya podria poder renderizarse, tiene que estar asociado al ListView}
                    n.setValor(nota);               //para asociar el adapter con el listview abajo del this.adapter (linea 39)
                    n.setPorcentaje(porcentaje);
                    porcentajeActual += porcentaje;
                    notas.add(n);
                    adapter.notifyDataSetChanged(); //cada vez que modifique el recurso asociado al adaptador debo debo llamar a este metodo
                    mostrarPromedio();
                    }
                } else {
                        mostrarErrores(errores);
                    }

            }
        });
    }

    private void mostrarPromedio(){
        double promedio = 0;
        for(Nota n: notas){
            promedio += (n.getValor()*n.getPorcentaje()/100);
        }
        this.promedioTxt.setText(String.format("%.1f",promedio)); //edito el valueOF por el format para que me mueste 1 o dos decimales
        if(promedio < 4.0){
           this.promedioTxt.setTextColor(ContextCompat.getColor
                   (this,R.color.colorverguenza)); //el promedio salga en rojo
        }else {
            this.promedioTxt.setTextColor(ContextCompat.getColor
                    (this, R.color.colorexito));//el promedio salga en azul
        }
        this.promedioLl.setVisibility(View.VISIBLE); //hago visivle el promedioLl y para que lo muestre debo agregar un mostrar en la linea 87
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