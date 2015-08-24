package ucles.weblab.common.multipart.webapi.jersey;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Adapts a multipart file body part parsed by Jersey to a Spring MultipartFile.
 *
 * @since 26/06/15
 */
class JerseyMultipartFileAdapter implements MultipartFile {
    private final BodyPart finalFile;
    byte[] fileData;

    public JerseyMultipartFileAdapter(BodyPart finalFile) throws IOException {
        this.finalFile = finalFile;
        fileData = finalFile != null ? FileCopyUtils.copyToByteArray(((BodyPartEntity) finalFile.getEntity()).getInputStream()) : new byte[0];
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getOriginalFilename() {
        return finalFile != null ? finalFile.getContentDisposition().getFileName() : null;
    }

    @Override
    public String getContentType() {
        return finalFile != null ? finalFile.getMediaType().toString() : null;
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public long getSize() {
        return fileData.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return finalFile != null ? fileData : null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException();
    }
}
