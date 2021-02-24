package com.omada.junctionadmin.data.repository;

import androidx.annotation.Nullable;

/*
 This class identifies the data repository accessor so that state can be tracked without
 creating new instances for each accessor
*/
public final class DataRepositoryAccessIdentifier {

    private final String id;

    DataRepositoryAccessIdentifier(String id){
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DataRepositoryAccessIdentifier other = (DataRepositoryAccessIdentifier) obj;
        return this.id.equals(other.id);
    }

}
