package online.superh.gmall.manage;

import com.alibaba.dubbo.config.annotation.Reference;
import online.superh.gmall.bean.SkuInfo;
import online.superh.gmall.bean.SkuLsInfo;
import online.superh.gmall.bean.SpuImage;
import online.superh.gmall.bean.SpuSaleAttr;
import online.superh.gmall.service.ListService;
import online.superh.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-25 12:17
 */
@RestController
@CrossOrigin
public class SkuManageController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;
    //上传一个商品
    @RequestMapping("onSale")
    public void onSale(String skuId){
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //给skuLsInfo赋值
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        BeanUtils.copyProperties(skuInfo,skuLsInfo);
        listService.saveSkuLsInfo(skuLsInfo);

    }
    /**
     *@Description: 根据spuId获取图片
     *@Creator: SuperH  
     *@Date: 2021/4/25 12:29   
     *@param spuImage
     *@return: java.util.List<online.superh.gmall.bean.SpuImage> 
     *@throws: 
     */ 
    @GetMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage){
        return manageService.getspuImageList(spuImage);
    }
    //spuSaleAttrList
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }
    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo){
         manageService.saveSkuInfo(skuInfo);
    }
}
