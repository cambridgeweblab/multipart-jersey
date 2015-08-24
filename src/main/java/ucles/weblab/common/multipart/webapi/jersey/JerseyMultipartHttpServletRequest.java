package ucles.weblab.common.multipart.webapi.jersey;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static javax.ws.rs.core.MediaType.CHARSET_PARAMETER;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

/**
 * Spring MultipartHttpServletRequest adapter, wrapping a Jersey MultiPart reader to process a request.
 *
 * @since 26/06/15
 */
class JerseyMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {
    final MultiPart multiPart;
    final MultiValueMap<String, BodyPart> parts = new LinkedMultiValueMap<>();
    final MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<>();
    final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

    public JerseyMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
        super(request);
        try {
            final MediaType contentType = MediaType.parseMediaType(request.getHeader(HttpHeaders.CONTENT_TYPE));
            final MultivaluedMap<String, String> userAgentHeader = new MultivaluedHashMap<String, String>() {{
                putSingle(HttpHeaders.USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT));
            }};
            try (InputStream inputStream = request.getInputStream()) {
                multiPart = JerseyMultipartSupport.createJerseyMultiPartReader().readFrom(MultiPart.class, null, null,
                        new javax.ws.rs.core.MediaType(contentType.getType(), contentType.getSubtype(), contentType.getParameters()), userAgentHeader,
                        inputStream
                );
                for (BodyPart bodyPart : multiPart.getBodyParts()) {
                    final String formField = ((FormDataContentDisposition) bodyPart.getContentDisposition()).getName();
                    parts.add(formField, bodyPart);
                    final Optional<javax.ws.rs.core.MediaType> mediaType = Optional.ofNullable(bodyPart.getMediaType());
                    if (bodyPart.getContentDisposition().getFileName() != null) {
                        multipartFiles.add(formField, new JerseyMultipartFileAdapter(bodyPart));
                    } else if (mediaType.map(m -> m.isCompatible(TEXT_PLAIN_TYPE)).orElse(true)) {
                        final Charset charset = mediaType
                                .map(m -> m.getParameters().get(CHARSET_PARAMETER))
                                .map(Charset::forName).orElse(StandardCharsets.ISO_8859_1);
                        try (InputStream formDataStream = ((BodyPartEntity) bodyPart.getEntity()).getInputStream()) {
                            parameters.add(formField,
                                    new Scanner(new InputStreamReader(formDataStream, charset)).useDelimiter("\\A").next());
                        }
                    }
                }
            }
            setMultipartFiles(multipartFiles);
        } catch (Exception ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }

    @Override
    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        final BodyPart part = parts.getFirst(paramOrFileName);
        if (part != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(part.getHeaders());
            return headers;
        }
        return null;
    }

    @Override
    public String getMultipartContentType(String paramOrFileName) {
        final BodyPart part = parts.getFirst(paramOrFileName);
        return part != null ? part.getMediaType().toString() : null;
    }

    @Override
    public String getParameter(String name) {
        return parameters.getFirst(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> toStringArray(entry.getValue())));
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return toStringArray(parameters.get(name));
    }

    void cleanup() {
        multiPart.cleanup();
    }

    String[] toStringArray(List<String> strings) {
        return strings == null || strings.isEmpty() ? null : strings.toArray(new String[strings.size()]);
    }
}
