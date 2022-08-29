package com.runsidekick.agent.core.app;

import com.runsidekick.agent.core.property.StandardPropertyAccessor;
import com.runsidekick.agent.core.app.impl.PropertyAwareApplicationInfoProvider;
import com.runsidekick.agent.core.util.EnvironmentUtils;
import com.runsidekick.agent.core.test.BaseSidekickTest;
import com.runsidekick.agent.core.test.EnvironmentTestUtils;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author serkan
 */
public class PropertyAwareApplicationInfoProviderTest extends BaseSidekickTest {

    @Test
    public void applicationInfoShouldBeProvidedFromSystemProperties() {
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_ID_PROP_NAME, "test-app-id");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_INSTANCE_ID_PROP_NAME, "test-app-instanceId");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_DOMAIN_NAME_PROP_NAME, "test-app-domainName");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_CLASS_NAME_PROP_NAME, "test-app-className");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_NAME_PROP_NAME, "test-app-name");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_VERSION_PROP_NAME, "test-app-version");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_STAGE_PROP_NAME, "test-app-stage");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-string-tag", "test");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-numeric-tag", "123");
        System.setProperty(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-boolean-tag", "true");

        PropertyAwareApplicationInfoProvider propertyAwareApplicationInfoProvider =
                new PropertyAwareApplicationInfoProvider(new StandardPropertyAccessor());
        ApplicationInfo applicationInfo = propertyAwareApplicationInfoProvider.getApplicationInfo();
        assertThat(applicationInfo, notNullValue());
        assertThat(applicationInfo.getApplicationId(), is("test-app-id"));
        assertThat(applicationInfo.getApplicationInstanceId(), is("test-app-instanceId"));
        assertThat(applicationInfo.getApplicationDomainName(), is("test-app-domainName"));
        assertThat(applicationInfo.getApplicationClassName(), is("test-app-className"));
        assertThat(applicationInfo.getApplicationName(), is("test-app-name"));
        assertThat(applicationInfo.getApplicationVersion(), is("test-app-version"));
        assertThat(applicationInfo.getApplicationStage(), is("test-app-stage"));
        assertThat(applicationInfo.getApplicationRuntime(), is("java"));
        assertThat(applicationInfo.getApplicationRuntimeVersion(), Is.is(EnvironmentUtils.JVM_VERSION));
        assertThat(applicationInfo.getApplicationTags(), notNullValue());
        assertThat(applicationInfo.getApplicationTags().size(), is(3));
        assertThat(applicationInfo.getStringApplicationTag("test-app-string-tag"), is("test"));
        assertThat(applicationInfo.getIntegerApplicationTag("test-app-numeric-tag"), is(123));
        assertThat(applicationInfo.getBooleanApplicationTag("test-app-boolean-tag"), is(true));
    }

    @Test
    public void applicationInfoShouldBeProvidedFromEnvironmentVariables() {
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_ID_PROP_NAME, "test-app-id");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_INSTANCE_ID_PROP_NAME, "test-app-instanceId");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_DOMAIN_NAME_PROP_NAME, "test-app-domainName");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_CLASS_NAME_PROP_NAME, "test-app-className");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_NAME_PROP_NAME, "test-app-name");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_VERSION_PROP_NAME, "test-app-version");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_STAGE_PROP_NAME, "test-app-stage");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-string-tag", "test");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-numeric-tag", "123");
        EnvironmentTestUtils.setEnvironmentVariable(PropertyAwareApplicationInfoProvider.APPLICATION_TAG_PROP_NAME_PREFIX + "test-app-boolean-tag", "true");

        PropertyAwareApplicationInfoProvider standardApplicationInfoProvider =
                new PropertyAwareApplicationInfoProvider(new StandardPropertyAccessor());
        ApplicationInfo applicationInfo = standardApplicationInfoProvider.getApplicationInfo();
        assertThat(applicationInfo, notNullValue());
        assertThat(applicationInfo.getApplicationId(), is("test-app-id"));
        assertThat(applicationInfo.getApplicationInstanceId(), is("test-app-instanceId"));
        assertThat(applicationInfo.getApplicationDomainName(), is("test-app-domainName"));
        assertThat(applicationInfo.getApplicationClassName(), is("test-app-className"));
        assertThat(applicationInfo.getApplicationName(), is("test-app-name"));
        assertThat(applicationInfo.getApplicationVersion(), is("test-app-version"));
        assertThat(applicationInfo.getApplicationStage(), is("test-app-stage"));
        assertThat(applicationInfo.getApplicationRuntime(), is("java"));
        assertThat(applicationInfo.getApplicationRuntimeVersion(), is(EnvironmentUtils.JVM_VERSION));
        assertThat(applicationInfo.getApplicationTags(), notNullValue());
        assertThat(applicationInfo.getApplicationTags().size(), is(3));
        assertThat(applicationInfo.getStringApplicationTag("test-app-string-tag"), is("test"));
        assertThat(applicationInfo.getIntegerApplicationTag("test-app-numeric-tag"), is(123));
        assertThat(applicationInfo.getBooleanApplicationTag("test-app-boolean-tag"), is(true));
    }

}
