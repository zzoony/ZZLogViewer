package com.zzlogviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
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

    // 파일 선택 런처
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                loadLogFile(uri)
            }
        }
    }

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

        // 파일 브라우징 버튼 클릭 리스너 설정
        binding.browseFileButton.setOnClickListener {
            openFilePicker()
        }

        // RecyclerView 설정 (초기에는 빈 리스트)
        logAdapter = LogAdapter(emptyList()).apply {
            this.showLineNumbers = this@MainActivity.showLineNumbers
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = logAdapter
        }

        // 초기 상태: 파일 선택 버튼 표시, RecyclerView 숨김
        showEmptyState(true)
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

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain", "text/*", "*/*"))
        }
        filePickerLauncher.launch(intent)
    }

    private fun loadLogFile(uri: Uri) {
        try {
            val logLines = contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                reader.readLines()
            } ?: emptyList()

            // RecyclerView에 로그 표시
            logAdapter = LogAdapter(logLines).apply {
                this.showLineNumbers = this@MainActivity.showLineNumbers
            }
            binding.recyclerView.adapter = logAdapter

            // 빈 상태 숨기고 RecyclerView 표시
            showEmptyState(false)
        } catch (e: Exception) {
            e.printStackTrace()
            logAdapter = LogAdapter(listOf("Error loading log file: ${e.message}")).apply {
                this.showLineNumbers = this@MainActivity.showLineNumbers
            }
            binding.recyclerView.adapter = logAdapter
            showEmptyState(false)
        }
    }

    private fun showEmptyState(show: Boolean) {
        if (show) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.browseFileButton.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.browseFileButton.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_browse_file -> {
                openFilePicker()
                true
            }
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
