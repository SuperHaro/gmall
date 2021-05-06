package online.superh.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import online.superh.gmall.bean.*;
import online.superh.gmall.service.ListService;
import online.superh.gmall.service.ManageService;
import online.superh.gmall.util.LoginRequie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-29 16:50
 */
@Controller
public class ListController {
    @Reference
    ListService listService;
    @Reference
    ManageService manageService;
//    @LoginRequie
    @RequestMapping("list.html")
    //@ResponseBody
    public String listData(SkuLsParams skuLsParams, Model model){
        try {
            skuLsParams.setPageSize(2);
            SkuLsResult search = listService.search(skuLsParams);
            //String searchJson = JSON.toJSONString(search);
            List<SkuLsInfo> skuLsInfoList = search.getSkuLsInfoList();
            //平台属性值
            List<String> attrValueIdList = search.getAttrValueIdList();
            //通过Id查平台属性值和平台属性值的名称
            List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(attrValueIdList);
            //面包屑
             List<BaseAttrValue> elist = new ArrayList<>();

            //迭代器
            for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
                BaseAttrInfo baseAttrInfo = iterator.next();
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    String[] valueIds = skuLsParams.getValueId();
                    if(valueIds!=null&&valueIds.length>0){
                        for (String valueId : valueIds) {
                            if(valueId.equals(baseAttrValue.getId())){
                                BaseAttrValue baseAttrValueeed = new BaseAttrValue();
                                baseAttrValueeed.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                                String urlParam = makeUrlParam(skuLsParams, valueId);
                                baseAttrValueeed.setUrlParam(urlParam);
                                elist.add(baseAttrValueeed);
                                iterator.remove();

                            }
                        }
                    }
                }
            }
            //编写方法来判断url后面的参数条件
            String urlParam = makeUrlParam(skuLsParams);
            model.addAttribute("skuLsInfoList",skuLsInfoList);
            model.addAttribute("baseAttrInfoList",baseAttrInfoList);
            model.addAttribute("urlParam",urlParam);
            model.addAttribute("elist",elist);
            model.addAttribute("keyword",skuLsParams.getKeyword());
            model.addAttribute("pageNo",skuLsParams.getPageNo());
            model.addAttribute("totalPages",search.getTotalPages());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "list";
    }
    //判断具体有哪些参数
    private String makeUrlParam(SkuLsParams skuLsParams,String... excludeValueIds) {
        String urlParam = "";
        //根据keyword
        if(skuLsParams.getKeyword()!=null&&skuLsParams.getKeyword().length()>0){
            urlParam += "keyword="+skuLsParams.getKeyword();
        }
        if (skuLsParams.getCatalog3Id()!=null&&skuLsParams.getCatalog3Id().length()>0){
            if(urlParam.length()>0){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        if(skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){

            for (String valueId : skuLsParams.getValueId()) {
                if(excludeValueIds!=null&&excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals(valueId)){
                        continue;
                    }
                }
                if(urlParam.length()>0){
                    urlParam+="&";
                }
                urlParam+="valueId="+valueId;
            }
        }
        return urlParam;
    }
}
