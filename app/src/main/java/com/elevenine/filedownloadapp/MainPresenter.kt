package com.elevenine.filedownloadapp

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.io.IOException
import java.io.OutputStream

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private val fileApi: FileApi = provideFileApi(provideOkHttpClient())

    private val subscriptions = CompositeDisposable()

    private val staticTypeFiles = mutableListOf<FileModel>()
    private val dynamicTypeFiles = mutableListOf<FileModel>()

    private var currentFileModel: FileModel? = null

    init {
        staticTypeFiles.addAll(
            listOf(
                FileModel(
                    "moon.jpg",
                    "https://i.pinimg.com/originals/a4/f8/f9/a4f8f91b31d2c63a015ed34ae8c13bbd.jpg"
                ),
                FileModel(
                    "dude.jpg",
                    "https://i.pinimg.com/originals/82/f2/36/82f2364b6512247726c00292f7fc4a57.jpg"
                )
            )
        )

        dynamicTypeFiles.addAll(
            listOf(
                FileModel(
                    "city.jpg",
                    "https://i.pinimg.com/originals/6a/8b/50/6a8b50ac0aa0e64f224bcee2f0afbd66.jpg",
                    "jpg"
                ),
                FileModel(
                    "alienware.pdf",
                    "https://dl.dell.com/topicspdf/alienwarecomcenter5_en-us.pdf",
                    "pdf"
                )
            )
        )
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.addFiles(staticTypeFiles)
        viewState.addTitle("Dynamic type files")
        viewState.addFiles(dynamicTypeFiles)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.dispose()
    }

    fun onFileClicked(fileModel: FileModel) {
        currentFileModel = fileModel
        viewState.createNewDocument(fileModel.name)
    }

    fun initDownload(fileOutputStream: OutputStream) {
        subscriptions.add(
            fileApi.downloadFile(currentFileModel?.url ?: "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ body ->
                    val inputStream = body.byteStream()
                    try {
                        val buffer = ByteArray(1024) //Set buffer type
                        var len1 = 0 //init length
                        while (inputStream.read(buffer).also { len1 = it } != -1) {
                            fileOutputStream.write(buffer, 0, len1) //Write new file
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        fileOutputStream.close()
                        inputStream.close()
                        return@subscribe
                    }

                    //Close all connection after doing task
                    fileOutputStream.close()
                    inputStream.close()
                }, {
                    it.printStackTrace()
                })
        )
    }

}
