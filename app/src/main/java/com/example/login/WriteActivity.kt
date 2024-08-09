package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class WriteActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()
    // FirebaseAuth 인스턴스 초기화
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val input = findViewById<Button>(R.id.input)
        input.setOnClickListener {

            // EditText 뷰 연결
            val titleEditText = findViewById<EditText>(R.id.title)
            val contentEditText = findViewById<EditText>(R.id.content)

            // EditText에서 텍스트 추출
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            addNewPost(title, content)
            val intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val gotoRead = findViewById<Button>(R.id.gotoRead)
        gotoRead.setOnClickListener {
            val intent = Intent(this, ReadActivity::class.java)
            startActivity(intent)
        }

        val gotoDelete = findViewById<Button>(R.id.gotoDelete)
        gotoDelete.setOnClickListener {
            val intent = Intent(this, DeleteActivity::class.java)
            startActivity(intent)
        }

        val btn = findViewById<Button>(R.id.gotoMain)
        btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 데이터 쓰기 함수
    fun addNewPost(title: String, content: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // 사용자 도큐먼트의 서브컬렉션에 새 글 추가
            val post = hashMapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users").document(userId).collection("posts")
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