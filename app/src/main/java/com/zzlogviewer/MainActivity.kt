package com.zzlogviewer

import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zzlogviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var logAdapter: LogAdapter
    private var showLineNumbers = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메뉴 버튼 클릭 리스너 설정
        binding.menuButton.setOnClickListener {
            showPopupMenu()
        }

        // 로그 파일 읽기
        val logLines = readLogFile()

        // RecyclerView 설정
        logAdapter = LogAdapter(logLines)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = logAdapter
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.menuButton)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        // 체크 상태 설정
        val toggleItem = popupMenu.menu.findItem(R.id.action_toggle_line_numbers)
        toggleItem?.isChecked = showLineNumbers

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

                // Adapter에 변경 사항 반영
                logAdapter.showLineNumbers = showLineNumbers

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
