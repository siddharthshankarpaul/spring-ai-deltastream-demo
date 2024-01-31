package com.glitterlabs.chatgptstreaming.service;

import com.alibaba.fastjson.JSONObject;
import com.glitterlabs.chatgptstreaming.configuration.AppConfig;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatgptService {

    private final AppConfig appConfig;
    private final OpenAiChatClient aiClient;
    private final EmbeddingClient embeddingClient;
    private final MilvusServiceClient milvusServiceClient;
    private VectorStore vectorStore;
    private SystemPromptTemplate systemPromptTemplate;


    public ChatgptService(AppConfig appConfig, OpenAiChatClient aiClient, EmbeddingClient embeddingClient, MilvusServiceClient milvusServiceClient, VectorStore vectorStore) {
        this.appConfig = appConfig;
        this.aiClient = aiClient;
        this.embeddingClient = embeddingClient;
        this.milvusServiceClient = milvusServiceClient;
        this.systemPromptTemplate = new SystemPromptTemplate(appConfig.getPrompt());
        this.vectorStore = vectorStore;
    }


    public List<Double> createEmbeddings(String text) {
        List<Double> embed = embeddingClient.embed(text);
        return embed;
    }

    public String getAssistantResponse(String question, String userData) {
        List<Document> documents = getSearch(question);
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("context", getDocumentContent(documents), "data", userData));
        Prompt userPrompt = new Prompt(List.of(systemMessage, new UserMessage(question)));
        String content = aiClient.call(userPrompt).getResult().getOutput().getContent();
        return content;
    }

    private List<Document> getSearch(String question) {
//        return this.vectorStore.similaritySearch(question);
        List<Double> embedding = this.embeddingClient.embed(question);
        var searchParamBuilder = SearchParam.newBuilder()
                .withCollectionName(appConfig.getCollectionName())
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(MetricType.COSINE)
                .withOutFields(Arrays.asList(MilvusVectorStore.DOC_ID_FIELD_NAME, MilvusVectorStore.CONTENT_FIELD_NAME))
                .withTopK(2)
                .withVectors(List.of(embedding.stream().map(Number::floatValue).toList()))
                .withVectorFieldName(MilvusVectorStore.EMBEDDING_FIELD_NAME);
        R<SearchResults> respSearch = milvusServiceClient.search(searchParamBuilder.build());
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());

        return wrapperSearch.getRowRecords(0)
                .stream()
                .map(rowRecord -> {
                    String docId = rowRecord.get(MilvusVectorStore.DOC_ID_FIELD_NAME).toString();
                    String content = (String) rowRecord.get(MilvusVectorStore.CONTENT_FIELD_NAME);
                    JSONObject metadata = new JSONObject();
                    return new Document(docId, content, metadata.getInnerMap());
                })
                .toList();

    }

    private String getDocumentContent(List<Document> docs) {
        return docs.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
    }

}
