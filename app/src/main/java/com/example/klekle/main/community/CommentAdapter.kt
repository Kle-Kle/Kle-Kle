package com.example.klekle.main.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.klekle.databinding.ListItemArticleBinding
import com.example.klekle.databinding.ListItemCommentBinding
import com.example.klekle.model.ArticleModel
import com.example.klekle.model.CommentModel
import com.example.klekle.util.BitmapConverter

class CommentAdapter: ListAdapter<CommentModel, BaseViewHolder>(COMMENT_LIST_COMPARATOR) {

    private lateinit var moreUnit: () -> Unit

    fun moreUnit(moreUnit: () -> Unit) {
        this.moreUnit = moreUnit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.run item@ {
            with(holder.binding as ListItemCommentBinding) {
                // 더보기 버튼 클릭
                with(btnMore) {
                    setOnClickListener {
                        moreUnit()
                    }
                }

                // 프로필 이미지
                with(ivProfile) {
                    val bm = BitmapConverter.stringToBitmap(user_profile ?: "")
                    setImageBitmap(bm)
                }

                // 닉네임
                with(tvNickName) {
                    text = user_nickname ?: ""
                }

                // 아이디
                with(tvUserId) {
                    text = user_id ?: ""
                }

                // 작성 날짜
                with(tvRegAt) {
                    text = commented ?: ""
                }

                // 본문
                with(tvContent) {
                    text = comment_content ?: ""
                }
            }
        }
    }

    companion object {
        private val COMMENT_LIST_COMPARATOR = object: DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel) =
                oldItem == newItem
        }
    }
}