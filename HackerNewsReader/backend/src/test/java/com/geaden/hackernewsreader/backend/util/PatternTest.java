package com.geaden.hackernewsreader.backend.util;

import com.geaden.hackernewsreader.backend.servlets.UploadTopstoriesServlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;

/**
 * Tests for url pattern.
 *
 * @author Gennady Denisov
 */
@RunWith(Parameterized.class)
public class PatternTest {
    @Parameterized.Parameter
    public String url;

    @Parameterized.Parameter(value = 1)
    public Boolean expected;

    @Parameterized.Parameters(name = "{index}: url = {0}, expected = {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"http://www.foo.bar/example.pdf", Boolean.TRUE},
                {"http://www.foo.bar/example.doc", Boolean.TRUE},
                {"http://www.foo.bar/EXAMPLE.DOC", Boolean.TRUE},
                {"http://www.foo.bar/example.docx", Boolean.TRUE},
                {"http://www.foo.bar/example.xlsx", Boolean.TRUE},
                {"http://www.foo.bar/example.swf", Boolean.TRUE},
                {"http://www.foo.bar/example.odt", Boolean.TRUE},
                {"http://www.foo.bar/example.ods", Boolean.TRUE},
                {"http://www.foo.bar", Boolean.FALSE},
                {"http://www.foo.bar/foo.baz", Boolean.FALSE}
        });
    }

    @Test
    public void testUrlMatches() {
        Matcher m = UploadTopstoriesServlet.PATTERN.matcher(url);
        assertEquals(m.matches(), expected);
    }
}
