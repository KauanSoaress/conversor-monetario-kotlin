package com.example.monetario

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        result = findViewById<TextView>(R.id.txt_result)

        val buttonConverter = findViewById<Button>(R.id.btn_converter)

        buttonConverter.setOnClickListener {
            converter()
        }

    }

    private fun converter() {
        val selectedCurrency = findViewById<RadioGroup>(R.id.radio_group)

        val checked = selectedCurrency.checkedRadioButtonId

        val currency = when (checked) {
            // Como só tem 1 linha de código, pode ser feito em somente 1 linha, se não, fazer entre chaves
            R.id.radio_usd  -> "USD"
            R.id.radio_eur  -> "EUR"
            else            -> "CAD"
        }

        val editField = findViewById<EditText>(R.id.edit_field)

        val value = editField.text.toString()

        if (value.isEmpty()) return

        // Coisas que demoram, que têm latência de rede, como buscar um dado na internet, tem que ser feitas em processos diferentes, utilizando threads
        Thread {
            // Isto acontece em parelelo
            val url = URL("https://atway.tiagoaguiar.co/free/api/currency/convert?q=${currency}_BRL")

            val conn = url.openConnection() as HttpsURLConnection

            try {

                val data = conn.inputStream.bufferedReader().readText()

                // data = {"chave": valor} JSON

                val obj = JSONObject(data)

                runOnUiThread() { // Como é uma Thread a parte, temos que definir que o resultado será enviado para a Thread principal do app
                    val res = obj.getDouble("${currency}_BRL")

                    result.text = "R$${"%25.4f".format(value.toDouble() * res)}"
                    result.visibility = View.VISIBLE
                }


            } finally {
                conn.disconnect()
            }
        }.start()
    }
}