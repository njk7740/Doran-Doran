package com.voda.blog;

import com.voda.blog.alarm.Alarm;

import java.util.Comparator;

public class SortBy implements Comparator<Alarm> {
    @Override
    public int compare(Alarm o1, Alarm o2) {
        return o2.getCreateDate().compareTo(o1.getCreateDate());
    }
}
