package online.superh.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import io.searchbox.core.search.aggregation.TermsAggregation;
import online.superh.gmall.bean.SkuInfo;
import online.superh.gmall.bean.SkuLsInfo;
import online.superh.gmall.bean.SkuLsParams;
import online.superh.gmall.bean.SkuLsResult;
import online.superh.gmall.config.RedisUtil;
import online.superh.gmall.service.ListService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-29 13:01
 */
@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private JestClient jestClient;
    public static final String ES_INDEX="gmall";

    public static final String ES_TYPE="SkuInfo";
    @Autowired
    RedisUtil redisUtil;
    /**
     * @param skuLsInfo
     * @Description: 保存数据到es中
     * @Creator: SuperH
     * @Date: 2021/4/29 13:00
     * @return: void
     * @throws:
     */
    @Override
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户输入的条件查询商品
     *
     * @param skuLsParams
     * @return
     */
    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        /*
        1.定义dsl语句
        2.定义动作
        3.执行动作
        4.获取结果集
         */
        String query=makeQueryStringForSearch(skuLsParams);
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult result = null;
        try {
             result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SkuLsResult skuLsResult=makeResultForSearch(result,skuLsParams);
        return skuLsResult;
    }

    /**
     * 记录每个商品被访问的次数
     *
     * @param skuId
     */
    @Override
    public void incrHotScore(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        //定义key
        String hotKey="hotScore";
        Double count = jedis.zincrby(hotKey, 1, "skuid:" + skuId);
        if(count%10==0){
            updateHotScore(skuId,  Math.round(count));
        }
    }

    private void updateHotScore(String skuId, long round) {
        String upd = "{\n" +
                "  \"doc\": {\n" +
                "    \"hotScore\": "+round+"\n" +
                "  }  \n" +
                "}";
        Update update = new Update.Builder(upd).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
        try {
            jestClient.execute(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SkuLsResult makeResultForSearch(SearchResult result, SkuLsParams skuLsParams) {
      SkuLsResult skuLsResult = new SkuLsResult();

//        List<SkuLsInfo> skuLsInfoList;
        ArrayList<SkuLsInfo> skuLsInfosArrayList = new ArrayList<>();
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = result.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
             SkuLsInfo skuLsInfo=hit.source;
             //获取高亮
             if(hit.highlight!=null&&hit.highlight.size()>0){
                 Map<String, List<String>> highlight = hit.highlight;
                 List<String> skuName = highlight.get("skuName");
                 String skuHName = skuName.get(0);
                 skuLsInfo.setSkuName(skuHName);
             }
             skuLsInfosArrayList.add(skuLsInfo);
        }
        skuLsResult.setSkuLsInfoList(skuLsInfosArrayList);
//        long total;
        skuLsResult.setTotal(result.getTotal());
//        long totalPages;
//        long totalPages = result.getTotal()%skuLsParams.getPageSize()==0?result.getTotal()/skuLsParams.getPageSize():result.getTotal()/skuLsParams.getPageSize()+1;
        //（总记录数+每页显示的大小-1）%每页显示的大小=总页数
        long totalPages = (result.getTotal()+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(totalPages);
//        List<String> attrValueIdList;
        ArrayList<String> valueIdList = new ArrayList<>();
        TermsAggregation groupby_attr = result.getAggregations().getTermsAggregation("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            String key = bucket.getKey();
            valueIdList.add(key);
        }
        skuLsResult.setAttrValueIdList(valueIdList);
        return skuLsResult;
    }
    //动态dsl语句
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        // 创建查询bulid
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (skuLsParams.getKeyword() != null) {
            MatchQueryBuilder ma = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(ma);
            // 设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            // 将高亮结果放入查询器中
            searchSourceBuilder.highlight(highlightBuilder);

        }
        // 设置三级分类
        if (skuLsParams.getCatalog3Id() != null) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 设置属性值
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String valueId = skuLsParams.getValueId()[i];
                TermQueryBuilder termsQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termsQueryBuilder);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置分页
        int form = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(form);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 设置按照热度
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        // 设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);

        String query = searchSourceBuilder.toString();
        System.out.println("query=" + query);
        return query;
    }
}
