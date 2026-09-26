package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.hmall.common.utils.BeanUtils;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
@SpringBootTest(properties = "spring.profiles.active=local")
public class ESDocumentTest {

    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private IItemService iItemService;

    @Test
    public void testIndexDoc() throws IOException {
        // 0.准备文档数据
        // 0.1.根据id查询数据库数据
        Item item = iItemService.getById(317578L);
        // 0.2.把数据库数据转化为文档数据
        ItemDoc itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
        // 1.准备Request
        IndexRequest request = new IndexRequest("items").id(item.getId().toString());
        // 2.准备参数
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
        // 3.发送请求
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
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
