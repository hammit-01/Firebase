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

class DeleteActivity : AppCompatActivity() {
    // Firestore 인스턴스 초기화
    private val db = FirebaseFirestore.getInstance()

    // FirebaseAuth 인스턴스 초기화
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete)

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter

        readPosts()

        val gotoDelete = findViewById<Button>(R.id.gotoDelete)
        gotoDelete.setOnClickListener {

            val selectedPosts = postAdapter.getSelectedPosts()
            // 선택된 포스트 ID를 Toast로 표시
//            Toast.makeText(this, "Selected Posts: $selectedPosts", Toast.LENGTH_LONG).show()
            for (postId in selectedPosts) {
                deletePost(postId)
            }
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

        val gotoWrite = findViewById<Button>(R.id.gotoWrite)
        gotoWrite.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
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
                            Toast.makeText(this, "Selected Posts: $id", Toast.LENGTH_LONG).show()
                        }
                        postAdapter.notifyDataSetChanged()
                    } else {
                        // 데이터가 없는 경우 처리
                        Toast.makeText(this, "No Data!", Toast.LENGTH_LONG).show()
                        postAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    // 데이터 읽기 실패
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    postAdapter.notifyDataSetChanged()
                }
        } else {
            // 사용자 로그인 정보가 없는 경우 처리
            Toast.makeText(this, "You need to login", Toast.LENGTH_LONG).show()
            postAdapter.notifyDataSetChanged()
        }
    }

    // 데이터 삭제 함수
    private fun deletePost(postId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // 사용자의 특정 글 문서 참조
            val postRef = db.collection("users").document(userId).collection("posts").document(postId)

            // 문서 삭제
            postRef.delete()
                .addOnSuccessListener {
                    // 문서 삭제 성공
                    Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show()
                    readPosts()
                }
                .addOnFailureListener { e ->
                    // 문서 삭제 실패
                    Toast.makeText(this, "삭제 실패", Toast.LENGTH_LONG).show()
                }
        } else {
            // 사용자 로그인 정보가 없는 경우 처리
            Toast.makeText(this, "사용자 로그인 정보가 없습니다", Toast.LENGTH_LONG).show()
        }
    }
}