package com.zzlogviewer

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LogAdapter(private var logLines: List<String>) :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    var showLineNumbers: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var textSize: Float = 18f
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

        // 색상 하이라이팅 적용
        holder.logTextView.text = colorizeLogLine(logLine, holder.itemView)

        // 텍스트 크기 적용
        holder.lineNumberTextView.textSize = textSize
        holder.logTextView.textSize = textSize

        // 라인 번호 표시/숨김
        if (showLineNumbers) {
            holder.lineNumberTextView.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE
        } else {
            holder.lineNumberTextView.visibility = View.GONE
            holder.divider.visibility = View.GONE
        }
    }

    private fun colorizeLogLine(line: String, view: View): SpannableString {
        val spannable = SpannableString(line)
        val context = view.context

        // 1. 타임스탬프 패턴: [날짜 시간]
        val timestampRegex = Regex("""\[\d{2}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2}\]""")
        timestampRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_timestamp)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 2. 구분선 패턴: ===...
        val separatorRegex = Regex("""={3,}""")
        separatorRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_separator)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 3. 화살표 패턴: -->, ->>
        val arrowRegex = Regex("""--+>""")
        arrowRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_arrow)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 4. 키워드 패턴: 대문자로 시작하는 단어 뒤에 콜론
        val keywordRegex = Regex("""[A-Z_]+\s*:""")
        keywordRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_keyword)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }

    override fun getItemCount(): Int = logLines.size
}
