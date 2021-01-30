package com.omada.junctionadmin.utils.taskhandler;

public class CounterLiveData extends CountTriggeredLiveData <Boolean> {

    public CounterLiveData(int numberToCountTo) {
        super(numberToCountTo);
    }

    public void countUp(){
        setValue(true);
    }
}
