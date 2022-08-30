package com.runsidekick.agent.probe.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author serkan
 */
@RunWith(Parameterized.class)
public class ClassUtilsTest {

    @Parameterized.Parameters(name = "language={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "java", "java", "java" }, { "kotlin", "kotlin", "kt" }, { "scala", "scala", "scala" }
        });
    }

    private final String language;
    private final String folder;
    private final String extension;

    public ClassUtilsTest(String language, String folder, String extension) {
        this.language = language;
        this.folder = folder;
        this.extension = extension;
    }

    @Test
    public void classNameShouldBeAbleToExtractedFromSrcMainJavaFolder() {
        String actualClassName = "com.mycompany.MyService";

        String fileName1 = "src/main/" + folder + "/com/mycompany/MyService." + extension;
        String extractedClassName1 = ClassUtils.extractClassName(fileName1);
        assertThat(extractedClassName1, is(actualClassName));

        String fileName2 = "src/main/" + folder + "/com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2 = ClassUtils.extractClassName(fileName2);
        assertThat(extractedClassName2, is(actualClassName));
    }

    @Test
    public void classNameShouldBeAbleToExtractedFromSrcMainJavaFolderUnderRepo() {
        String actualClassName = "com.mycompany.MyService";

        String fileName1 = "my-repo/src/main/" + folder + "/com/mycompany/MyService." + extension;
        String extractedClassName1 = ClassUtils.extractClassName(fileName1);
        assertThat(extractedClassName1, is(actualClassName));

        String fileName2 = "my-repo/src/main/" + folder + "/com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2 = ClassUtils.extractClassName(fileName2);
        assertThat(extractedClassName2, is(actualClassName));
    }

    @Test
    public void classNameShouldBeAbleToExtractedFromSrcFolder() {
        String actualClassName = "com.mycompany.MyService";

        String fileName1 = "src/com/mycompany/MyService." + extension;
        String extractedClassName1 = ClassUtils.extractClassName(fileName1);
        assertThat(extractedClassName1, is(actualClassName));

        String fileName2 = "src/com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2 = ClassUtils.extractClassName(fileName2);
        assertThat(extractedClassName2, is(actualClassName));
    }

    @Test
    public void classNameShouldBeAbleToExtractedFromRootFolder() {
        String actualClassName = "com.mycompany.MyService";

        String fileName1a = "com/mycompany/MyService." + extension;
        String extractedClassName1a = ClassUtils.extractClassName(fileName1a);
        assertThat(extractedClassName1a, is(actualClassName));

        String fileName1b = "/com/mycompany/MyService." + extension;
        String extractedClassName1b = ClassUtils.extractClassName(fileName1b);
        assertThat(extractedClassName1b, is(actualClassName));

        String fileName1c = "//com/mycompany/MyService." + extension;
        String extractedClassName1c = ClassUtils.extractClassName(fileName1c);
        assertThat(extractedClassName1c, is(actualClassName));

        String fileName2a = "com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2a = ClassUtils.extractClassName(fileName2a);
        assertThat(extractedClassName2a, is(actualClassName));

        String fileName2b = "/com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2b = ClassUtils.extractClassName(fileName2b);
        assertThat(extractedClassName2b, is(actualClassName));

        String fileName2c = "//com/mycompany/MyService." + extension + "?xyz=123";
        String extractedClassName2c = ClassUtils.extractClassName(fileName2c);
        assertThat(extractedClassName2c, is(actualClassName));
    }

}
