package com.example.cl2_diana_portal_olano

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cl2_diana_portal_olano.Modelo.Posts


class PostsAdapter(private val postsList: List<Posts>, private val onItemClicked: (Posts) -> Unit) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lista_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)
        holder.itemView.setOnClickListener {
            onItemClicked(post)
        }
    }

    override fun getItemCount(): Int = postsList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)
        private val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val bodyTextView: TextView = itemView.findViewById(R.id.bodyTextView)

        // Vincular los datos del post a los TextViews
        fun bind(post: Posts) {

            userIdTextView.text = "User ID: ${post.getUserId()}"
            idTextView.text = "ID: ${post.getId()}"
            titleTextView.text = post.getTitle()
            bodyTextView.text = post.getBody()
        }
    }


}
