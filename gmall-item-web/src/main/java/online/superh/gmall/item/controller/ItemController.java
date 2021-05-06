package online.superh.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.ir.RuntimeNode;
import online.superh.gmall.bean.SkuInfo;
import online.superh.gmall.bean.SkuSaleAttrValue;
import online.superh.gmall.bean.SpuSaleAttr;
import online.superh.gmall.service.ListService;
import online.superh.gmall.service.ManageService;
import online.superh.gmall.util.LoginRequie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-26 22:47
 */
@Controller
public class ItemController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;
    @RequestMapping("{skuId}.html")
    @LoginRequie
    public String item(@PathVariable String skuId, Map<String,Object> map){
        //根据skuId获取数据
        SkuInfo skuInfo=manageService.getSkuInfo(skuId);
        //查询销售属性和销售属性值的集合
        List<SpuSaleAttr> SpuSaleAttrlist=manageService.getSpuSaleAttrlistChecked(skuInfo);
        //获取销售属性值Id的集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSpuSaleAttrValuelistBySpu(skuInfo.getSpuId());
        //遍历集合，拼接字符串
        String key="";
        HashMap<Object, Object> map1 = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if(key.length()>0){
                key+="|";
            }
            key+=skuSaleAttrValue.getSaleAttrValueId();
            if((i+1)==skuSaleAttrValueList.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i+1).getSkuId())){
                map1.put(key,skuSaleAttrValue.getSkuId());
                key = "";
            }

        }
        String valuesSkuJson = JSON.toJSONString(map1);
        System.out.println(valuesSkuJson);
        //保存到作用域
        map.put("skuInfo",skuInfo);
        map.put("SpuSaleAttrlist",SpuSaleAttrlist);
        map.put("valuesSkuJson",valuesSkuJson);
        listService.incrHotScore(skuId);
        return "item";
    }


}
