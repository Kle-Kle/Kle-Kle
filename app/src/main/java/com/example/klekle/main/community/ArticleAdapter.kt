package com.example.klekle.main.community

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.app.AlertDialog
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.ArticleActivity
import com.example.klekle.PersonalProfileActivity
import com.example.klekle.R
import com.example.klekle.WritePostActivity
import com.example.klekle.databinding.ListItemArticleBinding
import com.example.klekle.main.my.UpdateProfileActivity
import com.example.klekle.model.ArticleModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.DeleteArticleRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ArticleAdapter: ListAdapter<ArticleModel, BaseViewHolder>(ARTICLE_LIST_COMPARATOR) {
    private lateinit var activity : Activity
    // 현재 로그인 중인 계정의 userid
    private var currentUserid = ""
    // CommunityActivity 에서만 PersonalProfileActivity로 이동 시키게 확인 하는 flag
    private var personProfileflag = false

    public fun setActivity(activity: Activity){
        this.activity = activity
    }
    public fun setCurrentUserid(currentUserid : String){
        this.currentUserid = currentUserid
    }
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
                    if (user_id == currentUserid) isVisible = true // 현재 로그인 중인 사용자가 작성한 게시글이 아니면, more unit 안 보이게
                    setOnClickListener {
                        if(personProfileflag){
                            showPopupMenu(it, article_no, article_content, article_image)
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

    private fun showPopupMenu(view: View, article_no: String?, article_content: String?, article_image: String?) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 팝업 메뉴 리소스 파일 지정

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    // 메뉴 아이템 1 클릭 시 수행할 동작 추가
                    // 게시글 삭제하기
                    deleteArticle(article_no)
                    true
                }
                R.id.menu_item_2 -> {
                    // 메뉴 아이템 2 클릭 시 수행할 동작 추가
                    // 게시글 수정하기
                    updateArticle(article_no, article_content, article_image)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun updateArticle(article_no: String?, article_content: String?, article_image: String?) {
        activity.finish()
        activity.overridePendingTransition(0, 0) // 인텐트 효과 없애기

        val intent = Intent(activity, WritePostActivity::class.java)

        intent.putExtra("article_no", article_no)
        intent.putExtra("content", article_content)
        if ("$article_image" == "null" || "$article_image" == "") intent.putExtra("mode", "updateIsNotImage") // 왜인지 article_image == null 이건 안 먹힘
        else {
            intent.putExtra("mode", "updateIsImage")
            val sharedPreferences = activity.getSharedPreferences("tempData", Activity.MODE_PRIVATE) // 이미지가 너무 커서, 이 방식으로 데이터 전송
            val temp = sharedPreferences.edit() // bitmap을 encoding 한 string의 크기가 너무 커서, putExtra로는 넘어가지 않음.. shared preference로 재도전
            temp.putString("existingArticleImage", article_image)
            temp.apply()
        }

        activity.startActivity(intent)
    }

    private fun deleteArticle(article_no: String?) {
        try{
            val str_tittle = "게시글 삭제"
            val str_message = "이 게시글을 삭제하시겠습니까?"
            val str_buttonOK = "확인"
            val str_buttonNO = "취소"

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                val responseListener: Response.Listener<String?> =
                    Response.Listener<String?> { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val rqst = DeleteArticleRequest(article_no, responseListener)
                val queue = Volley.newRequestQueue(activity)
                queue.add(rqst)

                // 새로 고침
                refresh()
            })
            builder.setNegativeButton(str_buttonNO, DialogInterface.OnClickListener { dialog, which ->
                try {
                    dialog.dismiss()
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            })
            val dialog = builder.create()
            try {
                dialog.show()
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        catch(e : Exception){
            e.printStackTrace()
        }
    }

    private fun refresh() {
        val intent = activity.intent // 인텐트
        activity.finish()
        activity.overridePendingTransition(0, 0) // 인텐트 효과 없애기
        activity.startActivity(intent) // 액티비티 열기
        activity.overridePendingTransition(0, 0) // 인텐트 효과 없애기
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