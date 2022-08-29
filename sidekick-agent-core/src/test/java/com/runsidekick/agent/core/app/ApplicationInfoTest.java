package com.runsidekick.agent.core.app;

import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author serkan
 */
public class ApplicationInfoTest {

    private ApplicationInfo createTestApplicationInfo() {
        return new ApplicationInfo(
                "test-app-id",
                "test-app-instanceId",
                "test-app-domainName",
                "test-app-className",
                "test-app-name",
                "test-app-version",
                "test-app-stage",
                "test-app-runtime",
                "test-app-runtimeVersion",
                new HashMap<String, Object>() {{
                    put("test-app-string-tag", "test");
                    put("test-app-non-decimal-tag", 123);
                    put("test-app-decimal-tag", 123.456);
                    put("test-app-boolean-tag", true);
                }});
    }

    @Test
    public void applicationInformationShouldBeProvidedSuccessfullyFromGivenProperties() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        assertThat(applicationInfo.getApplicationId(), is("test-app-id"));
        assertThat(applicationInfo.getApplicationInstanceId(), is("test-app-instanceId"));
        assertThat(applicationInfo.getApplicationDomainName(), is("test-app-domainName"));
        assertThat(applicationInfo.getApplicationClassName(), is("test-app-className"));
        assertThat(applicationInfo.getApplicationName(), is("test-app-name"));
        assertThat(applicationInfo.getApplicationVersion(), is("test-app-version"));
        assertThat(applicationInfo.getApplicationStage(), is("test-app-stage"));
        assertThat(applicationInfo.getApplicationRuntime(), is("test-app-runtime"));
        assertThat(applicationInfo.getApplicationRuntimeVersion(), is("test-app-runtimeVersion"));
        assertThat(applicationInfo.getApplicationTags(), notNullValue());
        assertThat(applicationInfo.getApplicationTags().size(), is(4));
        assertThat(applicationInfo.getStringApplicationTag("test-app-string-tag"), is("test"));
        assertThat(applicationInfo.getByteApplicationTag("test-app-non-decimal-tag"), is((byte) 123));
        assertThat(applicationInfo.getShortApplicationTag("test-app-non-decimal-tag"), is((short) 123));
        assertThat(applicationInfo.getIntegerApplicationTag("test-app-non-decimal-tag"), is(123));
        assertThat(applicationInfo.getFloatApplicationTag("test-app-non-decimal-tag"), is(123.0F));
        assertThat(applicationInfo.getLongApplicationTag("test-app-non-decimal-tag"), is(123L));
        assertThat(applicationInfo.getDoubleApplicationTag("test-app-non-decimal-tag"), is(123.0));
        assertThat(applicationInfo.getByteApplicationTag("test-app-decimal-tag"), is((byte) 123));
        assertThat(applicationInfo.getShortApplicationTag("test-app-decimal-tag"), is((short) 123));
        assertThat(applicationInfo.getIntegerApplicationTag("test-app-decimal-tag"), is(123));
        assertThat(applicationInfo.getFloatApplicationTag("test-app-decimal-tag"), is(123.456F));
        assertThat(applicationInfo.getLongApplicationTag("test-app-decimal-tag"), is(123L));
        assertThat(applicationInfo.getDoubleApplicationTag("test-app-decimal-tag"), is(123.456));
        assertThat(applicationInfo.getApplicationTags().get("test-app-boolean-tag"), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingStringTypedTagFromNumericTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getStringApplicationTag("test-app-non-decimal-tag");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingStringTypedTagFromBooleanTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getStringApplicationTag("test-app-boolean-tag");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNumericTypedTagFromStringTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getIntegerApplicationTag("test-app-string-tag");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingNumericTypedTagFromBooleanTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getIntegerApplicationTag("test-app-boolean-tag");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingBooleanTypedTagFromStringTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getBooleanApplicationTag("test-app-string-tag");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingBooleanTypedTagFromNumericTypedTagShouldFail() {
        ApplicationInfo applicationInfo = createTestApplicationInfo();
        applicationInfo.getBooleanApplicationTag("test-app-non-decimal-tag");
    }

}
