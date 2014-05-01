package org.atmosphere.interceptor;

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereInterceptor;

public final class Priority implements Comparable<Priority> {

    private Builder builder;

    private boolean isDefault;

    private Priority(final Builder b, final boolean def) {
        builder = b;
        isDefault = def;
    }

    @Override
    public int compareTo(final Priority p) {
        int retval = builder.level - p.builder.level;

        // Always execute first
        if (builder.firstBeforeDefault) {
            if (p.builder.firstBeforeDefault) {
                throw new IllegalStateException("Cannot set more than one AtmosphereInterceptor to be executed first");
            }

            retval = -1;
        // Always execute first
        } else if (p.builder.firstBeforeDefault) {
            if (builder.firstBeforeDefault) {
                throw new IllegalStateException("Cannot set more than one AtmosphereInterceptor to be executed first");
            }

            retval = 1;
        // Comparing this to a default builder
        } else if (p.isDefault) {

            // This must executes before
            if (builder.beforeDefault) {
                retval = -1;
            // This must executes after
            } else  if (builder.afterDefault) {
                retval = 1;
            }
        } else if (isDefault) {
            // This must executes after
            if (p.builder.beforeDefault) {
                retval = 1;
                // This must executes before
            } else  if (p.builder.afterDefault) {
                retval = -1;
            }
        } else if (builder.afterDefault && p.builder.beforeDefault) {
            retval = 1;
        } else if (builder.beforeDefault && p.builder.afterDefault) {
            retval = -1;
        }

        return retval;
    }

    public static class Builder {

        private AtmosphereInterceptor interceptor;

        private boolean afterDefault;

        private boolean beforeDefault;

        private boolean firstBeforeDefault;

        private int level;

        public Builder(final AtmosphereInterceptor forInterceptor) {
            interceptor = forInterceptor;
            afterDefault = true;
            beforeDefault = false;
            firstBeforeDefault = false;
            level = 0;
        }

        public Builder level(final int l) {
            level = l;
            return this;
        }

        public Builder afterDefault(final boolean flag) {
            afterDefault = flag;
            return this;
        }

        public Builder beforeDefault(final boolean flag) {
            beforeDefault = flag;
            return this;
        }

        public Builder firstBeforeDefault(final boolean flag) {
            firstBeforeDefault = flag;
            return this;
        }

        public Priority build() {
            if (afterDefault && beforeDefault) {
                throw new IllegalStateException("Priority configured to be after default AND before default: cannot set true for both");
            }

            if (firstBeforeDefault && (afterDefault || beforeDefault)) {
                throw new IllegalStateException("Cannot execute before/after if priority configured with firstAfterDefault");
            }

            int defaultLevel = -1;

            for (int i = 0; i < AtmosphereFramework.DEFAULT_INTERCEPTORS.length; i++) {
                if (AtmosphereFramework.DEFAULT_INTERCEPTORS[i].isAssignableFrom(interceptor.getClass())) {
                    defaultLevel = i;
                    break;
                }
            }

            final boolean isDefault = defaultLevel != -1;

            if (isDefault) {
                level(defaultLevel);

                if (afterDefault || beforeDefault || firstBeforeDefault) {
                    throw new IllegalStateException("Priority for a default interceptor can only be relative to another default interceptor");
                }
            }

            return new Priority(this, isDefault);
        }
    }
}
