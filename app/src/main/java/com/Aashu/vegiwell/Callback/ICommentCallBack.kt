package com.AashuDeveloper.vegiwell.Callback

import com.AashuDeveloper.vegiwell.Model.CommentModel

interface ICommentCallBack {

    fun onCommentLoadSuccess(commentList: List<CommentModel>)
    fun onCommentLoadFailed(message:String)
}
