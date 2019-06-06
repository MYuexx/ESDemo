package com.my.controller;

import com.my.dao.GoodsRepository;
import com.my.entity.GoodsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <一句话功能简述><br>
 * ()
 *
 * @author M.Y
 * @date 2019/5/30
 * @since 1.0.0
 */
@RestController
public class GoodsController {
    @Autowired
    private GoodsRepository goodsRepository;
    //http://localhost:8080/save?des=
    @GetMapping("save")
    public String save(String des){
        GoodsInfo goodsInfo = new GoodsInfo(System.currentTimeMillis(),
                "商品"+System.currentTimeMillis(),des);
        goodsRepository.save(goodsInfo);
        return "success";
    }

    //http://localhost:8080/delete?id=
    @GetMapping("delete")
    public String delete(long id){
        goodsRepository.deleteById(id);
        return "success";
    }

    //http://localhost:8080/update?name=修改&des=修改&id=
    @GetMapping("update")
    public String update(long id,String name,String description){
        GoodsInfo goodsInfo = new GoodsInfo(id,
                name,description);
        goodsRepository.save(goodsInfo);
        return "success";
    }

    //http://localhost:8080/getOne?id=
    @GetMapping("getOne")
    public GoodsInfo getOne(long id){
        GoodsInfo goodsInfo = goodsRepository.findById(id).orElse(null);
        return goodsInfo;
    }
}