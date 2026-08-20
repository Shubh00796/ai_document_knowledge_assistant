package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.config.ChunkingProperties;
import com.ai_document_knowledge_assistant.exception.InvalidChunkConfigurationException;
import com.ai_document_knowledge_assistant.model.DocumentChunk;
import com.ai_document_knowledge_assistant.model.ParsedDocument;
import com.ai_document_knowledge_assistant.service.ChunkingService;
import com.ai_document_knowledge_assistant.service.TextNormalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterChunkingService implements ChunkingService {

    private static final Pattern SENTENCE_PATTERN =
            Pattern.compile("(?<=[.!?])\\s+(?=[A-Z•])");

    private static final int WORD_SEPARATOR_LENGTH = 1;
    private static final String WORD_SEPARATOR = " ";

    /*
     * Page number is not available in ParsedDocument currently.
     * -1 means page information is unavailable.
     */
    private static final int UNKNOWN_PAGE_NUMBER = -1;

    private final ChunkingProperties properties;
    private final TextNormalizationService textNormalizationService;

    @Override
    public List<DocumentChunk> chunk(ParsedDocument document) {

        if (document == null || document.text() == null) {
            return List.of();
        }

        String text = textNormalizationService.normalize(document.text());

        if (text == null || text.isBlank()) {
            return List.of();
        }

        int chunkSize = properties.chunkSize();
        int overlap = properties.overlap();

        validateConfiguration(chunkSize, overlap);

        List<TextSegment> segments = splitIntoSentences(text);

        return buildChunks(segments, chunkSize, overlap);
    }

    private List<TextSegment> splitIntoSentences(String text) {

        List<TextSegment> segments = new ArrayList<>();

        Matcher matcher = SENTENCE_PATTERN.matcher(text);

        int start = 0;

        while (matcher.find()) {

            int end = matcher.start();

            addSegment(
                    segments,
                    text,
                    start,
                    end
            );

            start = matcher.end();
        }

        addSegment(
                segments,
                text,
                start,
                text.length()
        );

        return segments;
    }

    private void addSegment(
            List<TextSegment> segments,
            String text,
            int start,
            int end
    ) {

        String content = text.substring(start, end).trim();

        if (content.isEmpty()) {
            return;
        }

        int actualStart = text.indexOf(content, start);
        int actualEnd = actualStart + content.length();

        segments.add(
                new TextSegment(
                        content,
                        actualStart,
                        actualEnd
                )
        );
    }

    private List<DocumentChunk> buildChunks(
            List<TextSegment> segments,
            int chunkSize,
            int overlap
    ) {

        List<DocumentChunk> chunks = new ArrayList<>();
        List<TextSegment> currentSegments = new ArrayList<>();

        int currentLength = 0;
        int chunkIndex = 0;

        for (TextSegment segment : segments) {

            int segmentLength = segment.content().length();

            /*
             * If a single sentence is larger than the configured
             * chunk size, split it by characters.
             */
            if (segmentLength > chunkSize) {

                if (!currentSegments.isEmpty()) {

                    chunks.add(
                            createChunk(
                                    currentSegments,
                                    chunkIndex++
                            )
                    );

                    currentSegments.clear();
                    currentLength = 0;
                }

                List<DocumentChunk> largeSegmentChunks =
                        splitLargeSegment(
                                segment,
                                chunkSize,
                                overlap,
                                chunkIndex
                        );

                chunks.addAll(largeSegmentChunks);

                chunkIndex += largeSegmentChunks.size();

                continue;
            }

            /*
             * Current chunk would exceed the configured size.
             */
            if (!currentSegments.isEmpty()
                    && currentLength
                    + segmentLength
                    + WORD_SEPARATOR_LENGTH
                    > chunkSize) {

                chunks.add(
                        createChunk(
                                currentSegments,
                                chunkIndex++
                        )
                );

                List<TextSegment> overlappingSegments =
                        findOverlapSegments(
                                currentSegments,
                                overlap
                        );

                currentSegments =
                        new ArrayList<>(overlappingSegments);

                currentLength =
                        calculateLength(currentSegments);
            }

            currentSegments.add(segment);

            currentLength +=
                    segmentLength + WORD_SEPARATOR_LENGTH;
        }

        /*
         * Add the final chunk.
         */
        if (!currentSegments.isEmpty()) {

            chunks.add(
                    createChunk(
                            currentSegments,
                            chunkIndex
                    )
            );
        }

        return List.copyOf(chunks);
    }

    private DocumentChunk createChunk(
            List<TextSegment> segments,
            int chunkIndex
    ) {

        TextSegment first = segments.getFirst();
        TextSegment last = segments.getLast();

        String content = segments.stream()
                .map(TextSegment::content)
                .collect(Collectors.joining(WORD_SEPARATOR));

        return new DocumentChunk(
                chunkIndex,
                content,
                first.startOffset(),
                last.endOffset(),
                UNKNOWN_PAGE_NUMBER
        );
    }

    private List<TextSegment> findOverlapSegments(
            List<TextSegment> segments,
            int overlap
    ) {

        List<TextSegment> overlappingSegments =
                new ArrayList<>();

        int length = 0;

        for (int i = segments.size() - 1; i >= 0; i--) {

            TextSegment segment = segments.get(i);

            if (length + segment.content().length() > overlap
                    && !overlappingSegments.isEmpty()) {

                break;
            }

            overlappingSegments.addFirst(segment);

            length +=
                    segment.content().length()
                            + WORD_SEPARATOR_LENGTH;
        }

        return overlappingSegments;
    }

    private int calculateLength(
            List<TextSegment> segments
    ) {

        return segments.stream()
                .mapToInt(
                        segment ->
                                segment.content().length()
                                        + WORD_SEPARATOR_LENGTH
                )
                .sum();
    }

    private List<DocumentChunk> splitLargeSegment(
            TextSegment segment,
            int chunkSize,
            int overlap,
            int startingIndex
    ) {

        List<DocumentChunk> chunks = new ArrayList<>();

        String content = segment.content();

        int start = 0;
        int chunkIndex = startingIndex;

        while (start < content.length()) {

            int end =
                    Math.min(
                            start + chunkSize,
                            content.length()
                    );

            String chunkContent =
                    content.substring(start, end);

            chunks.add(
                    new DocumentChunk(
                            chunkIndex++,
                            chunkContent,
                            segment.startOffset() + start,
                            segment.startOffset() + end,
                            UNKNOWN_PAGE_NUMBER
                    )
            );

            /*
             * Last chunk reached.
             */
            if (end == content.length()) {
                break;
            }

            start = end - overlap;
        }

        return chunks;
    }

    private void validateConfiguration(
            int chunkSize,
            int overlap
    ) {

        if (chunkSize <= 0) {

            throw new InvalidChunkConfigurationException(
                    "Invalid chunk configuration: "
                            + "chunkSize must be greater than 0."
            );
        }

        if (overlap < 0) {

            throw new InvalidChunkConfigurationException(
                    "Invalid chunk configuration: "
                            + "overlap cannot be negative."
            );
        }

        if (overlap >= chunkSize) {

            throw new InvalidChunkConfigurationException(
                    "Invalid chunk configuration: "
                            + "overlap must be smaller than chunkSize."
            );
        }
    }

    private record TextSegment(
            String content,
            int startOffset,
            int endOffset
    ) {
    }
}