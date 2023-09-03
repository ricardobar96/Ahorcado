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
     * Method that changes the difficulty and starts a new game based on the difficulty
     * option selected
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
     * Method that selects a random word from an array based on the value "difficulty", which the
     * player will have to guess to win the game
     * @return String that represents the word the player have to guess to win the game
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
     * Method that updates the image of a hanged figure based on the number of errors commited
     * by the player in the current game
     * @param estado Integer that represents the number of errors commited by the player before
     * guessing the current word
     */
    private fun updateImage(state: Int) {
        val resource = resources.getIdentifier("state$state", "drawable",
                packageName)
        image!!.setImageResource(resource)
    }

    /**
     * Method that starts a new game, resetting the number of mistakes made in the game, the list of
     * words chosen by the player when trying to guess the word, the state of the hangman image, the
     * messages displayed on the view. It also chooses a new word to find with the method chooseWord
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
     * Method that reads the letter selected by the player. If the word to guess does not contain this
     * letter, it adds an error, but if the word to search contains this letter it detects in which
     * position goes, filling those positions and showing them to the player as a clue to help them
     * guess the entire word. Finallu, it ads the letter to the list of letters already entered in
     * the current game
     * @param c String that represents the letter selected by the player
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
     * Method that returns the state of the word the player have to guess, based on the letters
     * already guessed by the player
     * @return String that represents the state of the word the player have to guess
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
     * Method that when selecting a letter shows in the view an updated clue of the current word
     * the player have to guess based on the letters correctly guessed by the player, the message
     * "YOU WIN! if the clue is completed before reaching 7 mistakes and the message "You lost..."
     * if the player reach 7 mistakes before achieving this. It also updates the current score with
     * each victory, based on the difficulty of the word
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
     * Method that starts a new game with the difficulty selected when pressing the button
     * @param view
     */
    fun start(view: View?) {
        newGame()
    }

    companion object {
        /**
         * List of words used on the Easy difficulty
         */
        val easyWords = arrayOf(
                "PEAR", "TREE", "SUN", "COW", "WOOD", "SEA", "MOON", "BED", "SOFA",
                "LOVE", "FREE", "FALSE", "DOG", "ROSE", "PEN", "CAR", "BOOK", "JAIL",
                "BOAT", "EAT", "MEAT", "DOOR", "ICE", "HEAT", "SAND", "PRAY", "EYE",
                "ALIVE", "GOD", "RAY", "HOPE", "TOMB", "NEW", "FIRE", "ROCK", "REST",
                "WALK", "BATH", "TALK", "FLY", "OPEN", "DATE", "MINT", "LIKE", "HAND"
        )

        /**
         * List of the words used on the Normal difficulty
         */
        val normalWords = arrayOf(
                "MOUNTAIN", "LANGUAGE", "PAINTING", "WHEEL", "PROFESSOR", "CHALK", "SATURDAY",
                "GENESIS", "POETRY", "RECORD", "LIBRERY", "POTATO", "GANG", "FUNNY",
                "STATE", "MONEY", "INVEST", "SPARK", "COMPANY", "WORKER", "SHOE",
                "LANTERN", "SPRAY", "BEACH", "AWAKE", "KEEPER", "STRAY", "HORROR", "TURTLE",
                "SWIMMER", "PERSON", "HOTEL", "CREAM", "REVENGE", "REGRET", "STRANGE", "PROUD"
        )

        /**
         * List of the words used on the Hard difficulty
         */
        val hardWords = arrayOf(
                "IMAGINATION", "EXISTENTIAL", "AWKWARD", "POSSIBILITY", "GLYPH", "IMAGINACION",
                "WELLSPRING", "STRONGHOLD", "JUKEBOX", "UMBRELLA", "BOOKWORM", "BLIZZARD",
                "JACKPOT", "PROGRAMMER", "PARANOIA", "PNEUMONIA", "NEWSPAPER", "NIGHTCLUB",
                "EXODUS", "EMPEROR", "CORRUPTION", "BACKPACK", "MUSHROOM", "WARRIOR", "DINOSAUR",
                "MICROWAVE", "BAREFOOT", "BEAUTIFUL", "HANDSOME", "EXPRESSION", "DISCIPLINE"
        )
        val random = Random()
    }
}