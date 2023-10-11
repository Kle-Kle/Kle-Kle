package com.example.klekle.main.community

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.klekle.MainActivity
import com.example.klekle.PersonalProfileActivity
import com.example.klekle.R
import com.example.klekle.databinding.ListItemArticleBinding
import com.example.klekle.databinding.ListItemCommentBinding
import com.example.klekle.model.ArticleModel
import com.example.klekle.model.CommentModel
import com.example.klekle.util.BitmapConverter
import com.example.klekle.util.DeleteArticleRequest
import com.example.klekle.util.DeleteCommentRequest
import com.example.klekle.util.UpdateCommentRequest
import com.example.klekle.util.UpdateStatusMessageRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class CommentAdapter: ListAdapter<CommentModel, BaseViewHolder>(COMMENT_LIST_COMPARATOR) {
    lateinit var view_updateStatusMessage : View

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
        BaseViewHolder(ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position)?.run item@ {
            with(holder.binding as ListItemCommentBinding) {
                // 더보기 버튼 클릭
                with(btnMore) {
                    if (user_id == currentUserid) isVisible = true // 현재 로그인 중인 사용자가 작성한 게시글이 아니면, more unit 안 보이게
                    setOnClickListener {
                        if(personProfileflag){
                            showPopupMenu(it, comment_no, comment_content)
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

                // 사용자 프로필 영역(닉네임, 아이디)
                with(articleAuthorId) {
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
                }

                // 아이디
                with(tvUserId) {
                    text = user_id ?: ""
                }

                // 작성 날짜
                with(tvRegAt) {
                    text = commented ?: ""
                }

                // 수정 여부
                with(tvIsEdited) {
                    text = if (is_edited == 1) "  •  (편집됨)"
                    else ""
                }

                // 본문
                with(tvContent) {
                    text = comment_content ?: ""
                }
            }
        }
    }

    private fun showPopupMenu(view: View, comment_no: String?, comment_content: String?) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 팝업 메뉴 리소스 파일 지정

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    // 메뉴 아이템 1 클릭 시 수행할 동작 추가
                    // 게시글 삭제하기
                    deleteComment(comment_no)
                    true
                }
                R.id.menu_item_2 -> {
                    // 메뉴 아이템 2 클릭 시 수행할 동작 추가
                    // 게시글 수정하기
                    updateComment(comment_no, comment_content)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun updateComment(commentNo: String?, commentContent: String?) {
        val inflater = activity.layoutInflater;
        var currentCharCount = commentContent?.length // 기존 댓글의 길이

        try{
            val str_tittle = "댓글 수정"
            val str_message = "댓글은 최대 280자까지 작성 가능합니다."
            val str_buttonOK = "편집"
            val str_buttonNO = "취소"

            view_updateStatusMessage = inflater.inflate(R.layout.dialog_update_status_message, null)
            val et_newCommentContent = view_updateStatusMessage.findViewById<EditText>(R.id.et_newStatusMessage)
            val tv_currentCharCount = view_updateStatusMessage.findViewById<TextView>(R.id.tv_currentCharCount)

            et_newCommentContent.setText(commentContent)  // 기존 상태메시지 불러옴
            tv_currentCharCount.text = "($currentCharCount/280)"

            et_newCommentContent.addTextChangedListener {
                currentCharCount = et_newCommentContent.length()
                tv_currentCharCount.text = "($currentCharCount/280)"
            }

            val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
            builder.setTitle(str_tittle) // 팝업창 타이틀 지정
            builder.setMessage(str_message) // 팝업창 내용 지정
            builder.setView(view_updateStatusMessage)

            builder.setPositiveButton(str_buttonOK, DialogInterface.OnClickListener { dialog, which ->
                val newContent : String = et_newCommentContent.text.toString()
                val responseListener: Response.Listener<String?> =
                    Response.Listener<String?> { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            val success = jsonResponse.getBoolean("success")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                val rqst = UpdateCommentRequest(commentNo, newContent, responseListener)
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

    private fun deleteComment(commentNo: String?) {
        try{
            val str_tittle = "댓글 삭제"
            val str_message = "이 댓글을 삭제하시겠습니까?"
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
                val rqst = DeleteCommentRequest(commentNo, responseListener)
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
        private val COMMENT_LIST_COMPARATOR = object: DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel) =
                oldItem == newItem
        }
    }
}