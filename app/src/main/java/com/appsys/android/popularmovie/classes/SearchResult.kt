package com.appsys.android.popularmovie.classes

import java.util.*

/**
 * Created by shakir on 8/11/2017.
 */
class SearchResult<T>(val totalPages: Int, val totalResult: Int, val currentPages: Int, val list: ArrayList<T>)