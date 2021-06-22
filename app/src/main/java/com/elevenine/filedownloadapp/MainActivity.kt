package com.elevenine.filedownloadapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.elevenine.filedownloadapp.databinding.ActivityMainBinding
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import java.io.OutputStream

class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    private lateinit var binding: ActivityMainBinding

    private var fileAdapter: FileAdapter? = null

    private val mimeType = "image/jpg"

    private val createDocumentResultLauncher =
        registerForActivityResult(object : ActivityResultContracts.CreateDocument() {
            override fun createIntent(context: Context, input: String): Intent {
                val intent = super.createIntent(context, input)
                intent.apply {
                    // set static mime type for the downloaded file
                    type = mimeType
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
        }) { destinationUri ->
            var fos: OutputStream? = null
            if (destinationUri != null) fos = contentResolver.openOutputStream(destinationUri)
            if (fos != null) presenter.initDownload(fos)
        }

    private var chooseDestinationResultLauncher: ActivityResultLauncher<Uri> =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { destinationUri ->
            // check for null is necessary, since just pressing the back button while being
            // in the FileProvider will return the Uri = null.
            if (destinationUri != null) presenter.onFileLocationSelected(destinationUri)
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

    override fun selectFileLocation() {
        chooseDestinationResultLauncher.launch(Environment.DIRECTORY_DOCUMENTS.toUri())
    }

    override fun createNewFile(destinationUri: Uri, type: String, name: String) {
        val newFile =
            DocumentFile.fromTreeUri(this, destinationUri)?.createFile(mimeType, name)
        newFile?.uri?.let {
            val fos = contentResolver.openOutputStream(it)
            if (fos != null) {
                presenter.initDownload(fos)
            }
        }
    }
}