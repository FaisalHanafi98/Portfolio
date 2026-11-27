package com.faisal.portfolio.analytics;

import com.faisal.portfolio.common.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "eventId",
            "schemaVersion",
            "eventName",
            "projectSlug",
            "sectionKey",
            "depthBucket",
            "topicLabel",
            "clientOccurredAt"
    );

    private final AnalyticsProperties properties;
    private final AnalyticsIngestionRateLimiter rateLimiter;
    private final AnalyticsIngestionService ingestionService;
    private final ObjectMapper strictObjectMapper;
    private final Validator validator;

    public AnalyticsController(
            AnalyticsProperties properties,
            AnalyticsIngestionRateLimiter rateLimiter,
            AnalyticsIngestionService ingestionService,
            ObjectMapper objectMapper,
            Validator validator
    ) {
        this.properties = properties;
        this.rateLimiter = rateLimiter;
        this.ingestionService = ingestionService;
        this.strictObjectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        this.validator = validator;
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<AnalyticsIngestResultDto>> ingestEvent(HttpServletRequest request)
            throws IOException {
        if (!properties.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .cacheControl(CacheControl.noStore())
                    .body(ApiResponse.error("Analytics ingestion is disabled"));
        }

        rateLimiter.checkAndConsume(request);

        int maxBytes = properties.getMaxBodyBytes();
        if (request.getContentLengthLong() > maxBytes) {
            throw new AnalyticsPayloadTooLargeException("Event payload exceeds size limit");
        }

        byte[] rawBody = readBoundedBytes(request.getInputStream(), maxBytes);
        if (rawBody.length == 0) {
            throw new IllegalArgumentException("Request body must not be empty");
        }

        AnalyticsEventRequest payload;
        try {
            String json = new String(rawBody, StandardCharsets.UTF_8);
            JsonNode tree = strictObjectMapper.readTree(json);
            if (tree == null || !tree.isObject()) {
                throw new IllegalArgumentException("Analytics payload must be a JSON object");
            }
            for (Iterator<String> names = tree.fieldNames(); names.hasNext(); ) {
                if (!ALLOWED_FIELDS.contains(names.next())) {
                    throw new IllegalArgumentException("Disallowed analytics payload field");
                }
            }
            payload = strictObjectMapper.treeToValue(tree, AnalyticsEventRequest.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Malformed or disallowed analytics event payload");
        }

        Set<ConstraintViolation<AnalyticsEventRequest>> violations = validator.validate(payload);
        if (!violations.isEmpty()) {
            throw new IllegalArgumentException("Analytics payload validation failed");
        }

        AnalyticsIngestResultDto result = ingestionService.ingest(payload);
        HttpStatus status = result.deduplicated() ? HttpStatus.OK : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .body(ApiResponse.success(result));
    }

    private static byte[] readBoundedBytes(InputStream inputStream, int maxBytes) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[512];
        int total = 0;
        int read;
        while ((read = inputStream.read(chunk)) != -1) {
            total += read;
            if (total > maxBytes) {
                throw new AnalyticsPayloadTooLargeException("Event payload exceeds size limit");
            }
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }
}
