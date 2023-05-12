package com.lgy.pool.core;

interface DownloadListener<E> {
    void onDownloadStart(E task);
    void onProgressChanged(int progress,E task);

    void onDownloadPaused(E task);

    void onDownloadCanceled(E task);

    void onDownloadCompleted(E task);

    void onDownloadError(E task,String message);
    }