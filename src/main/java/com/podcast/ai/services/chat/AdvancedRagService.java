package com.podcast.ai.services.chat;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.time.Duration.ofSeconds;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdvancedRagService {
    @Value("${hugging-face.api-key}")
    private String hfApiKey;
    @Value("${qgrant.api-key}")
    private String qgrantApiKey;
    @Value("${qgrant.grpc-host}")
    private String qgrantGrpcHost;

    public String generateAnswer(ClientChatRequest chatRequest) {
        try {
            QuestionAnsweringAgent agent = advancedQuestionAnsweringAgent(chatRequest.getPodcastGuid());
            return agent.answer(chatRequest.getUserMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "RAG: Exception occurred while generating answer.";
        }
    }

    private QuestionAnsweringAgent advancedQuestionAnsweringAgent(String podcastGuid) {
        ChatLanguageModel chatModel = getChatModel();

        // Chat memory to remember previous interactions
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        ContentRetriever contentRetriever = getEmbeddingStoreContentRetriever("podcast_" + podcastGuid);

        PromptTemplate promptTemplate = PromptTemplate.from(
                        """ 
                        You are a question answering bot.
                        You will be given a QUESTION and a set of paragraphs in the CONTENT section.
                        You need to answer the question using the text present in the CONTENT section.
                        If the answer is not present in the CONTENT text then reply: `I don't have answer of this question`
                        CONTENT: {{contents}} QUESTION: {{userMessage}}
                        """
        );

        ContentInjector contentInjector = DefaultContentInjector.builder()
                .promptTemplate(promptTemplate)
                .build();

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentInjector(contentInjector)
                .build();

        return AiServices.builder(QuestionAnsweringAgent.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();
    }

    private ContentRetriever getEmbeddingStoreContentRetriever(String collectionName) {
        EmbeddingStore<TextSegment> embeddingStore =
                QdrantEmbeddingStore.builder()
                        .collectionName(collectionName)
                        .host(qgrantGrpcHost)
                        .port(6334)
                        .apiKey(qgrantApiKey)
                        .useTls(true)
                        .build();

        HuggingFaceEmbeddingModel embeddingModel = getEmbeddingModel();

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();
    }

    private HuggingFaceEmbeddingModel getEmbeddingModel() {
        return HuggingFaceEmbeddingModel.builder()
                .accessToken(hfApiKey)
                .waitForModel(true)
                .timeout(ofSeconds(60))
                .build();
    }

    protected ChatLanguageModel getChatModel() {
        return HuggingFaceChatModel.builder()
                .accessToken(hfApiKey)
                .modelId("HuggingFaceH4/zephyr-7b-beta")
                .timeout(ofSeconds(15))
                .temperature(0.7)
                .maxNewTokens(20)
                .waitForModel(true)
                .build();
    }
}
