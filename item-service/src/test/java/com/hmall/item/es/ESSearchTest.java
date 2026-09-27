package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/*@SpringBootTest(properties = "spring.profiles.active=local")*/
public class ESSearchTest {

    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testSearch() throws IOException {
        // 1.创建Request对象
        SearchRequest request = new SearchRequest("items");
        // 2.配置Request参数
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 3.发送请求
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        parseResponse(response);
    }

    @Test
    public void testSearch1() throws IOException {
        SearchRequest request = new SearchRequest("items");
        request.source().query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
                        .filter(QueryBuilders.rangeQuery("price").lte(30000))
                        .filter(QueryBuilders.termQuery("brand", "德亚"))
        );
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        parseResponse(response);
    }

    private static void parseResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("total = " + total);
        // 4.2.命中的数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 4.2.1.获取source结果
            String source = hit.getSourceAsString();
            // 4.2.2转为ItemDoc
            ItemDoc doc = JSONUtil.toBean(source, ItemDoc.class);
            System.out.println("doc = " + doc);
        }
    }


    @BeforeEach
    public void setUp()
    {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("localhost:9200")
        ));
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (restHighLevelClient != null)
        {
            restHighLevelClient.close();
        }
    }


}
