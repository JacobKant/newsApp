package ru.jacobkant.newsapp.presentation.newsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_article.view.*
import ru.jacobkant.newsapp.R
import ru.jacobkant.newsapp.newsApi.Article
import ru.jacobkant.newsapp.presentation.toStringFormat

class NewsAdapter(
    private var items: List<Article> = listOf(),
    private val itemClickListener: ((Article) -> Unit)? = null,
    private val onScrollToLastPageListener: (() -> Unit)? = null
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    var isFullData: Boolean = false

    fun setItems(articles: List<Article>) {
        items = articles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        if (!isFullData && position >= items.size - 5) onScrollToLastPageListener?.invoke()
        val item: Article = items[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.author.text = item.author
        holder.dateTime.text = item.publishedAt.toStringFormat()
        holder.source.text = item.source.name
        Picasso.get()
            .load(item.urlToImage)
            .placeholder(R.drawable.progress_animated)
            .error(R.drawable.ic_error_outline_black_24dp)
            .into(holder.image)
        holder.container.setOnClickListener {
            itemClickListener?.invoke(items[holder.adapterPosition])
        }

    }

    override fun getItemCount(): Int = items.size

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: ViewGroup = itemView.item_article_container
        val title: TextView = itemView.item_article_title
        val description: TextView = itemView.item_article_description
        val dateTime: TextView = itemView.item_article_datetime
        val author: TextView = itemView.item_article_author
        val source: TextView = itemView.item_article_source
        val image: ImageView = itemView.item_article_image
    }

}