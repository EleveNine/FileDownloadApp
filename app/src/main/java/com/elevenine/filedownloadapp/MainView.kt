package com.elevenine.filedownloadapp

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
}
