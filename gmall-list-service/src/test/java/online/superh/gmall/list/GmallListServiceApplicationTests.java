package online.superh.gmall.list;



import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import online.superh.gmall.config.JestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ComponentScan(basePackages = "online.superh.gmall")
class GmallListServiceApplicationTests {
    @Test
    void contextLoads() {
        System.out.println("你好！！！");
    }
    @Test
    public void testEs() throws IOException {
        String query="{\n" +
                "  \"query\": {\n" +
                "    \"term\": {\n" +
                "      \"actorList.name\": \"张译\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://192.168.88.128:9200")
                .multiThreaded(true)
                .build());
        JestClient jestClient = factory.getObject();
        SearchResult result = jestClient.execute(search);

        List<SearchResult.Hit<Map, Void>> hits = result.getHits(Map.class);
        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map source = hit.source;
            System.err.println(source.get("name"));
        }}

}
