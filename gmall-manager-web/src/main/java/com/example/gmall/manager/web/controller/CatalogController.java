package com.example.gmall.manager.web.controller;


import com.example.gmall.bean.PmsBaseCatalog1;
import com.example.gmall.bean.PmsBaseCatalog2;
import com.example.gmall.bean.PmsBaseCatalog3;
import com.example.gmall.service.CatalogService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class CatalogController {

    @Reference
    CatalogService catalogService;

    @RequestMapping("getCatalog3")
    @ResponseBody
    public List<PmsBaseCatalog3> getCatalog3(String catalog3Id){

        List<PmsBaseCatalog3> catalog3s = catalogService.getCatalog3(catalog3Id);
        return catalog3s;
    }


    @RequestMapping("getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(String catalog2Id){

        List<PmsBaseCatalog2> catalog2s = catalogService.getCatalog2(catalog2Id);
        return catalog2s;
    }

    @RequestMapping("getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){

        List<PmsBaseCatalog1> catalog1s = catalogService.getCatalog1();
        return catalog1s;
    }
}
