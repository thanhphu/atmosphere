package org.atmosphere.interceptor;

import org.atmosphere.cpr.AtmosphereInterceptor;

// Candidate for replacing InvokationOrder
// Any InvocationOrder is actually an AtmosphereInterceptor
public interface InvocationOrder extends AtmosphereInterceptor {
    Priority getPriority();
}
