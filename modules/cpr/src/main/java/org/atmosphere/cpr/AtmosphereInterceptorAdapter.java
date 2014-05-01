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
package org.atmosphere.cpr;

import org.atmosphere.interceptor.InvocationOrder;
import org.atmosphere.interceptor.InvokationOrder;
import org.atmosphere.interceptor.Priority;

/**
 * A Simple {@link AtmosphereInterceptor} that creates an {@link AtmosphereInterceptorWriter} and sets it as
 * the default {@link AsyncIOWriter} on an {@link AtmosphereResponse}.
 *
 * @author Jeanfrancois Arcand
 */
public abstract class AtmosphereInterceptorAdapter implements AtmosphereInterceptor, InvokationOrder, InvocationOrder {

    @Override
    public void configure(AtmosphereConfig config) {
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        AtmosphereResponse res = r.getResponse();
        if (res.getAsyncIOWriter() == null) {
            res.asyncIOWriter(new AtmosphereInterceptorWriter());
        }
        return Action.CONTINUE;
    }

    @Override
    public void postInspect(AtmosphereResource r) {
    }

    @Override
    public PRIORITY priority() {
        return InvokationOrder.AFTER_DEFAULT;
    }

    // By default, it should be an 'after default' priority but for the test we set priority that works with default interceptors
    // If accepted, we should create an intermediate class between this one and all the default interceptors to override this method
    @Override
    public Priority getPriority() {
        return new Priority.Builder(this).afterDefault(false).build();
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
}
