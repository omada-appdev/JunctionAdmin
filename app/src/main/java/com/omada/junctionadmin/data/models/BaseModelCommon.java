package com.omada.junctionadmin.data.models;

import java.util.Objects;

public class BaseModelCommon {

    protected String id;

    public BaseModelCommon(){
    }

    public BaseModelCommon(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModelCommon that = (BaseModelCommon) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
