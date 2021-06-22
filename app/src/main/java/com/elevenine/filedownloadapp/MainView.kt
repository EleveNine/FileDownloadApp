package com.elevenine.filedownloadapp

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface MainView : MvpView {

    @AddToEndSingle
    fun addFiles(files: List<FileModel>)

    @AddToEndSingle
    fun addTitle(title: String)

    @OneExecution
    fun createNewDocument(fileName: String)

    @OneExecution
    fun selectFileLocation()

    @OneExecution
    fun createNewFile(destinationUri: Uri, type: String, name: String)
}
