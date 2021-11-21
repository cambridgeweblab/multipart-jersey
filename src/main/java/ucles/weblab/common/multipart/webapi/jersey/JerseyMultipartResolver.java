package ucles.weblab.common.multipart.webapi.jersey;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Uses Jersey to resolve multipart file contents.
 * Jersey correctly handles Content-Transfer-Encoding, which neither commons-fileupload not Tomcat container do.
 *
 * @since 26/06/15
 */
public class JerseyMultipartResolver implements MultipartResolver {

    // Same properties as Commons FileUpload

    /**
     * The maximum size permitted for the complete request, as opposed to
     * {@link #fileSizeMax}. A value of -1 indicates no maximum.
     */
    private long sizeMax = -1;

    /**
     * The maximum size permitted for a single uploaded file, as opposed
     * to {@link #sizeMax}. A value of -1 indicates no maximum.
     */
    private long fileSizeMax = -1;

    /**
     * Returns the maximum allowed size of a complete request, as opposed
     * to {@link #getFileSizeMax()}.
     *
     * @return The maximum allowed size, in bytes. The default value of
     *   -1 indicates, that there is no limit.
     *
     * @see #setSizeMax(long)
     *
     */
    public long getSizeMax() {
        return sizeMax;
    }

    /**
     * Sets the maximum allowed size of a complete request, as opposed
     * to {@link #setFileSizeMax(long)}.
     *
     * @param sizeMax The maximum allowed size, in bytes. The default value of
     *   -1 indicates, that there is no limit.
     *
     * @see #getSizeMax()
     *
     */
    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    /**
     * Returns the maximum allowed size of a single uploaded file,
     * as opposed to {@link #getSizeMax()}.
     *
     * @see #setFileSizeMax(long)
     * @return Maximum size of a single uploaded file.
     */
    public long getFileSizeMax() {
        return fileSizeMax;
    }

    /**
     * Sets the maximum allowed size of a single uploaded file,
     * as opposed to {@link #getSizeMax()}.
     *
     * @see #getFileSizeMax()
     * @param fileSizeMax Maximum size of a single uploaded file.
     */
    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        // Same check as in Commons FileUpload...
        if (!"post".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase(Locale.getDefault()).startsWith("multipart/");
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
        return new JerseyMultipartHttpServletRequest(request, sizeMax, fileSizeMax);
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (request instanceof JerseyMultipartHttpServletRequest) {
            ((JerseyMultipartHttpServletRequest) request).cleanup();
        }
    }

}
