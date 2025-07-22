package com.ms.service;

public interface Identifiable<ID> {
    ID getId();
    void setId(ID id);
}
