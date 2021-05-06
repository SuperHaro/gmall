package online.superh.gmall.manage;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.apache.xpath.internal.operations.Mod;
import online.superh.gmall.bean.SpuInfo;
import online.superh.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-23 21:50
 */
@RestController
@CrossOrigin
public class SpuManageController {
    @Reference
    private ManageService manageService;
    //spuList 获取SPU列表
    @RequestMapping("spuList")
    public List<SpuInfo> getSpuList(SpuInfo spuInfo){

        return manageService.getSpuInfoList(spuInfo);
    }
    //saveSpuInfo
    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        if(spuInfo!=null){
            manageService.saveSpuInfo(spuInfo);
        }

    }
}
