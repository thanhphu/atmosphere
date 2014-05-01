/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.atmosphere.interceptor;

import org.atmosphere.cpr.AtmosphereInterceptorAdapter;

/**
 * <p>
 * Base class that must be sub-classed to create an {@link org.atmosphere.cpr.AtmosphereInterceptor}
 * used by default by the {@link org.atmosphere.cpr.AtmosphereFramework framework}.
 * </p>
 *
 * <p>
 * It overrides {@link #getPriority()} to return an instance in a valid state for any default interceptor.
 * The priority between two default interceptors will follow the order of declaration in
 * {@link org.atmosphere.cpr.AtmosphereFramework#DEFAULT_INTERCEPTORS}.
 * </p>
 *
 * @author Guillaume DROUET
 * @version 1.0
 * @since 2.2
 */
public class DefaultInterceptor extends AtmosphereInterceptorAdapter {

    /**
     * The priority for this interceptor.
     */
    private final Priority priority;

    /**
     * Builds a new instance.
     */
    public DefaultInterceptor() {
        priority = new Priority.Builder(this).afterDefault(false).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Priority getPriority() {
        return priority;
    }
}
