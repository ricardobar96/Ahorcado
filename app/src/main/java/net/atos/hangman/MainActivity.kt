package net.atos.hangman

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Random

class MainActivity : AppCompatActivity() {
    var palabraBuscar: String? = null
    lateinit var respuestas: CharArray
    var errores = 0
    var dificultad = "Normal"
    var puntuacion = 0
    private val letras = ArrayList<String>()
    private var imagen: ImageView? = null
    private var palabraTV: TextView? = null
    private var buscarTV: TextView? = null
    private var puntuacionTV: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagen = findViewById(R.id.img)
        palabraTV = findViewById(R.id.palabraTV)
        buscarTV = findViewById(R.id.buscarTV)
        puntuacionTV = findViewById(R.id.puntuacionTV)
        nuevaPartida()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Método que según la opción elegida en el menú cambia la dificultad e inicia una nueva
     * partida en dicha dificultad
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuFacil -> {
                dificultad = "Facil"
                nuevaPartida()
            }

            R.id.menuNormal -> {
                dificultad = "Normal"
                nuevaPartida()
            }

            R.id.menuDificil -> {
                dificultad = "Dificil"
                nuevaPartida()
            }

            R.id.menuSorpresa -> {
                val dificultades = arrayOf("Facil", "Normal", "Dificil")
                dificultad = dificultades[random.nextInt(dificultades.size)]
                nuevaPartida()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Método que en base al valor de "dificultad" selecciona la palabra de un array determinado
     * @return String que representa la palabra que tendrá que adivinar el jugador
     */
    private fun elegirPalabra(): String {
        if (dificultad == "Facil") {
            return palabrasFacil[random.nextInt(palabrasFacil.size)]
        }
        return if (dificultad == "Dificil") {
            palabrasDificil[random.nextInt(palabrasDificil.size)]
        } else palabrasMedio[random.nextInt(palabrasMedio.size)]
    }

    /**
     * Método que actualiza la imagen del ahorcado según los errores cometidos por el jugador
     * @param estado Integer que representa el número de errores cometidos en la partida actual
     */
    private fun actualizarImagen(estado: Int) {
        val resource = resources.getIdentifier("estado$estado", "drawable",
                packageName)
        imagen!!.setImageResource(resource)
    }

    /**
     * Método que inicia una partida nueva, reseteando el número de errores cometidos en la partida
     * anterior, la lista de letras elegidas por el jugador al intentar adivinar la palabra, el
     * estado de la imagen del ahorcado y los mensajes de la vista, además de elegir una nueva
     * palabra a buscar con el método elegirPalabra
     */
    fun nuevaPartida() {
        errores = -1
        letras.clear()
        palabraBuscar = elegirPalabra()
        respuestas = CharArray(palabraBuscar!!.length)
        for (i in respuestas.indices) {
            respuestas[i] = '_'
        }
        actualizarImagen(errores)
        palabraTV!!.text = estadoPalabra()
        buscarTV!!.text = ""
    }

    /**
     * Método que lee la letra seleccionada por el jugador. Si la palabra a buscar no contiene esta
     * letra, suma un error, pero si la palabra a buscar contiene esta letra se detecta en qué
     * posición de la palabra se encuentra y se rellenan dichas posiciones en la pista mostrada.
     * Por último se añade la letra a la lista de letras introducidas en la partida
     * @param c String que representa la letra seleccionada por el jugador
     */
    private fun leerLetra(c: String) {
        if (!letras.contains(c)) {
            if (palabraBuscar!!.contains(c)) {
                var index = palabraBuscar!!.indexOf(c)
                while (index >= 0) {
                    respuestas[index] = c[0]
                    index = palabraBuscar!!.indexOf(c, index + 1)
                }
            } else {
                errores++
            }
            letras.add(c)
        } else {
            Toast.makeText(this, "Letra ya introducida", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que devuelve el estado de la palabra a encontrar por el jugador, variable según las
     * letras adivinadas por este
     * @return String que representa el estado de la palabra a adivinar
     */
    private fun estadoPalabra(): String {
        val builder = StringBuilder()
        for (i in respuestas.indices) {
            builder.append(respuestas[i])
            if (i < respuestas.size - 1) {
                builder.append(" ")
            }
        }
        return builder.toString()
    }

    /**
     * Método que al seleccionar una letra muestra en la vista la pista de la palabra a adivinar
     * actualizada en base a las letras acertadas por el jugador, el mensaje "¡HAS GANADO!" si se
     * completa la pista antes de sobrepasar el máximo de errores y el mensaje "Has perdido..." si
     * no se logra esto último antes de 7 errores. Además actualiza el valor de la puntuación con
     * cada victoria según la dificultad elegida
     * @param v
     */
    fun tocarLetra(v: View) {
        if (errores < 6 && buscarTV!!.text != "¡HAS GANADO!") {
            val letra = (v as Button).text.toString()
            leerLetra(letra)
            palabraTV!!.text = estadoPalabra()
            actualizarImagen(errores)
            if (palabraBuscar.contentEquals(String(respuestas))) {
                Toast.makeText(this, "¡HAS GANADO!", Toast.LENGTH_SHORT).show()
                buscarTV!!.text = "¡HAS GANADO!"
                if (dificultad == "Dificil") {
                    puntuacion += 15
                }
                if (dificultad == "Normal") {
                    puntuacion += 10
                }
                if (dificultad == "Facil") {
                    puntuacion += 5
                }
                puntuacionTV!!.text = "Puntuación: $puntuacion"
            } else {
                if (errores >= 6) {
                    actualizarImagen(7)
                    Toast.makeText(this, "Has perdido...", Toast.LENGTH_SHORT).show()
                    buscarTV!!.text = palabraBuscar
                }
            }
        } else {
            Toast.makeText(this, "Fin del juego", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que inicia una nueva partida con la dificultad seleccionada al pulsar el botón
     * correspondiente
     * @param view
     */
    fun comenzar(view: View?) {
        nuevaPartida()
    }

    companion object {
        /**
         * Lista de palabras usadas en la dificultad Fácil
         */
        val palabrasFacil = arrayOf(
                "PERA", "MOTO", "PALO", "LOCO", "RIMA", "REMO", "CARDO", "CAMA", "PESO",
                "AMOR", "ROTO", "FALSO", "BURRO", "FLOR", "NOTA", "COCHE", "LIBRO", "PRESO",
                "CANOA", "FIDEO", "CARNE", "RATON", "MANTEL", "SOBRE", "PERRO", "GATO", "LUNA",
                "PATO", "CABO", "ROJO", "POLEA", "MIRLO", "JAMON", "MONJA", "CRUZ", "GRIMA",
                "MONTE", "SOL", "LORO", "MOSCA", "TELON", "CITA", "MENTA", "CARRO", "MANO"
        )

        /**
         * Lista de palabras usadas en la dificultad Normal
         */
        val palabrasMedio = arrayOf(
                "BRUJULA", "TRICICLO", "LOTERIA", "MAIZAL", "PROFESOR", "PIZARRA", "SABADO",
                "GENESIS", "POESIA", "DIBUJO", "LIBRERIA", "PESCADO", "PANDILLA", "COMICO",
                "ESTADO", "MONEDA", "BILLETE", "INCENDIO", "EMPRESA", "TRABAJO", "MENDRUGO",
                "ROCIO", "ARROZ", "LLUVIA", "ACAMPAR", "TENDERO", "CAUCE", "TERROR", "TORTUGA",
                "NADADOR", "PERSONA", "HOSTAL", "HELADO", "CORRAL", "AMPARO", "EXTRAÑO", "FRIO"
        )

        /**
         * Lista de palabras usadas en la dificultad Difícil
         */
        val palabrasDificil = arrayOf(
                "AUTOESTIMA", "EXISTENTIAL", "OPTATIVA", "POSIBILIDAD", "ENCERRONA", "IMAGINACION",
                "AISLAMIENTO", "CAVERNICOLA", "ORIENTACION", "PARABRISAS", "DIMENSION", "ALMOHADILLA",
                "ESPIONAJE", "PROGRAMACION", "APOCALIPSIS", "PREMONICION", "PERIODICO", "PRESENTADOR",
                "CRUCIGRAMA", "EMPERADOR", "PATRONAJE", "MOCHILERO", "MEDICINA", "SANITARIO", "AZUCENA",
                "PIZZERIA", "TABAQUERIA", "GASOLINERA", "EMPRENDEDOR", "RESPONSABILIDAD", "DISCIPLINA"
        )
        val random = Random()
    }
}