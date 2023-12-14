

package im.vector.app.features.rageshake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;

public class BugReporterMultipartBody extends RequestBody {

    
    public interface WriteListener {
        
        void onWrite(long totalWritten, long contentLength);
    }

    private static final MediaType FORM = MediaType.parse("multipart/form-data");

    private static final byte[] COLONSPACE = {':', ' '};
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DASHDASH = {'-', '-'};

    private final ByteString mBoundary;
    private final MediaType mContentType;
    private final List<Part> mParts;
    private long mContentLength = -1L;

    
    private WriteListener mWriteListener;

    
    private List<Long> mContentLengthSize = null;

    private BugReporterMultipartBody(ByteString boundary, List<Part> parts) {
        mBoundary = boundary;
        mContentType = MediaType.parse(FORM + "; boundary=" + boundary.utf8());
        mParts = Util.toImmutableList(parts);
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public long contentLength() throws IOException {
        long result = mContentLength;
        if (result != -1L) return result;
        return mContentLength = writeOrCountBytes(null, true);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        writeOrCountBytes(sink, false);
    }

    
    public void setWriteListener(WriteListener listener) {
        mWriteListener = listener;
    }

    
    private void onWrite(long totalWrittenBytes) {
        if ((null != mWriteListener) && (mContentLength > 0)) {
            mWriteListener.onWrite(totalWrittenBytes, mContentLength);
        }
    }

    
    private long writeOrCountBytes(BufferedSink sink, boolean countBytes) throws IOException {
        long byteCount = 0L;

        Buffer byteCountBuffer = null;
        if (countBytes) {
            sink = byteCountBuffer = new Buffer();
            mContentLengthSize = new ArrayList<>();
        }

        for (int p = 0, partCount = mParts.size(); p < partCount; p++) {
            Part part = mParts.get(p);
            Headers headers = part.headers;
            RequestBody body = part.body;

            sink.write(DASHDASH);
            sink.write(mBoundary);
            sink.write(CRLF);

            if (headers != null) {
                for (int h = 0, headerCount = headers.size(); h < headerCount; h++) {
                    sink.writeUtf8(headers.name(h))
                            .write(COLONSPACE)
                            .writeUtf8(headers.value(h))
                            .write(CRLF);
                }
            }

            MediaType contentType = body.contentType();
            if (contentType != null) {
                sink.writeUtf8("Content-Type: ")
                        .writeUtf8(contentType.toString())
                        .write(CRLF);
            }

            int contentLength = (int) body.contentLength();
            if (contentLength != -1) {
                sink.writeUtf8("Content-Length: ")
                        .writeUtf8(contentLength + "")
                        .write(CRLF);
            } else if (countBytes) {
                
                byteCountBuffer.clear();
                return -1L;
            }

            sink.write(CRLF);

            if (countBytes) {
                byteCount += contentLength;
                mContentLengthSize.add(byteCount);
            } else {
                body.writeTo(sink);

                
                
                
                if ((null != mContentLengthSize) && (p < mContentLengthSize.size())) {
                    onWrite(mContentLengthSize.get(p));
                }
            }
            sink.write(CRLF);
        }

        sink.write(DASHDASH);
        sink.write(mBoundary);
        sink.write(DASHDASH);
        sink.write(CRLF);

        if (countBytes) {
            byteCount += byteCountBuffer.size();
            byteCountBuffer.clear();
        }

        return byteCount;
    }

    private static void appendQuotedString(StringBuilder target, String key) {
        target.append('"');
        for (int i = 0, len = key.length(); i < len; i++) {
            char ch = key.charAt(i);
            switch (ch) {
                case '\n':
                    target.append("%0A");
                    break;
                case '\r':
                    target.append("%0D");
                    break;
                case '"':
                    target.append("%22");
                    break;
                default:
                    target.append(ch);
                    break;
            }
        }
        target.append('"');
    }

    public static final class Part {
        public static Part create(Headers headers, RequestBody body) {
            if (body == null) {
                throw new NullPointerException("body == null");
            }
            if (headers != null && headers.get("Content-Type") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Type");
            }
            if (headers != null && headers.get("Content-Length") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Length");
            }
            return new Part(headers, body);
        }

        public static Part createFormData(String name, String value) {
            return createFormData(name, null, RequestBody.create(value, null));
        }

        public static Part createFormData(String name, String filename, RequestBody body) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            StringBuilder disposition = new StringBuilder("form-data; name=");
            appendQuotedString(disposition, name);

            if (filename != null) {
                disposition.append("; filename=");
                appendQuotedString(disposition, filename);
            }

            return create(Headers.of("Content-Disposition", disposition.toString()), body);
        }

        final Headers headers;
        final RequestBody body;

        private Part(Headers headers, RequestBody body) {
            this.headers = headers;
            this.body = body;
        }
    }

    public static final class Builder {
        private final ByteString boundary;
        private final List<Part> parts = new ArrayList<>();

        public Builder() {
            this(UUID.randomUUID().toString());
        }

        public Builder(String boundary) {
            this.boundary = ByteString.encodeUtf8(boundary);
        }

        
        public Builder addFormDataPart(String name, String value) {
            return addPart(Part.createFormData(name, value));
        }

        
        public Builder addFormDataPart(String name, String filename, RequestBody body) {
            return addPart(Part.createFormData(name, filename, body));
        }

        
        public Builder addPart(Part part) {
            if (part == null) throw new NullPointerException("part == null");
            parts.add(part);
            return this;
        }

        
        public BugReporterMultipartBody build() {
            if (parts.isEmpty()) {
                throw new IllegalStateException("Multipart body must have at least one part.");
            }
            return new BugReporterMultipartBody(boundary, parts);
        }
    }
}