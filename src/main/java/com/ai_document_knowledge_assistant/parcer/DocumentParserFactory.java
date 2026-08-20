package com.ai_document_knowledge_assistant.parcer;

import com.ai_document_knowledge_assistant.exception.UnsupportedDocumentTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentParserFactory {

    private static final String EMPTY_DOCUMENT_MESSAGE = "Document file must not be empty";
    private static final String UNSUPPORTED_DOCUMENT_MESSAGE = "Unsupported document type: ";
    private static final String UNKNOWN_FILE_TYPE = "unknown";

    private final List<DocumentParser> parsers;

    public DocumentParser getParser(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new UnsupportedDocumentTypeException(EMPTY_DOCUMENT_MESSAGE);
        }

        return parsers.stream()
                .filter(parser -> parser.supports(file))
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedDocumentTypeException(
                                UNSUPPORTED_DOCUMENT_MESSAGE + resolveFileType(file)
                        )
                );
    }

    private String resolveFileType(MultipartFile file) {

        String contentType = file.getContentType();

        if (hasText(contentType)) {
            return contentType;
        }

        return Optional.ofNullable(file.getOriginalFilename())
                .filter(this::hasText)
                .orElse(UNKNOWN_FILE_TYPE);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}