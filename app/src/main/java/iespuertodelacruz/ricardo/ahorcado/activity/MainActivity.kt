package iespuertodelacruz.ricardo.ahorcado.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import iespuertodelacruz.ricardo.ahorcado.R;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    /**
     * Lista de palabras usadas en la dificultad Fácil
     */
    public static final String[] palabrasFacil = {
            "PERA", "MOTO", "PALO", "LOCO", "RIMA", "REMO", "CARDO", "CAMA", "PESO",
            "AMOR", "ROTO", "FALSO", "BURRO", "FLOR", "NOTA", "COCHE", "LIBRO", "PRESO",
            "CANOA", "FIDEO", "CARNE", "RATON", "MANTEL", "SOBRE", "PERRO", "GATO", "LUNA",
            "PATO", "CABO", "ROJO", "POLEA", "MIRLO", "JAMON", "MONJA", "CRUZ", "GRIMA",
            "MONTE", "SOL", "LORO", "MOSCA", "TELON", "CITA", "MENTA", "CARRO", "MANO"
    };

    /**
     * Lista de palabras usadas en la dificultad Normal
     */
    public static final String[] palabrasMedio = {
            "BRUJULA", "TRICICLO", "LOTERIA", "MAIZAL", "PROFESOR", "PIZARRA", "SABADO",
            "GENESIS", "POESIA", "DIBUJO", "LIBRERIA", "PESCADO", "PANDILLA", "COMICO",
            "ESTADO", "MONEDA", "BILLETE", "INCENDIO", "EMPRESA", "TRABAJO", "MENDRUGO",
            "ROCIO", "ARROZ", "LLUVIA", "ACAMPAR", "TENDERO", "CAUCE", "TERROR", "TORTUGA",
            "NADADOR", "PERSONA", "HOSTAL", "HELADO", "CORRAL", "AMPARO", "EXTRAÑO", "FRIO"
    };

    /**
     * Lista de palabras usadas en la dificultad Difícil
     */
    public static final String[] palabrasDificil = {
            "AUTOESTIMA", "EXISTENTIAL", "OPTATIVA", "POSIBILIDAD", "ENCERRONA","IMAGINACION",
            "AISLAMIENTO", "CAVERNICOLA", "ORIENTACION", "PARABRISAS", "DIMENSION", "ALMOHADILLA",
            "ESPIONAJE", "PROGRAMACION", "APOCALIPSIS", "PREMONICION", "PERIODICO", "PRESENTADOR",
            "CRUCIGRAMA", "EMPERADOR", "PATRONAJE", "MOCHILERO", "MEDICINA", "SANITARIO", "AZUCENA",
            "PIZZERIA", "TABAQUERIA", "GASOLINERA", "EMPRENDEDOR", "RESPONSABILIDAD", "DISCIPLINA"
    };

    public static final Random random = new Random();
    String palabraBuscar;
    char[] respuestas;
    int errores;
    String dificultad = "Normal";
    int puntuacion = 0;
    private ArrayList<String> letras = new ArrayList<>();
    private ImageView imagen;
    private TextView palabraTV;
    private TextView buscarTV;
    private TextView puntuacionTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagen = findViewById(R.id.img);
        palabraTV = findViewById(R.id.palabraTV);
        buscarTV = findViewById(R.id.buscarTV);
        puntuacionTV = findViewById(R.id.puntuacionTV);
        nuevaPartida();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Método que según la opción elegida en el menú cambia la dificultad e inicia una nueva
     * partida en dicha dificultad
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menuFacil:
                dificultad = "Facil";
                nuevaPartida();
                break;
            case R.id.menuNormal:
                dificultad = "Normal";
                nuevaPartida();
                break;
            case R.id.menuDificil:
                dificultad = "Dificil";
                nuevaPartida();
                break;
            case R.id.menuSorpresa:
                String[] dificultades = {"Facil", "Normal", "Dificil"};
                dificultad = dificultades[random.nextInt(dificultades.length)];
                nuevaPartida();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método que en base al valor de "dificultad" selecciona la palabra de un array determinado
     * @return String que representa la palabra que tendrá que adivinar el jugador
     */
    private String elegirPalabra() {
        if(dificultad.equals("Facil")){
            return palabrasFacil[random.nextInt(palabrasFacil.length)];
        }
        if(dificultad.equals("Dificil")){
            return palabrasDificil[random.nextInt(palabrasDificil.length)];
        }
        return palabrasMedio[random.nextInt(palabrasMedio.length)];
    }

    /**
     * Método que actualiza la imagen del ahorcado según los errores cometidos por el jugador
     * @param estado Integer que representa el número de errores cometidos en la partida actual
     */
    private void actualizarImagen(int estado) {
        int resource = getResources().getIdentifier("estado" + estado, "drawable",
                getPackageName());
        imagen.setImageResource(resource);
    }

    /**
     * Método que inicia una partida nueva, reseteando el número de errores cometidos en la partida
     * anterior, la lista de letras elegidas por el jugador al intentar adivinar la palabra, el
     * estado de la imagen del ahorcado y los mensajes de la vista, además de elegir una nueva
     * palabra a buscar con el método elegirPalabra
     */
    public void nuevaPartida() {
        errores = -1;
        letras.clear();
        palabraBuscar = elegirPalabra();

        respuestas = new char[palabraBuscar.length()];

        for (int i = 0; i < respuestas.length; i++) {
            respuestas[i] = '_';
        }

        actualizarImagen(errores);
        palabraTV.setText(estadoPalabra());
        buscarTV.setText("");
    }

    /**
     * Método que lee la letra seleccionada por el jugador. Si la palabra a buscar no contiene esta
     * letra, suma un error, pero si la palabra a buscar contiene esta letra se detecta en qué
     * posición de la palabra se encuentra y se rellenan dichas posiciones en la pista mostrada.
     * Por último se añade la letra a la lista de letras introducidas en la partida
     * @param c String que representa la letra seleccionada por el jugador
     */
    private void leerLetra(String c) {
        if (!letras.contains(c)) {
            if (palabraBuscar.contains(c)) {
                int index = palabraBuscar.indexOf(c);

                while (index >= 0) {
                    respuestas[index] = c.charAt(0);
                    index = palabraBuscar.indexOf(c, index + 1);
                }
            } else {
                errores++;
            }

            letras.add(c);
        } else {
            Toast.makeText(this, "Letra ya introducida", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método que devuelve el estado de la palabra a encontrar por el jugador, variable según las
     * letras adivinadas por este
     * @return String que representa el estado de la palabra a adivinar
     */
    private String estadoPalabra() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < respuestas.length; i++) {
            builder.append(respuestas[i]);

            if (i < respuestas.length - 1) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    /**
     * Método que al seleccionar una letra muestra en la vista la pista de la palabra a adivinar
     * actualizada en base a las letras acertadas por el jugador, el mensaje "¡HAS GANADO!" si se
     * completa la pista antes de sobrepasar el máximo de errores y el mensaje "Has perdido..." si
     * no se logra esto último antes de 7 errores. Además actualiza el valor de la puntuación con
     * cada victoria según la dificultad elegida
     * @param v
     */
    public void tocarLetra(View v) {
        if (errores < 6 && (!buscarTV.getText().equals("¡HAS GANADO!"))) {
            String letra = ((Button) v).getText().toString();
            leerLetra(letra);
            palabraTV.setText(estadoPalabra());

            actualizarImagen(errores);

            if (palabraBuscar.contentEquals(new String(respuestas))) {
                Toast.makeText(this, "¡HAS GANADO!", Toast.LENGTH_SHORT).
                        show();
                buscarTV.setText("¡HAS GANADO!");
                if(dificultad.equals("Dificil")){
                    puntuacion += 15;
                }
                if(dificultad.equals("Normal")){
                    puntuacion += 10;
                }
                if(dificultad.equals("Facil")){
                    puntuacion += 5;
                }
                puntuacionTV.setText("Puntuación: " + puntuacion);

            } else {
                if (errores >= 6) {
                    actualizarImagen(7);
                    Toast.makeText(this, "Has perdido...", Toast.LENGTH_SHORT).show();
                    buscarTV.setText(palabraBuscar);
                }
            }
        } else {
            Toast.makeText(this, "Fin del juego", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método que inicia una nueva partida con la dificultad seleccionada al pulsar el botón
     * correspondiente
     * @param view
     */
    public void comenzar(View view) {
        nuevaPartida();
    }
}