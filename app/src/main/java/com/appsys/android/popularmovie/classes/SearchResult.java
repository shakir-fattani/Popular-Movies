package com.appsys.android.popularmovie.classes;

import java.util.ArrayList;

/**
 * Created by shakir on 8/11/2017.
 */

public class SearchResult<T> {
    private int mTotalPages;
    private int mCount;
    private int mCurrentPageNo;
    private ArrayList<T> mList;

    public SearchResult(int totalPages, int totalResult, int currentPage, ArrayList<T> list) {
        mTotalPages = totalPages;
        mCount = totalResult;
        mCurrentPageNo = currentPage;
        mList = list;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    public int getTotalResult() {
        return mCount;
    }

    public int getCurrentPages() {
        return mCurrentPageNo;
    }

    public ArrayList<T> getList() {
        return mList;
    }
}
