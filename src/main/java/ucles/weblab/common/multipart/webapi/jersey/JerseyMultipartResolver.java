package ucles.weblab.common.multipart.webapi.jersey;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Uses Jersey to resolve multipart file contents.
 * Jersey correctly handles Content-Transfer-Encoding, which neither commons-fileupload not Tomcat container do.
 *
 * @since 26/06/15
 */
public class JerseyMultipartResolver implements MultipartResolver {

    // TODO: Use MultipartProperties for max size etc.
    //   @Autowired
    //   private MultipartProperties multipartProperties = new MultipartProperties();

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        // Same check as in Commons FileUpload...
        if (!"post".equals(request.getMethod().toLowerCase())) {
            return false;
        }
        String contentType = request.getContentType();
        return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new JerseyMultipartHttpServletRequest(request);
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (request != null && request instanceof JerseyMultipartHttpServletRequest) {
            ((JerseyMultipartHttpServletRequest) request).cleanup();
        }
    }

}
