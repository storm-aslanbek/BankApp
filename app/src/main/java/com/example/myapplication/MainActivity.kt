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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        Получение запроса из SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val phone_number = sharedPreferences.getString("phone_number", null)
        val surname = sharedPreferences.getString("last_name", null)
        val name = sharedPreferences.getString("first_name", null)
        val fullname = surname+" "+name

        if (phone_number == null) {
            Toast.makeText(
                this@MainActivity, "Сіз жүйеге кірмегенсіз!", Toast.LENGTH_LONG
            ).show()

        } else {
            val profile: TextView = findViewById(R.id.profile)
            findViewById<TextView>(R.id.profile).text = fullname
        }


        val myBank: LinearLayout = findViewById(R.id.myBank)
        myBank.setOnClickListener {
            val intent = Intent(this, MyBankActivity::class.java)
            startActivity(intent)
        }

        val transactions: LinearLayout = findViewById(R.id.transactionsMain)
        transactions.setOnClickListener {
            val intent = Intent(this, TransactionsActivity::class.java)
            startActivity(intent)
        }

//        Ссылки профиля, авторизации
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