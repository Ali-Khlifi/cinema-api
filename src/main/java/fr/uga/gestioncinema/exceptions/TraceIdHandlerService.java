package fr.uga.gestioncinema.exceptions;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TraceIdHandlerService {

    private final Tracer tracer;

    public TraceIdHandlerService(Tracer tracer) {
        this.tracer = tracer;
    }

    public String getTraceId() {
        return Optional.of(tracer).map(Tracer::currentTraceContext)
                .map(CurrentTraceContext::context).map(TraceContext::traceId).orElse("");
    }

    public String getSpanId() {
        return Optional.of(tracer).map(Tracer::currentTraceContext)
                .map(CurrentTraceContext::context).map(TraceContext::spanId).orElse("");
    }

}
