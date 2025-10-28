package com.zzlogviewer

import android.text.SpannableString
import android.text.style.BackgroundColorSpan
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

    // 검색 관련 변수
    private var searchQuery: String = ""
    private var currentSearchLineIndex: Int = -1

    fun getLogLines(): List<String> = logLines

    fun setSearchHighlight(query: String, lineIndex: Int) {
        searchQuery = query
        currentSearchLineIndex = lineIndex
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

        // 색상 하이라이팅 적용 (로그 패턴 + 검색 하이라이트)
        holder.logTextView.text = colorizeLogLine(logLine, position, holder.itemView)

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

    private fun colorizeLogLine(line: String, position: Int, view: View): SpannableString {
        val spannable = SpannableString(line)
        val context = view.context

        // 1. 검색 하이라이팅 (가장 먼저 적용)
        if (searchQuery.isNotEmpty()) {
            var startIndex = 0
            while (startIndex < line.length) {
                val foundIndex = line.indexOf(searchQuery, startIndex, ignoreCase = true)
                if (foundIndex == -1) break

                val endIndex = foundIndex + searchQuery.length

                // 현재 라인이 선택된 검색 결과인지 확인
                val color = if (position == currentSearchLineIndex) {
                    ContextCompat.getColor(context, R.color.search_current)
                } else {
                    ContextCompat.getColor(context, R.color.search_highlight)
                }

                spannable.setSpan(
                    BackgroundColorSpan(color),
                    foundIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                startIndex = endIndex
            }
        }

        // 2. 타임스탬프 패턴: [날짜 시간]
        val timestampRegex = Regex("""\[\d{2}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2}\]""")
        timestampRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_timestamp)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 3. 구분선 패턴: ===...
        val separatorRegex = Regex("""={3,}""")
        separatorRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_separator)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 4. 화살표 패턴: -->, ->>
        val arrowRegex = Regex("""--+>""")
        arrowRegex.findAll(line).forEach { match ->
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.log_arrow)),
                match.range.first,
                match.range.last + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 5. 키워드 패턴: 대문자로 시작하는 단어 뒤에 콜론
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
