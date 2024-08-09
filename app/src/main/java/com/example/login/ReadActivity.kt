package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ReadActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()
    // FirebaseAuth 인스턴스 초기화
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter

        readPosts()

        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val gotoWrite = findViewById<Button>(R.id.gotoWrite)
        gotoWrite.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
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

    // 데이터 읽기 함수
    private fun readPosts() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId).collection("posts")
                .get()
                .addOnSuccessListener { result ->
                    posts.clear()
                    if (!result.isEmpty) {
                        for (document in result) {
                            val id = document.id
                            val title = document.getString("title") ?: "No Title"
                            val content = document.getString("content") ?: "No Content"
                            posts.add(Post(id, title, content))
                        }
                        postAdapter.notifyDataSetChanged()
                    } else {
                        // 데이터가 없는 경우 처리
                        posts.add(Post("No posts found", "", ""))
                        postAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    // 데이터 읽기 실패
                    posts.add(Post("Error getting posts", "", ""))
                    postAdapter.notifyDataSetChanged()
                }
        } else {
            // 사용자 로그인 정보가 없는 경우 처리
            posts.add(Post("User not logged in", "", ""))
            postAdapter.notifyDataSetChanged()
        }
    }
}