package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.retrofit.AuthPost
import com.example.myapplication.retrofit.MainAPI
import com.example.myapplication.retrofit.SearchPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransactionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        fun transaction_check(searchResponse: SearchResponse) {
//            val user_phone = searchResponse.phone_number
//        }


        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phone_number = sharedPreferences.getString("phone_number", null)
        val password = sharedPreferences.getString("password", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = surname + " " + name

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.100:8000").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainApi = retrofit.create(MainAPI::class.java)

//        Информация о профиле
        if (phone_number == null) {
            Toast.makeText(
                this@TransactionsActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG
            ).show()

        } else {

//            Получение информации о балансе
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.auth(
                    AuthPost(
                        phone_number.toString(),
                        password.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            Toast.makeText(
                                this@TransactionsActivity,
                                "Жүйеге кірдіңіз!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val real_balance = authResponse.balance
                            findViewById<TextView>(R.id.current_balance).text =
                                real_balance.toString() + "₸"


                        }
                    } else {
                        Toast.makeText(
                            this@TransactionsActivity,
                            "Жүйеге кіре алмадыңыз!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            findViewById<Button>(R.id.check_user_button).setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val check = mainApi.user_search(
                        SearchPost(
                            findViewById<EditText>(R.id.recipient_phone).text.toString()
                        )
                    )

                    withContext(Dispatchers.Main) {
                        if (check.isSuccessful) {
                            check.body()?.let { searchResponse ->
                                findViewById<TextView>(R.id.userStatus).text =
                                    searchResponse.last_name + searchResponse.first_name
                            }
                        } else {
                            findViewById<TextView>(R.id.userStatus).text = "Қолданушы табылмады"
                        }
                    }
                }
            }

            findViewById<Button>(R.id.confirm_transfer_button).setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val resp = mainApi.user_search(
                        SearchPost(
                            findViewById<EditText>(R.id.recipient_phone).text.toString(),
                        )
                    )

                    withContext(Dispatchers.Main) {
                        if (resp.isSuccessful) {
                            resp.body()?.let { searchResponse ->
                                Toast.makeText(
                                    this@TransactionsActivity,
                                    "Транзакция орындалды!" + searchResponse.first_name,
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(
                                    this@TransactionsActivity,
                                    MainActivity::class.java
                                )
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@TransactionsActivity,
                                "Жүйеге кіре алмадыңыз!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }



            val homeButton: LinearLayout = findViewById(R.id.homeButtonTransactions)
            homeButton.setOnClickListener {
                val intent = Intent(this@TransactionsActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}