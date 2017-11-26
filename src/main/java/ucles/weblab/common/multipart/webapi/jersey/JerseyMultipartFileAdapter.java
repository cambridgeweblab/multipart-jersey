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
    private final BodyPart bodyPart;
    byte[] fileData;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public JerseyMultipartFileAdapter(BodyPart bodyPart) throws IOException {
        this.bodyPart = bodyPart;
        this.fileData = bodyPart == null ? new byte[0] : FileCopyUtils.copyToByteArray(((BodyPartEntity) bodyPart.getEntity()).getInputStream());
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getOriginalFilename() {
        return bodyPart == null ? null : bodyPart.getContentDisposition().getFileName();
    }

    @Override
    public String getContentType() {
        return bodyPart == null ? null : bodyPart.getMediaType().toString();
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
        return bodyPart == null ? null : fileData;
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
