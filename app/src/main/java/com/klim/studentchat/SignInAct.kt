package com.klim.studentchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.klim.studentchat.databinding.ActivitySignInBinding



class SignInAct : AppCompatActivity() {

    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //инициализируем, переменную auth
        auth = Firebase.auth

        //сюда прийдет информация (результат)об окаунте пользователя, чтобы затем подключить FirebaseAuth
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // передаем сюда наш Intent об о акаунте
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            //если информация об акаунте есть, приложение работает, если нет обрабатываем ошибку
            try {
                val accaunt = task.getResult(ApiException::class.java)
                //если есть аккаунт , регистрируемся
                if (accaunt != null)
                    fаirebaseAuthWithGoogle(accaunt.idToken!!)

            } catch (e: ApiException) {
                Log.d("MyLog", "ApiException")

            }

        }

        binding.bSignIn.setOnClickListener() {
            sigInWithGoogle()
        }
        //проверка на авторизацию, если зарегистрированны , перекинет на др. страничку
        checkAuthState()
    }

    //отправляем андройду список аккаунтов,чтобы мы могли выбрать свой список-аккаунт
    //андройде, есть Google акаунт , в котором пользователь, зарегистрирован
    //этим методом мы запрашиваем информацию о пользователе, зарегистрирован
    private fun getClient(): GoogleSignInClient {

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //default_web_client_id  часто подсвечивает красным(не обращать внимания)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //затем поученный ключ мы отправляем нашему приложению
        return GoogleSignIn.getClient(this, gso)
    }

    //затем поученный ключ мы отправляем нашему приложению, через Intent
    private fun sigInWithGoogle() {
        val signInClient = getClient()

        launcher.launch(signInClient.signInIntent)
    }

    //как только есть токен подключаем наш акаунт на FirebaseAuth
    private fun fаirebaseAuthWithGoogle(idToken: String) {
        //взяли token из гугл акаунта
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        //и с этим токен заррегистрируемся и если успешно выводим.......
        auth.signInWithCredential(credencial).addOnCompleteListener() {
            //если все прошло успешно
            if (it.isSuccessful) {
                Log.d("MyLog", "Вы Успешно зарегистрированны!!!")

                checkAuthState()
            } else {
                Log.d("MyLog", "Произошла ошибка регистрации!!!")

            }
        
        }
    }

    //проверка на авторизацию
    private fun checkAuthState(){
        if(auth.currentUser != null){
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

}