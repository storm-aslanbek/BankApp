package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityAuthBinding
import com.example.myapplication.retrofit.AuthPost
import com.example.myapplication.retrofit.AuthResponse
import com.example.myapplication.retrofit.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.100:8000").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainApi = retrofit.create(MainAPI::class.java)

//Метод для хранения состояния
        fun saveUserData(authResponse: AuthResponse) {
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("phone_number", authResponse.phone_number)
            editor.putString("password", authResponse.password)
            editor.putString("last_name", authResponse.last_name)
            editor.putString("first_name", authResponse.first_name)
            editor.apply()
        }


        binding.authButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.auth(
                    AuthPost(
                        binding.phoneAuthInput.text.toString(),
                        binding.passwordAuthInput.text.toString()
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            saveUserData(authResponse) // Сохраняем данные пользователя
                            Toast.makeText(this@AuthActivity, "Жүйеге кірдіңіз!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@AuthActivity, "Жүйеге кіре алмадыңыз!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }





        val linkToReg: TextView = findViewById(R.id.linkRegText)
        linkToReg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)
        }

        val homeButton: LinearLayout = findViewById(R.id.homeButtonAuth)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}