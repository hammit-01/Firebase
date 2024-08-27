package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()
    // FirebaseAuth 인스턴스 초기화
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FirebaseAuth 인스턴스 초기화
        auth = FirebaseAuth.getInstance()
        val join = findViewById<Button>(R.id.join)
        val login = findViewById<Button>(R.id.login)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val gotoWrite = findViewById<Button>(R.id.gotoWrite)
        val gotoRead = findViewById<Button>(R.id.gotoRead)
        val logout = findViewById<Button>(R.id.logout)
        val gotoDelete = findViewById<Button>(R.id.gotoDelete)
        // Todo checkbox 설정
        val checkbox = findViewById<CheckBox>(R.id.checkBox)

        // 현재 사용자 가져오기
        val currentUser = auth.currentUser
        if (currentUser != null) {
            join.visibility = View.GONE
            login.visibility = View.GONE
            email.visibility = View.GONE
            password.visibility = View.GONE
            gotoWrite.visibility = View.VISIBLE
            gotoRead.visibility = View.VISIBLE
            logout.visibility = View.VISIBLE
            gotoDelete.visibility = View.VISIBLE
            checkbox.visibility = View.GONE

            logout.setOnClickListener {
                Firebase.auth.signOut()
                Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            gotoWrite.setOnClickListener {
                val intent = Intent(this, WriteActivity::class.java)
                startActivity(intent)
            }

            gotoRead.setOnClickListener {
                val intent = Intent(this, ReadActivity::class.java)
                startActivity(intent)
            }

            gotoDelete.setOnClickListener {
                val intent = Intent(this, DeleteActivity::class.java)
                startActivity(intent)
            }

        } else {
            auth = Firebase.auth

            join.visibility = View.VISIBLE
            login.visibility = View.VISIBLE
            email.visibility = View.VISIBLE
            password.visibility = View.VISIBLE
            gotoWrite.visibility = View.GONE
            gotoRead.visibility = View.GONE
            logout.visibility = View.GONE
            gotoDelete.visibility = View.GONE
            checkbox.visibility = View.VISIBLE

            join.setOnClickListener {
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //addNewPost(id, name, userName)
                            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
            }

            login.setOnClickListener {
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "로그인 완료", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
            }
        }
    }

    // 데이터 쓰기 함수
    fun addNewPost(id: String, name: String, userName: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // 사용자 도큐먼트의 서브컬렉션에 새 글 추가
            val post = hashMapOf(
                "id" to id,
                "name" to name,
                "userName" to userName
            )

            db.collection("userData").document(userId).collection("userdata")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    // 글 추가 성공
                    Toast.makeText(this, "데이터 쓰기 성공", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // 글 추가 실패
                    println("Error adding document: $e")
                }
        } else {
            // 사용자 로그인 정보가 없는 경우 처리
            println("User not logged in")
        }
    }
}