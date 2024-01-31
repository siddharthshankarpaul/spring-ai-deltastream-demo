package com.glitterlabs.chatgptstreaming.service;

import com.alibaba.fastjson.JSONObject;
import com.glitterlabs.chatgptstreaming.configuration.AppConfig;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.MutationResult;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilvusService {

    private final MilvusServiceClient milvusServiceClient;
    private final ChatgptService chatgptService;
    private final AppConfig appConfig;

    public void addDocuments(String text) {
        List<Double> embeddings = chatgptService.createEmbeddings(text);
        JSONObject row = new JSONObject();
        row.put(MilvusVectorStore.CONTENT_FIELD_NAME, text);
        row.put(MilvusVectorStore.EMBEDDING_FIELD_NAME, embeddings.stream().map(Double::floatValue).collect(Collectors.toList()));
        milvusServiceClient.insert(InsertParam.newBuilder()
                .withCollectionName(appConfig.getCollectionName())
                .withRows(List.of(row))
                .build());
    }

    public void dropCollection() {
        milvusServiceClient.dropCollection(DropCollectionParam.newBuilder().withCollectionName(appConfig.getCollectionName()).build());
    }

    public void createAndIndexAndLoadCollection() {
        milvusServiceClient.createCollection(createCollection());
        milvusServiceClient.createIndex(indexCollection());
        milvusServiceClient.loadCollection(loadCollection());
    }

    private CreateCollectionParam createCollection() {
        FieldType fieldType1 = FieldType.newBuilder()
                .withName(MilvusVectorStore.DOC_ID_FIELD_NAME)
                .withDescription("record identification")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();

        FieldType fieldType2 = FieldType.newBuilder()
                .withName(MilvusVectorStore.EMBEDDING_FIELD_NAME)
                .withDescription("rule embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(1536)
                .build();

        FieldType fieldType3 = FieldType.newBuilder()
                .withName(MilvusVectorStore.CONTENT_FIELD_NAME)
                .withDescription("rule text")
                .withDataType(DataType.VarChar)
                .withMaxLength(500)
                .build();

        return CreateCollectionParam.newBuilder()
                .withCollectionName(appConfig.getCollectionName())
                .withShardsNum(2)
                .withEnableDynamicField(false)
                .addFieldType(fieldType1)
                .addFieldType(fieldType2)
                .addFieldType(fieldType3)
                .build();
    }

    private LoadCollectionParam loadCollection() {
        return LoadCollectionParam.newBuilder()
                .withCollectionName(appConfig.getCollectionName())
                .build();
    }

    private CreateIndexParam indexCollection() {
        return CreateIndexParam.newBuilder()
                .withCollectionName(appConfig.getCollectionName())
                .withFieldName(MilvusVectorStore.EMBEDDING_FIELD_NAME)
                .withIndexType(IndexType.FLAT)
                .withSyncMode(Boolean.TRUE)
                .withMetricType(MetricType.COSINE)
                .build();
    }


    public void addPolicy(Long v1, Long v2, Long v3, Long v4, Long v5) {
        try {
            String userLevelRules = appConfig.getUserLevelRules().getContentAsString(Charset.defaultCharset());
            addDocuments(String.format(userLevelRules, v1, v2, v3, v4, v5));
            String userMembershipRules = appConfig.getUserMembershipRules().getContentAsString(Charset.defaultCharset());
            addDocuments(String.format(userMembershipRules, v1, v2, v3, v4, v5));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
