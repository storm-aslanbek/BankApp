package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bank.User
import com.example.myapplication.databinding.ActivityAuthBinding
import com.example.myapplication.databinding.ActivityRegBinding
import com.example.myapplication.retrofit.MainAPI
import com.example.myapplication.retrofit.PostUserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
//        setContentView(R.layout.activity_reg)
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
            .baseUrl("http://172.20.10.7:8000/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainApi = retrofit.create(MainAPI::class.java)

        binding.registerButton.setOnClickListener {
            Log.d("RegActivity", "Button clicked")
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("RegActivity", "Coroutine started")
                val user = mainApi.register(
                    PostUserData(
                        binding.lastNameInput.text.toString(),
                        binding.firstNameInput.text.toString(),
                        binding.phoneInput.text.toString(),
                        binding.passwordInput.text.toString()
                    )
                )
//                runOnUiThread {
//                    binding.apply {
//                        statusText.text = user.last_name
//                    }
//                }
            }
        }


        val linkToAuth: TextView = findViewById(R.id.linkAuthText)

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}