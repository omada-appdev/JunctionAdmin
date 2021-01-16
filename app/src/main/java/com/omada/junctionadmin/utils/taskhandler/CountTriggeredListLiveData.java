package com.omada.junctionadmin.utils.taskhandler;

import java.util.ArrayList;
import java.util.List;

public class CountTriggeredListLiveData <T> extends CountTriggeredLiveData <List<T>> {

    private final List<T> listOnHold = new ArrayList<>();

    public CountTriggeredListLiveData(int numberToCountTo) {
        super(numberToCountTo);
    }

    @Override
    protected void setValue(List<T> value) {
        super.setValue(value);
    }

    public void addValue(T value){
        listOnHold.add(value);
        setValue(listOnHold);
    }
}
