package com.example.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val selectedPosts = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.idView.text = post.id
        holder.titleTextView.text = post.title
        holder.contentTextView.text = post.content
        holder.checkBox.isChecked = selectedPosts.contains(post.id)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedPosts.add(post.id)
            } else {
                selectedPosts.remove(post.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun getSelectedPosts(): List<String> {
        return selectedPosts.toList()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idView: TextView = itemView.findViewById(R.id.idView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}