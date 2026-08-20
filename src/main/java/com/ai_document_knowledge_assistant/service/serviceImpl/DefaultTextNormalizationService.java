package com.ai_document_knowledge_assistant.service.serviceImpl;

import com.ai_document_knowledge_assistant.service.TextNormalizationService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Default implementation of {@link TextNormalizationService}.
 * <p>
 * Keeps the same normalization rules while using precompiled patterns for clarity
 * and to avoid recompiling regular expressions on every call.
 */
@Service
public class DefaultTextNormalizationService implements TextNormalizationService {

    private static final String EMPTY = "";
    private static final String NEWLINE = "\n";
    private static final String DOUBLE_NEWLINE = "\n\n";
    private static final String SINGLE_SPACE = " ";

    private static final Pattern TRAILING_WHITESPACE_BEFORE_NEWLINE =
            Pattern.compile("[ \\t]+\\n");
    private static final Pattern EXCESSIVE_BLANK_LINES =
            Pattern.compile("\\n{3,}");
    private static final Pattern REPEATED_HORIZONTAL_WHITESPACE =
            Pattern.compile("[ \\t]{2,}");

    /**
     * Normalizes input text by applying the existing line-ending and whitespace rules.
     *
     * @param text input text
     * @return normalized text, or empty string when input is null/blank
     */
    @Override
    public String normalize(String text) {
        if (text == null || text.isBlank()) {
            return EMPTY;
        }

        String normalized = text
                .replace("\r\n", NEWLINE)
                .replace('\r', '\n');

        normalized = TRAILING_WHITESPACE_BEFORE_NEWLINE.matcher(normalized)
                .replaceAll(NEWLINE);

        normalized = EXCESSIVE_BLANK_LINES.matcher(normalized)
                .replaceAll(DOUBLE_NEWLINE);

        normalized = REPEATED_HORIZONTAL_WHITESPACE.matcher(normalized)
                .replaceAll(SINGLE_SPACE);

        return normalized.trim();
    }
}