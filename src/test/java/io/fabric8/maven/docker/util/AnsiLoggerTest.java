package io.fabric8.maven.docker.util;
/*
 *
 * Copyright 2016 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import org.apache.maven.monitor.logging.DefaultLog;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.fusesource.jansi.Ansi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author roland
 * @since 07/10/16
 */
class AnsiLoggerTest {

    @Test
    void emphasizeDebug() {
        TestLog testLog = new TestLog() {
            @Override
            public boolean isDebugEnabled() {
                return true;
            }
        };

        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        logger.debug("Debug messages do not interpret [[*]]%s[[*]]", "emphasis");
        Assertions.assertEquals("T>Debug messages do not interpret [[*]]emphasis[[*]]",
                testLog.getMessage());
    }

    @Test
    void emphasizeInfoWithDebugEnabled() {
        TestLog testLog = new TestLog() {
            @Override
            public boolean isDebugEnabled() {
                return true;
            }
        };

        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        logger.info("Info messages do not apply [[*]]%s[[*]] when debug is enabled", "color codes");
        Assertions.assertEquals("T>Info messages do not apply color codes when debug is enabled",
                testLog.getMessage());
    }

    @Test
    void verboseEnabled() {
        String[] data = {
            "build", "Test",
            "api", null,
            "bla", "log: Unknown verbosity group bla. Ignoring...",
            "all", "Test",
            "", "Test",
            "true", "Test",
            "false", null
        };
        for (int i = 0; i < data.length; i += 2) {
            TestLog testLog = new TestLog();
            AnsiLogger logger = new AnsiLogger(testLog, false, data[i], false, "");
            logger.verbose(Logger.LogVerboseCategory.BUILD, "Test");
            Assertions.assertEquals(data[i+1], testLog.getMessage());
        }
    }
    @Test

    void emphasizeInfo() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, "build", false, "T>");
        Ansi ansi = Ansi.ansi();
        logger.info("Yet another [[*]]Test[[*]] %s", "emphasis");
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_INFO)
                         .a("T>")
                         .a("Yet another ")
                         .fgBright(AnsiLogger.COLOR_EMPHASIS)
                         .a("Test")
                         .fg(AnsiLogger.COLOR_INFO)
                         .a(" emphasis")
                         .reset().toString(),
                     testLog.getMessage());
    }

    @Test
    void emphasizeInfoSpecificColor() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        Ansi ansi = new Ansi();
        logger.info("Specific [[C]]color[[C]] %s","is possible");
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_INFO)
                        .a("T>")
                        .a("Specific ")
                        .fg(Ansi.Color.CYAN)
                        .a("color")
                        .fg(AnsiLogger.COLOR_INFO)
                        .a(" is possible")
                        .reset().toString(),
                testLog.getMessage());
    }

    @Test
    void emphasizeInfoIgnoringEmpties() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        Ansi ansi = new Ansi();
        // Note that the closing part of the emphasis does not need to match the opening.
        // E.g. [[b]]Blue[[*]] works just like [[b]]Blue[[b]]
        logger.info("[[b]][[*]]Skip[[*]][[*]]ping [[m]]empty strings[[/]] %s[[*]][[c]][[c]][[*]]","is possible");
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_INFO)
                        .a("T>")
                        .a("Skipping ")
                        .fgBright(Ansi.Color.MAGENTA)
                        .a("empty strings")
                        .fg(AnsiLogger.COLOR_INFO)
                        .a(" is possible")
                        .reset().toString(),
                testLog.getMessage());
    }

    @Test
    void emphasizeInfoSpecificBrightColor() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        Ansi ansi = new Ansi();
        logger.info("Lowercase enables [[c]]bright version[[c]] of %d colors",Ansi.Color.values().length - 1);
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_INFO)
                        .a("T>")
                        .a("Lowercase enables ")
                        .fgBright(Ansi.Color.CYAN)
                        .a("bright version")
                        .fg(AnsiLogger.COLOR_INFO)
                        .a(" of 8 colors")
                        .reset().toString(),
                testLog.getMessage());
    }

    @Test
    void emphasizeInfoWithoutColor() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, false, null, false, "T>");
        logger.info("Disabling color causes logger to [[*]]interpret and remove[[*]] %s","emphasis");
        Assertions.assertEquals("T>Disabling color causes logger to interpret and remove emphasis",
                     testLog.getMessage());
    }

    @Test
    void emphasizeWarning() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        Ansi ansi = new Ansi();
        logger.warn("%s messages support [[*]]emphasis[[*]] too","Warning");
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_WARNING)
                         .a("T>")
                         .a("Warning messages support ")
                         .fgBright(AnsiLogger.COLOR_EMPHASIS)
                         .a("emphasis")
                         .fg(AnsiLogger.COLOR_WARNING)
                         .a(" too")
                         .reset().toString(),
                     testLog.getMessage());
    }

    @Test
    void emphasizeError() {
        TestLog testLog = new TestLog();
        AnsiLogger logger = new AnsiLogger(testLog, true, null, false, "T>");
        Ansi ansi = new Ansi();
        logger.error("Error [[*]]messages[[*]] could emphasise [[*]]%s[[*]]","many things");
        Assertions.assertEquals(ansi.fg(AnsiLogger.COLOR_ERROR)
                         .a("T>")
                         .a("Error ")
                         .fgBright(AnsiLogger.COLOR_EMPHASIS)
                         .a("messages")
                         .fg(AnsiLogger.COLOR_ERROR)
                         .a(" could emphasise ")
                         .fgBright(AnsiLogger.COLOR_EMPHASIS)
                         .a("many things")
                         .reset()
                         .toString(),
                     testLog.getMessage());
    }


    private class TestLog extends DefaultLog {
        private String message;

        public TestLog() {
            super(new ConsoleLogger());
        }

        @Override
        public void debug(CharSequence content) {
            this.message = content.toString();
            super.debug(content);
        }

        @Override
        public void info(CharSequence content) {
            this.message = content.toString();
            super.info(content);
        }

        @Override
        public void warn(CharSequence content) {
            this.message = content.toString();
            super.warn(content);
        }

        @Override
        public void error(CharSequence content) {
            this.message = content.toString();
            super.error(content);
        }

        void reset() {
            message = null;
        }

        public String getMessage() {
            return message;
        }
    }

}
