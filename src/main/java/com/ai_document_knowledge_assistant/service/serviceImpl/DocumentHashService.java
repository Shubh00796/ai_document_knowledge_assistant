package com.ai_document_knowledge_assistant.service.serviceImpl;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class DocumentHashService {

    private static final String SHA_256 = "SHA-256";

    public String sha256(byte[] content) {

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(
                    "Cannot calculate hash of empty content"
            );
        }

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(SHA_256);

            byte[] hash =
                    digest.digest(content);

            StringBuilder hex =
                    new StringBuilder(hash.length * 2);

            for (byte value : hash) {

                hex.append(
                        String.format("%02x", value)
                );
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }
}