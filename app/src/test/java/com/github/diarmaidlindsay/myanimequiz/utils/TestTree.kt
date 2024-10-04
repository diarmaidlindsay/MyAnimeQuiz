package com.github.diarmaidlindsay.myanimequiz.utils

import timber.log.Timber

class TestTree : Timber.Tree() {
    val logs = mutableListOf<Log>()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logs.add(Log(priority, tag, message, t))
    }

    data class Log(val priority: Int, val tag: String?, val message: String, val t: Throwable?)
}

fun withTestTree(body: TestTree.() -> Unit) {
    val testTree = TestTree()
    Timber.plant(testTree)
    body(testTree)
    Timber.uproot(testTree)
}