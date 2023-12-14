package com.app.chain.entity;

import common.app.base.BaseFragment;

public class ChainTabInfo {
    public int chainSyncType;
    public int titleId;
    public int syncCount;
    public BaseFragment fragment;

    public ChainTabInfo(int chainSyncType, int titleId, int syncCount, BaseFragment fragment) {
        this.chainSyncType = chainSyncType;
        this.titleId = titleId;
        this.syncCount = syncCount;
        this.fragment = fragment;
    }
}
