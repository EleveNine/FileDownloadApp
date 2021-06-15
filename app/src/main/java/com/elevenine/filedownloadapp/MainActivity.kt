package com.elevenine.filedownloadapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevenine.filedownloadapp.databinding.ActivityMainBinding
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() = MainPresenter()

    private lateinit var binding: ActivityMainBinding

    private var fileAdapter: FileAdapter? = null

    private val createDocumentResultLauncher =
        registerForActivityResult(object : ActivityResultContracts.CreateDocument() {
            override fun createIntent(context: Context, input: String): Intent {
                val intent = super.createIntent(context, input)
                intent.apply {
                    // set static mime type for the downloaded file
                    type = "image/jpg"
                    if (Build.VERSION.SDK_INT >= 26)
                        // optional: set initial destination Uri where the system's FileProvider
                        // will start when it is called using createDocumentResultLauncher
                        putExtra(
                            DocumentsContract.EXTRA_INITIAL_URI,
                            Environment.DIRECTORY_DOCUMENTS.toUri()
                        )
                }
                return intent
            }
        }) {
            val fos = contentResolver.openOutputStream(it)
            if (fos != null) presenter.initDownload(fos)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fileAdapter = FileAdapter { file ->
            presenter.onFileClicked(file)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = fileAdapter
    }

    override fun addFiles(files: List<FileModel>) {
        fileAdapter?.addFiles(files)
    }

    override fun addTitle(title: String) {
        fileAdapter?.addTitle(title)
    }

    override fun createNewDocument(fileName: String) {
        createDocumentResultLauncher.launch(fileName)
    }
}