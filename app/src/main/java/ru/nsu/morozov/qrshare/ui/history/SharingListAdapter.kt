/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.nsu.morozov.qrshare.ui.history

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.data.Sharing
import ru.nsu.morozov.qrshare.databinding.SharingListItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SharingListAdapter(
    private var onSend: (Sharing) -> Unit,
    private var onCopy: (Sharing) -> Unit,
    private var onDelete: (Sharing) -> Unit
) : ListAdapter<Sharing, SharingListAdapter.SharingListViewHolder>(SharingDiffCallback()) {

    class SharingListViewHolder(
        private val binding: SharingListItemBinding,
        private val onSend: (Sharing) -> Unit,
        private val onCopy: (Sharing) -> Unit,
        private val onDelete: (Sharing) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val sharingData = binding.sharingData
        private val datetime = binding.datetime

        fun bind(sharing: Sharing) {
            sharingData.text = sharing.data
            datetime.text = SimpleDateFormat(
                "HH:mm:ss dd.MM.yyyy",
                Locale.getDefault()
            ).format(Date(sharing.sentAt))
            binding.delete.setOnClickListener {
                onDelete(sharing)
            }
            binding.send.setOnClickListener {
                onSend(sharing)
            }
            binding.copy.setOnClickListener {
                onCopy(sharing)
            }
            val params: ViewGroup.LayoutParams = binding.sharingData.layoutParams
            binding.sharingData.measure(
                binding.sharingData.width,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (binding.sharingData.measuredHeight > binding.sharingData.context.resources.getDimension(
                    R.dimen.list_item_height
                ).toInt()
            ) {
                params.height =
                    binding.sharingData.context.resources.getDimension(R.dimen.list_item_height)
                        .toInt()
                binding.sharingData.layoutParams = params
                binding.sharingData.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.sharingData.context,
                        R.color.shrank
                    )
                )
                var isExpanded = true
                binding.sharingData.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if (!isExpanded) {
                            val animator = ValueAnimator.ofInt(
                                v.measuredHeight,
                                v.context.resources.getDimension(R.dimen.list_item_height).toInt()
                            )
                            val colorAnimator =
                                ValueAnimator.ofArgb(
                                    ContextCompat.getColor(v.context, R.color.white),
                                    ContextCompat.getColor(v.context, R.color.shrank)
                                )
                            animator.addUpdateListener {
                                params.height = it.animatedValue as Int
                                binding.sharingData.layoutParams = params
                            }
                            colorAnimator.addUpdateListener {
                                v.setBackgroundColor(it.animatedValue as Int)
                            }
                            colorAnimator.setDuration(200)
                            animator.setDuration(200)
                            animator.start()
                            colorAnimator.start()
                        } else {
                            val initialHeight = v.measuredHeight
                            v.measure(v.width, ViewGroup.LayoutParams.WRAP_CONTENT)
                            val animator =
                                ValueAnimator.ofInt(initialHeight, v.measuredHeight)
                            val colorAnimator =
                                ValueAnimator.ofArgb(
                                    ContextCompat.getColor(v.context, R.color.shrank),
                                    ContextCompat.getColor(v.context, R.color.white)
                                )
                            animator.addUpdateListener {
                                params.height = it.animatedValue as Int
                                binding.sharingData.layoutParams = params
                            }
                            colorAnimator.addUpdateListener {
                                v.setBackgroundColor(it.animatedValue as Int)
                            }
                            colorAnimator.setDuration(200)
                            animator.setDuration(200)
                            animator.start()
                            colorAnimator.start()
                        }
                        isExpanded = !isExpanded
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SharingListViewHolder(
        SharingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onSend,
        onCopy,
        onDelete
    )

    override fun onBindViewHolder(holder: SharingListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SharingDiffCallback : DiffUtil.ItemCallback<Sharing>() {
    override fun areItemsTheSame(oldItem: Sharing, newItem: Sharing): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sharing, newItem: Sharing): Boolean {
        return oldItem == newItem
    }
}
