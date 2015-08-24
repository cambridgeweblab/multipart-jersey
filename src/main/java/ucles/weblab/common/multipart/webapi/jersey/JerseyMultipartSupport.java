package ucles.weblab.common.multipart.webapi.jersey;

import org.glassfish.jersey.media.multipart.MultiPartProperties;
import org.glassfish.jersey.media.multipart.internal.MultiPartReaderClientSide;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import javax.inject.Provider;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

/**
 * Support utilities for using Jersey's multipart support
 *
 * @since 26/06/15
 */
public class JerseyMultipartSupport {
    private JerseyMultipartSupport() { // Prevent instantiation
    }

    static MultiPartReaderClientSide createJerseyMultiPartReader() {
        final MultiPartReaderClientSide readerClientSide = new MultiPartReaderClientSide(new Providers() {
            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> aClass, Type type, Annotation[] annotations, javax.ws.rs.core.MediaType mediaType) {
                return null;
            }

            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> aClass, Type type, Annotation[] annotations, javax.ws.rs.core.MediaType mediaType) {
                return null;
            }

            @Override
            public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> aClass) {
                return null;
            }

            @Override
            public <T> ContextResolver<T> getContextResolver(Class<T> aClass, javax.ws.rs.core.MediaType mediaType) {
                if (aClass == MultiPartProperties.class) {
                    return (ContextResolver<T>) new MultiPartProperties().resolver();
                }
                return null;
            }
        }) {{
            final Field messageBodyWorkers = ReflectionUtils.findField(getClass(), "messageBodyWorkers");
            ReflectionUtils.makeAccessible(messageBodyWorkers);
            ReflectionUtils.setField(messageBodyWorkers, this, (Provider<MessageBodyWorkers>) () -> null);
        }};
        return readerClientSide;
    }
}
