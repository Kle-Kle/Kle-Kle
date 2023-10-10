package com.example.klekle.main.community

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.klekle.ArticleActivity
import com.example.klekle.PersonalProfileActivity
import com.example.klekle.R
import com.example.klekle.WritePostActivity
import com.example.klekle.databinding.ListItemArticleBinding
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.BitmapConverter
import java.io.ByteArrayOutputStream

class ArticleAdapter: ListAdapter<ArticleModel, BaseViewHolder>(ARTICLE_LIST_COMPARATOR) {

    // CommunityActivity 에서만 PersonalProfileActivity로 이동 시키게 확인 하는 flag
    private var personProfileflag  = false;

    public fun setPersonProfile(flag : Boolean){
        personProfileflag = flag;
    }

    private lateinit var moreUnit: () -> Unit

    fun moreUnit(moreUnit: () -> Unit) {
        this.moreUnit = moreUnit
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(ListItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.run item@ {
            with(holder.binding as ListItemArticleBinding) {

                with(btnGoToArticle) {
                    setOnClickListener {
                        val intent = Intent(context.applicationContext, ArticleActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("currentArticleNo", article_no) // 화면 넘어가면서, 해당 글의 article no 를 같이 넘겨줌
                        context.applicationContext.startActivity(intent)
                    }
                }

                // 더보기 버튼 클릭
                with(btnMore) {
                    setOnClickListener {
                        if(personProfileflag){
                            showPopupMenu(it)
                        }else {
                            moreUnit()
                        }
                    }
                }

                // 프로필 이미지
                with(ivProfile) {
                    val bm = BitmapConverter.stringToBitmap(user_profile ?: "")
                    setImageBitmap(bm)
                    setOnClickListener{
                        if(personProfileflag){
                            // Activity를 시작합니다.
                            // tvNickName 데이터를 다음 Activity로 전달하는 Intent 생성
                            val selectedArticle = getItem(position)

                            val intent = Intent(context, PersonalProfileActivity::class.java)
                            intent.putExtra("NickName", selectedArticle.user_nickname)
                            intent.putExtra("UserId",selectedArticle.user_id)

                            val bm = BitmapConverter.stringToBitmap(user_profile ?: "")

                            // 이미지를 압축한 후 ByteArray로 변환
                            val stream = ByteArrayOutputStream()
                            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                            val byteArray = stream.toByteArray()

                            intent.putExtra("Profile",byteArray)

                            context.startActivity(intent)
                        }
                    }

                }

                // 닉네임
                with(tvNickName) {
                    text = user_nickname ?: ""
                    setOnClickListener{
                        if(personProfileflag){
                            val selectedArticle = getItem(position)

                            val intent = Intent(context, PersonalProfileActivity::class.java)
                            intent.putExtra("NickName", selectedArticle.user_nickname)
                            intent.putExtra("UserId",selectedArticle.user_id)
                            val bm = BitmapConverter.stringToBitmap(selectedArticle.user_profile ?: "")

                            // 이미지를 압축한 후 ByteArray로 변환
                            val stream = ByteArrayOutputStream()
                            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                            val byteArray = stream.toByteArray()

                            intent.putExtra("Profile",byteArray)

                            context.startActivity(intent)
                        }
                    }
                }

                // 아이디
                with(tvUserId) {
                    text = user_id ?: ""
                    setOnClickListener{
                        if(personProfileflag){
                            val selectedArticle = getItem(position)

                            val intent = Intent(context, PersonalProfileActivity::class.java)
                            intent.putExtra("NickName", selectedArticle.user_nickname)
                            intent.putExtra("UserId",selectedArticle.user_id)
                            val bm = BitmapConverter.stringToBitmap(selectedArticle.user_profile ?: "")

                            // 이미지를 압축한 후 ByteArray로 변환
                            val stream = ByteArrayOutputStream()
                            bm.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                            val byteArray = stream.toByteArray()

                            intent.putExtra("Profile",byteArray)

                            context.startActivity(intent)
                        }
                    }
                }

                // 작성 날짜
                with(tvRegAt) {
                    text = published ?: ""
                }

                // 본문
                with(tvContent) {
                    text = article_content ?: ""
                }

                // 본문 사진
                with(ivContent) {
                    val bm = BitmapConverter.stringToBitmap(article_image ?: "")
                    setImageBitmap(bm)
                }

                // 댓글수
                with(tvComment) {
                    text = comment_count ?: ""
                }

                // 좋아요
                with(tvFavorite) {
                    // TODO
                }
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 팝업 메뉴 리소스 파일 지정

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    // 메뉴 아이템 1 클릭 시 수행할 동작 추가
                    true
                }
                R.id.menu_item_2 -> {
                    // 메뉴 아이템 2 클릭 시 수행할 동작 추가
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    companion object {
        private val ARTICLE_LIST_COMPARATOR = object: DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel) =
                oldItem == newItem
        }
    }
}