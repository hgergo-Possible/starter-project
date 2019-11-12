package com.possible.demo.feature.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Base [RecyclerView.ViewHolder] which creates the binding from the given parent and layout.
 */
open class BindingViewHolder<B : ViewDataBinding> private constructor(protected val binding: B) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, @LayoutRes layoutRes: Int) : this(
            DataBindingUtil.inflate<B>(
                    LayoutInflater.from(parent.context),
                    layoutRes,
                    parent,
                    false
            ))

}