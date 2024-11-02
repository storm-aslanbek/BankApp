package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.retrofit.AuthPost
import com.example.myapplication.retrofit.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyBankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_bank)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phone_number = sharedPreferences.getString("phone_number", null)
        val password = sharedPreferences.getString("password", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = surname+" "+name

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
                this@MyBankActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG
            ).show()

        } else {
            val profile: TextView = findViewById(R.id.profile)
            findViewById<TextView>(R.id.profile).text = fullname
            findViewById<TextView>(R.id.phoneNumberText).text = phone_number

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
                                this@MyBankActivity,
                                "Жүйеге кірдіңіз!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val real_balance = authResponse.balance
                            findViewById<TextView>(R.id.balanceText).text = real_balance.toString()


                        }
                    } else {
                        Toast.makeText(
                            this@MyBankActivity,
                            "Жүйеге кіре алмадыңыз!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


        val homeButton: LinearLayout = findViewById(R.id.homeButtonBank)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        Ссылки прфиля
        val linkToAuth: TextView = findViewById(R.id.profile)
        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        val imageToAuth: ImageView = findViewById(R.id.profilePhoto)
        imageToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        val exit: Button = findViewById(R.id.exitButton)
        exit.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.remove("phone_number")
            editor.remove("last_name")
            editor.apply()
            finishAffinity()
        }
    }
}