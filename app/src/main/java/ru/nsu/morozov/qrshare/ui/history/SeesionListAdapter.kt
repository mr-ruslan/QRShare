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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.nsu.morozov.qrshare.databinding.SessionListItemBinding
import ru.nsu.morozov.qrshare.data.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionListAdapter(
    private var onEdit: (Session) -> Unit,
    private var onDelete: (Session) -> Unit
) : ListAdapter<Session, SessionListAdapter.SessionListViewHolder>(SessionDiffCallback()) {

    class SessionListViewHolder(
        private val binding: SessionListItemBinding,
        private val onEdit: (Session) -> Unit,
        private val onDelete: (Session) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val nameView = binding.name
        private val datetime = binding.datetime
        private val nutshell = binding.nutshell

        fun bind(session: Session) {
            nameView.text = session.name
            datetime.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(session.createdAt))
            nutshell.text = "tap to see the content"
            binding.deleteButton.setOnClickListener {
                onDelete(session)
            }
            binding.root.setOnClickListener {
                onEdit(session)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SessionListViewHolder(
        SessionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onEdit,
        onDelete
    )

    override fun onBindViewHolder(holder: SessionListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SessionDiffCallback : DiffUtil.ItemCallback<Session>() {
    override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
        return oldItem == newItem
    }
}
