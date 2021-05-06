package online.superh.gmall.manage;

import com.alibaba.dubbo.config.annotation.Reference;
import online.superh.gmall.bean.*;
import online.superh.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-23 12:56
 */
@RestController
@CrossOrigin
public class MangaeController {
    @Reference
    private ManageService manageService;
    @RequestMapping ("getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }
    @RequestMapping ("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return manageService.getCatalog2(catalog1Id);
    }
    @RequestMapping ("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return manageService.getCatalog3(catalog2Id);
    }
    //attrInfoList
    @RequestMapping ("attrInfoList")
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id) {
        return manageService.getAttrInfo(catalog3Id);
    }
    //getAttrValueList
//    @RequestMapping("getAttrValueList")
//    public List<BaseAttrValue> getAttrValueList(@RequestParam(name="attrId") String attrInfoId) {
//        return manageService.getAttrValue(attrInfoId);
//    }
    //saveAttrInfo
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);
    }
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(@RequestParam(name="attrId") String attrInfoId) {
        //先查询平台属性
        BaseAttrInfo attrInfo = manageService.getAttrInfoById(attrInfoId);
        //返回平台属性值的Id
        return attrInfo.getAttrValueList();
    }
    //baseSaleAttrList 获取销售属性
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return manageService.getBaseSaleAttrList();
    }
}