package org.atmosphere.cpr;

import org.atmosphere.interceptor.CacheHeadersInterceptor;
import org.atmosphere.interceptor.CorsInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.InvocationOrder;
import org.atmosphere.interceptor.OnDisconnectInterceptor;
import org.atmosphere.interceptor.PaddingAtmosphereInterceptor;
import org.atmosphere.interceptor.Priority;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriorityTest {

    private interface InterceptorIsAnInvocationOrder extends AtmosphereInterceptor, InvocationOrder {

    }

    // This comparator could be used in AtmosphereFramework to order an unique list of interceptors, ordering well default interceptors with others
    // => deeply simplify the class
    private final Comparator<AtmosphereInterceptor> COMPARATOR = new Comparator<AtmosphereInterceptor>() {
        @Override
        public int compare(final AtmosphereInterceptor o1, final AtmosphereInterceptor o2) {
            final Priority p1 = InvocationOrder.class.isAssignableFrom(o1.getClass()) ? InvocationOrder.class.cast(o1).getPriority() : new Priority.Builder(o1).build();
            final Priority p2 = InvocationOrder.class.isAssignableFrom(o2.getClass()) ? InvocationOrder.class.cast(o2).getPriority() : new Priority.Builder(o2).build();
            return p1.compareTo(p2);
        }
    };

    private AtmosphereInterceptor a = mock(InterceptorIsAnInvocationOrder.class);
    private AtmosphereInterceptor b = mock(InterceptorIsAnInvocationOrder.class);
    private AtmosphereInterceptor c = mock(InterceptorIsAnInvocationOrder.class);
    private AtmosphereInterceptor d = mock(InterceptorIsAnInvocationOrder.class);
    private AtmosphereInterceptor e = mock(InterceptorIsAnInvocationOrder.class);
    private List<AtmosphereInterceptor> list = new ArrayList<AtmosphereInterceptor>();

    public void prepare(final boolean prepareDefault) {
        if (prepareDefault) {
            a = new OnDisconnectInterceptor();
            b = new HeartbeatInterceptor();
            c = new CorsInterceptor();
            d = new CacheHeadersInterceptor();
            e = new PaddingAtmosphereInterceptor();
        } else {
            a = mock(InterceptorIsAnInvocationOrder.class);
            b = mock(InterceptorIsAnInvocationOrder.class);
            c = mock(InterceptorIsAnInvocationOrder.class);
            d = mock(InterceptorIsAnInvocationOrder.class);
            e = mock(InterceptorIsAnInvocationOrder.class);

            when(a.toString()).thenReturn("a");
            when(b.toString()).thenReturn("b");
            when(c.toString()).thenReturn("c");
            when(d.toString()).thenReturn("d");
            when(e.toString()).thenReturn("e");
        }

        list.add(c);
        list.add(e);
        list.add(a);
        list.add(d);
        list.add(b);
    }

    @AfterMethod
    public void destroy() {
        list.clear();
    }

    // The test shows how we can take control over the order between interceptors that execute after default interceptors
    @Test
    public void orderNotDefault() {
        prepare(false);

        when(((InterceptorIsAnInvocationOrder) a).getPriority()).thenReturn(new Priority.Builder(a).level(1).build());
        when(((InterceptorIsAnInvocationOrder) b).getPriority()).thenReturn(new Priority.Builder(b).level(2).build());
        when(((InterceptorIsAnInvocationOrder) c).getPriority()).thenReturn(new Priority.Builder(c).level(3).build());
        when(((InterceptorIsAnInvocationOrder) d).getPriority()).thenReturn(new Priority.Builder(d).level(4).build());
        when(((InterceptorIsAnInvocationOrder) e).getPriority()).thenReturn(new Priority.Builder(e).level(5).build());

        Collections.sort(list, COMPARATOR);

        Assert.assertEquals(0, list.indexOf(a));
        Assert.assertEquals(1, list.indexOf(b));
        Assert.assertEquals(2, list.indexOf(c));
        Assert.assertEquals(3, list.indexOf(d));
        Assert.assertEquals(4, list.indexOf(e));
    }

    // The test shows that default interceptors are reordered regarding their index in the array of default interceptors
    @Test
    public void orderWithDefault() {
        prepare(true);
        Collections.sort(list, COMPARATOR);

        Assert.assertTrue(CorsInterceptor.class.isAssignableFrom(list.get(0).getClass()));
        Assert.assertTrue(CacheHeadersInterceptor.class.isAssignableFrom(list.get(1).getClass()));
        Assert.assertTrue(PaddingAtmosphereInterceptor.class.isAssignableFrom(list.get(2).getClass()));
        Assert.assertTrue(HeartbeatInterceptor.class.isAssignableFrom(list.get(3).getClass()));
        Assert.assertTrue(OnDisconnectInterceptor.class.isAssignableFrom(list.get(4).getClass()));
    }

    // Real life: we mix default with other interceptors
    @Test
    public void orderMix() {
        prepare(true);
        prepare(false);

        when(((InterceptorIsAnInvocationOrder) a).getPriority()).thenReturn(new Priority.Builder(a).level(1).build());
        when(((InterceptorIsAnInvocationOrder) b).getPriority()).thenReturn(new Priority.Builder(b).level(2).build());
        when(((InterceptorIsAnInvocationOrder) c).getPriority()).thenReturn(new Priority.Builder(c).afterDefault(false).firstBeforeDefault(true).build());
        when(((InterceptorIsAnInvocationOrder) d).getPriority()).thenReturn(new Priority.Builder(d).afterDefault(false).beforeDefault(true).level(1).build());
        when(((InterceptorIsAnInvocationOrder) e).getPriority()).thenReturn(new Priority.Builder(e).afterDefault(false).beforeDefault(true).level(2).build());

        Collections.sort(list, COMPARATOR);

        Assert.assertEquals(8, list.indexOf(a));
        Assert.assertEquals(9, list.indexOf(b));
        Assert.assertEquals(0, list.indexOf(c));
        Assert.assertEquals(1, list.indexOf(d));
        Assert.assertEquals(2, list.indexOf(e));
    }
}
