package com.ai_document_knowledge_assistant.parcer;

import com.ai_document_knowledge_assistant.exception.DocumentParsingException;
import com.ai_document_knowledge_assistant.model.ParsedDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfDocumentParser implements DocumentParser {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String PARSE_ERROR_PREFIX = "Failed to parse PDF document: ";

    @Override
    public boolean supports(MultipartFile file) {

        final String contentType = file.getContentType();
        final String fileName = file.getOriginalFilename();

        return PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)
                || hasPdfExtension(fileName);
    }

    @Override
    public ParsedDocument parse(MultipartFile file) {

        final String fileName = file.getOriginalFilename();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            final String extractedText =
                    new PDFTextStripper().getText(document);

            return new ParsedDocument(
                    fileName,
                    PDF_CONTENT_TYPE,
                    extractedText,
                    document.getNumberOfPages()
            );

        } catch (IOException exception) {

            throw new DocumentParsingException(
                    PARSE_ERROR_PREFIX + fileName,
                    exception
            );
        }
    }

    private boolean hasPdfExtension(String fileName) {

        return fileName != null
                && fileName.length() >= PDF_EXTENSION.length()
                && fileName.regionMatches(true,
                fileName.length() - PDF_EXTENSION.length(),
                PDF_EXTENSION,
                0,
                PDF_EXTENSION.length());
    }
}