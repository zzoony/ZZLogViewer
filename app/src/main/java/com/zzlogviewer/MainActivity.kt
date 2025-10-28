package com.zzlogviewer

import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zzlogviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_SHOW_LINE_NUMBERS = "show_line_numbers"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var logAdapter: LogAdapter
    private var showLineNumbers = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state
        showLineNumbers = savedInstanceState?.getBoolean(KEY_SHOW_LINE_NUMBERS, true) ?: true

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 타이틀 설정 (동적으로 버전 표시)
        binding.toolbarTitle.text = getString(
            R.string.app_title_with_version_fmt,
            getString(R.string.app_name),
            BuildConfig.VERSION_NAME
        )

        // 메뉴 버튼 클릭 리스너 설정
        binding.menuButton.setOnClickListener {
            showPopupMenu()
        }

        // 로그 파일 읽기
        val logLines = readLogFile()

        // RecyclerView 설정
        logAdapter = LogAdapter(logLines).apply {
            this.showLineNumbers = this@MainActivity.showLineNumbers
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = logAdapter
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SHOW_LINE_NUMBERS, showLineNumbers)
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.menuButton)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        // 체크 상태 설정 및 라벨 업데이트
        val toggleItem = popupMenu.menu.findItem(R.id.action_toggle_line_numbers)
        toggleItem?.isChecked = showLineNumbers
        toggleItem?.title = getString(if (showLineNumbers) R.string.hide_line_numbers else R.string.show_line_numbers)

        popupMenu.setOnMenuItemClickListener { item ->
            handleMenuItemClick(item)
        }

        popupMenu.show()
    }

    private fun readLogFile(): List<String> {
        return try {
            assets.open("sample.txt").bufferedReader().use { reader ->
                reader.readLines()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            listOf("Error loading log file: ${e.message}")
        }
    }

    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_line_numbers -> {
                // 토글 상태 변경
                showLineNumbers = !showLineNumbers
                item.isChecked = showLineNumbers
                item.title = getString(if (showLineNumbers) R.string.hide_line_numbers else R.string.show_line_numbers)

                // Adapter에 변경 사항 반영
                logAdapter.showLineNumbers = showLineNumbers

                true
            }
            else -> false
        }
    }
}
