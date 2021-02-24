package com.omada.junctionadmin.utils.taskhandler;


public class LiveEvent<T> {

    protected Boolean eventHandled;
    protected T data;

    public LiveEvent(){
        eventHandled = false;
    }

    public LiveEvent(T data){
        eventHandled = false;
        this.data = data;
    }

    protected T getDataOnce(){
        if(eventHandled){
            return null;
        }
        else{
            eventHandled = true;
            return data;
        }
    }

    public T getDataOnceAndReset(){
        T _data = getDataOnce();
        data = null;
        return _data;
    }

    protected T getData(){
        return data;
    }
}
