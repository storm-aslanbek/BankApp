package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bank.User

class RegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userFirstName: EditText = findViewById(R.id.firstNameInput)
        val userLastName: EditText = findViewById(R.id.lastNameInput)
        val userPhone: EditText = findViewById(R.id.phoneInput)
        val userPass: EditText = findViewById(R.id.passwordInput)
        val registerButton: Button = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val firstName = userFirstName.text.toString().trim()
            val lastName = userLastName.text.toString().trim()
            val phone = userPhone.text.toString().trim()
            val password = userPass.text.toString().trim()

            if(firstName == "" || lastName == "" || phone == "" || password == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                val user = User(firstName, lastName, phone, password)

            }
        }
        val linkToAuth: TextView = findViewById(R.id.linkAuthText)

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }
}