package com.stephen.cli.project.library;

//每页加载完成事件
public interface FragmentTabContentLoadedListener {
    void onFragmentTabContentLoaded(int parentIndex, int childIndex);
}
