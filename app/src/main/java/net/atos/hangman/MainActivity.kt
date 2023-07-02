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
    var wordSearch: String? = null
    lateinit var answers: CharArray
    var errors = 0
    var difficulty = "Normal"
    var score = 0
    private val letters = ArrayList<String>()
    private var image: ImageView? = null
    private var wordTV: TextView? = null
    private var searchTV: TextView? = null
    private var scoreTV: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image = findViewById(R.id.img)
        wordTV = findViewById(R.id.wordTV)
        searchTV = findViewById(R.id.searchTV)
        scoreTV = findViewById(R.id.scoreTV)
        newGame()
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
            R.id.menuEasy -> {
                difficulty = "Easy"
                newGame()
            }

            R.id.menuNormal -> {
                difficulty = "Normal"
                newGame()
            }

            R.id.menuHard -> {
                difficulty = "Hard"
                newGame()
            }

            R.id.menuRandom -> {
                val difficulties = arrayOf("Easy", "Normal", "Hard")
                difficulty = difficulties[random.nextInt(difficulties.size)]
                newGame()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Método que en base al valor de "dificultad" selecciona la palabra de un array determinado
     * @return String que representa la palabra que tendrá que adivinar el jugador
     */
    private fun chooseWord(): String {
        if (difficulty == "Easy") {
            return easyWords[random.nextInt(easyWords.size)]
        }
        return if (difficulty == "Hard") {
            hardWords[random.nextInt(hardWords.size)]
        } else normalWords[random.nextInt(normalWords.size)]
    }

    /**
     * Método que actualiza la imagen del ahorcado según los errores cometidos por el jugador
     * @param estado Integer que representa el número de errores cometidos en la partida actual
     */
    private fun updateImage(state: Int) {
        val resource = resources.getIdentifier("state$state", "drawable",
                packageName)
        image!!.setImageResource(resource)
    }

    /**
     * Método que inicia una partida nueva, reseteando el número de errores cometidos en la partida
     * anterior, la lista de letras elegidas por el jugador al intentar adivinar la palabra, el
     * estado de la imagen del ahorcado y los mensajes de la vista, además de elegir una nueva
     * palabra a buscar con el método elegirPalabra
     */
    fun newGame() {
        errors = -1
        letters.clear()
        wordSearch = chooseWord()
        answers = CharArray(wordSearch!!.length)
        for (i in answers.indices) {
            answers[i] = '_'
        }
        updateImage(errors)
        wordTV!!.text = stateWord()
        searchTV!!.text = ""
    }

    /**
     * Método que lee la letra seleccionada por el jugador. Si la palabra a buscar no contiene esta
     * letra, suma un error, pero si la palabra a buscar contiene esta letra se detecta en qué
     * posición de la palabra se encuentra y se rellenan dichas posiciones en la pista mostrada.
     * Por último se añade la letra a la lista de letras introducidas en la partida
     * @param c String que representa la letra seleccionada por el jugador
     */
    private fun readLetter(c: String) {
        if (!letters.contains(c)) {
            if (wordSearch!!.contains(c)) {
                var index = wordSearch!!.indexOf(c)
                while (index >= 0) {
                    answers[index] = c[0]
                    index = wordSearch!!.indexOf(c, index + 1)
                }
            } else {
                errors++
            }
            letters.add(c)
        } else {
            Toast.makeText(this, "Letter already entered", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que devuelve el estado de la palabra a encontrar por el jugador, variable según las
     * letras adivinadas por este
     * @return String que representa el estado de la palabra a adivinar
     */
    private fun stateWord(): String {
        val builder = StringBuilder()
        for (i in answers.indices) {
            builder.append(answers[i])
            if (i < answers.size - 1) {
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
    fun touchLetter(v: View) {
        if (errors < 6 && searchTV!!.text != "YOU WIN!") {
            val letter = (v as Button).text.toString()
            readLetter(letter)
            wordTV!!.text = stateWord()
            updateImage(errors)
            if (wordSearch.contentEquals(String(answers))) {
                Toast.makeText(this, "YOU WIN!", Toast.LENGTH_SHORT).show()
                searchTV!!.text = "YOU WIN!"
                if (difficulty == "Hard") {
                    score += 15
                }
                if (difficulty == "Normal") {
                    score += 10
                }
                if (difficulty == "Easy") {
                    score += 5
                }
                scoreTV!!.text = "Score: $score"
            } else {
                if (errors >= 6) {
                    updateImage(7)
                    Toast.makeText(this, "You lose...", Toast.LENGTH_SHORT).show()
                    searchTV!!.text = wordSearch
                }
            }
        } else {
            Toast.makeText(this, "End of the game", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Método que inicia una nueva partida con la dificultad seleccionada al pulsar el botón
     * correspondiente
     * @param view
     */
    fun start(view: View?) {
        newGame()
    }

    companion object {
        /**
         * Lista de palabras usadas en la dificultad Fácil
         */
        val easyWords = arrayOf(
                "PERA", "MOTO", "PALO", "LOCO", "RIMA", "REMO", "CARDO", "CAMA", "PESO",
                "AMOR", "ROTO", "FALSO", "BURRO", "FLOR", "NOTA", "COCHE", "LIBRO", "PRESO",
                "CANOA", "FIDEO", "CARNE", "RATON", "MANTEL", "SOBRE", "PERRO", "GATO", "LUNA",
                "PATO", "CABO", "ROJO", "POLEA", "MIRLO", "JAMON", "MONJA", "CRUZ", "GRIMA",
                "MONTE", "SOL", "LORO", "MOSCA", "TELON", "CITA", "MENTA", "CARRO", "MANO"
        )

        /**
         * Lista de palabras usadas en la dificultad Normal
         */
        val normalWords = arrayOf(
                "BRUJULA", "TRICICLO", "LOTERIA", "MAIZAL", "PROFESOR", "PIZARRA", "SABADO",
                "GENESIS", "POESIA", "DIBUJO", "LIBRERIA", "PESCADO", "PANDILLA", "COMICO",
                "ESTADO", "MONEDA", "BILLETE", "INCENDIO", "EMPRESA", "TRABAJO", "MENDRUGO",
                "ROCIO", "ARROZ", "LLUVIA", "ACAMPAR", "TENDERO", "CAUCE", "TERROR", "TORTUGA",
                "NADADOR", "PERSONA", "HOSTAL", "HELADO", "CORRAL", "AMPARO", "EXTRAÑO", "FRIO"
        )

        /**
         * Lista de palabras usadas en la dificultad Difícil
         */
        val hardWords = arrayOf(
                "AUTOESTIMA", "EXISTENTIAL", "OPTATIVA", "POSIBILIDAD", "ENCERRONA", "IMAGINACION",
                "AISLAMIENTO", "CAVERNICOLA", "ORIENTACION", "PARABRISAS", "DIMENSION", "ALMOHADILLA",
                "ESPIONAJE", "PROGRAMACION", "APOCALIPSIS", "PREMONICION", "PERIODICO", "PRESENTADOR",
                "CRUCIGRAMA", "EMPERADOR", "PATRONAJE", "MOCHILERO", "MEDICINA", "SANITARIO", "AZUCENA",
                "PIZZERIA", "TABAQUERIA", "GASOLINERA", "EMPRENDEDOR", "RESPONSABILIDAD", "DISCIPLINA"
        )
        val random = Random()
    }
}