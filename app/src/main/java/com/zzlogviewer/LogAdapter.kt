package com.zzlogviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private var logLines: List<String>) :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    var showLineNumbers: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun updateData(newLines: List<String>) {
        logLines = newLines
        notifyDataSetChanged()
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lineNumberTextView: TextView = itemView.findViewById(R.id.lineNumberTextView)
        val divider: View = itemView.findViewById(R.id.divider)
        val logTextView: TextView = itemView.findViewById(R.id.logTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val lineNumber = position + 1
        val logLine = logLines[position]

        holder.lineNumberTextView.text = lineNumber.toString()
        holder.logTextView.text = logLine

        // 라인 번호 표시/숨김
        if (showLineNumbers) {
            holder.lineNumberTextView.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
        } else {
            holder.lineNumberTextView.visibility = View.GONE
            holder.divider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = logLines.size
}
