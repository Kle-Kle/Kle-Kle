package com.example.klekle.main.community

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    lateinit var binding: ViewBinding

    constructor(binding: ViewBinding): this(binding.root) {
        this.binding = binding
    }
}