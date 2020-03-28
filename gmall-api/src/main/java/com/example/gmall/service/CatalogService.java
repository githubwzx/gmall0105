package com.example.gmall.service;

import com.example.gmall.bean.PmsBaseCatalog1;
import com.example.gmall.bean.PmsBaseCatalog2;
import com.example.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * Created by wzx on 2020/3/26 23:21
 */
public interface CatalogService {

    List<PmsBaseCatalog1> getCatalog1();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
