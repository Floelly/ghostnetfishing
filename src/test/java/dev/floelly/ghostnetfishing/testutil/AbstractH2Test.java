package dev.floelly.ghostnetfishing.testutil;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("h2-test")
public abstract class AbstractH2Test extends AbstractTest {
}