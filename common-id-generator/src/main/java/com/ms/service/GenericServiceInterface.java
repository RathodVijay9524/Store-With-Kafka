package com.ms.service;

import java.util.List;

public interface GenericServiceInterface<Req,Res,ID> {
    Res create(Req request);
    Res findById(ID id);
    List<Res> findAll();
    Res update(ID id,Req request);
    void delete(ID id);
    Res getByIdOrThrow(ID id);
}
