package com.podcast.ai.services.chat;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Slf4j
@Service
public class DataIngestionService {
    @Value("${hugging-face.api-key}")
    private String hfApiKey;
    @Value("${qgrant.api-key}")
    private String qgrantApiKey;
    @Value("${qgrant.grpc-host}")
    private String qgrantGrpcHost;

    public void createCollection(String podcastGuid) {
        QdrantClient client = getQdrantClient();
        try {
            client.createCollectionAsync(
                    "podcast_" + podcastGuid,
                    VectorParams.newBuilder().setDistance(Distance.Dot).setSize(384).build()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void insertDocuments(String episodeContent, String podcastGuid) {
        try {
            HuggingFaceEmbeddingModel embeddingModel = getEmbeddingModel();
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(1000, 150);
            Document doc = Document.from(episodeContent, Metadata.from("document-type", "history-document"));

            EmbeddingStore<TextSegment> embeddingStore = QdrantEmbeddingStore.builder()
                    .collectionName("podcast_" + podcastGuid)
                    .host(qgrantGrpcHost)
                    .port(6334)
                    .apiKey(qgrantApiKey)
                    .useTls(true)
                    .build();
            List<TextSegment> segments = documentSplitter.split(doc);
            Response<List<Embedding>> embeddingResponse = embeddingModel.embedAll(segments);
            List<Embedding> embeddings = embeddingResponse.content();
            embeddingStore.addAll(embeddings, segments);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private QdrantClient getQdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(qgrantGrpcHost, 6334, true)
                        .withApiKey(qgrantApiKey)
                        .build()
        );
    }

    private HuggingFaceEmbeddingModel getEmbeddingModel() {
        return HuggingFaceEmbeddingModel.builder()
                .accessToken(hfApiKey)
                .waitForModel(true)
                .timeout(ofSeconds(60))
                .build();
    }
}
