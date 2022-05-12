package com.klim.studentchat

import android.content.ContentValues.TAG
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.klim.studentchat.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //запускаем аватар , в акшенбаре
        setUpActionBar()

        // создаем database
        val database = Firebase.database
        //"message" это путь куда, записываются данные
        val myRef = database.getReference("message")

        // передаем текст database (тест)
        //  myRef.setValue("Hello, World!")


        binding.bSend.setOnClickListener {
            //передаем данные ,переданные пользователем

            //чтобы сообщение, не перезаписовалось, делаем новый узел с помощью child()
            //myRef.setValue(User(auth.currentUser?.displayName,binding.textMessege.text.toString()))
            //у каждого сообщения свой индификальный ключ, сообщение не перезаписывается
            myRef.child(myRef.push().key ?:"Что угодно").setValue(User(auth.currentUser?.displayName,binding.edMessage.text.toString()))
        }
        // слушатель-постоянно обновляем данные с "message"
        onChangeListener( myRef)

        //адаптер сообщений
        tekstChat()
    }

    //адаптер сообщений
    private fun tekstChat() = with(binding){
        adapter = UserAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }


    //меню бара
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //меню бара
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sign_out){
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onChangeListener(dRef: DatabaseReference) {
        //как только на database, происходят изменения, сразу передаются данные сюда
        dRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val list =ArrayList<User>()
                //сообщения перебираем с помощью цикла (узла "message")
                for (s in snapshot.children ){
                    val user =s.getValue(User::class.java)
                    //если user не равен null, тогда заполняем список
                    if (user!=null) list.add(user)
                }
                adapter.submitList(list)



                //Данная модель, если сообщение перезаписывается
                //        binding.apply {
//                  //append() добавляет (\n с новой строки)
////                    tekstChat.append("\n")
////                    //snapshot, это информация (новое значение), полученная с  database
////                    tekstChat.append("Sergey : ${snapshot.value.toString()}")
//                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //делает аватарку в баре
    private fun setUpActionBar(){
        //акшенбар
        val ab = supportActionBar
        Thread{
            //загружаем картинку из интернета
            val bMap = Picasso.get().load(auth.currentUser?.photoUrl).get()
            //конвертируем bMap в  Drawable
            val dIcon = BitmapDrawable(resources, bMap)

            // runOnUiThread запускаем на основном потоке, иначе не получится
            runOnUiThread {
                //активировать кнопку
                ab?.setDisplayHomeAsUpEnabled(true)
                ab?.setHomeAsUpIndicator(dIcon)
                //вставляем название из акаунта пользователя
                ab?.title = auth.currentUser?.displayName
            }
        }.start()

    }
}

































//package com.klim.studentchat
//
//import android.content.ContentValues.TAG
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import com.klim.studentchat.databinding.ActivityMainBinding
//
//class MainActivity : AppCompatActivity() {
//    lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setContentView(R.layout.activity_main)
//
//        // создаем database
//        val database = Firebase.database
//        val myRef = database.getReference("message")
//
//        // передаем текст database (тест)
//        //  myRef.setValue("Hello, World!")
//
//
//        binding.bSend.setOnClickListener {
//            myRef.setValue(binding.textMessege.toString())
//        }
//
//
//    }
//
//    private fun onChangeListener(dRef: DatabaseReference) {
//
//    }
//
//}








