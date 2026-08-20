package com.ai_document_knowledge_assistant.parcer;

import com.ai_document_knowledge_assistant.exception.DocumentParsingException;
import com.ai_document_knowledge_assistant.model.ParsedDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class TxtDocumentParser implements DocumentParser {

    private static final String TEXT_CONTENT_TYPE = "text/plain";
    private static final String TXT_EXTENSION = ".txt";

    @Override
    public boolean supports(MultipartFile file) {

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return isTextContentType(contentType)
                || hasTxtExtension(fileName);
    }

    @Override
    public ParsedDocument parse(MultipartFile file) {

        String fileName = file.getOriginalFilename();

        try {

            String text = new String(
                    file.getBytes(),
                    StandardCharsets.UTF_8
            );

            return new ParsedDocument(
                    fileName,
                    TEXT_CONTENT_TYPE,
                    text,
                    1
            );

        } catch (IOException exception) {

            throw new DocumentParsingException(
                    "Failed to read text document: " + fileName,
                    exception
            );
        }
    }

    private boolean isTextContentType(String contentType) {

        return contentType != null
                && TEXT_CONTENT_TYPE.equalsIgnoreCase(contentType);
    }

    private boolean hasTxtExtension(String fileName) {

        return fileName != null
                && fileName.toLowerCase(Locale.ROOT)
                .endsWith(TXT_EXTENSION);
    }
}