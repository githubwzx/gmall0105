package com.example.gmall.service;



import com.example.gmall.bean.PmsSearchParam;
import com.example.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
