package com.vibrent.milestone;

import org.junit.Test;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Tag("unitTest")
@DisplayName("Sample Application Unit Test")
public class UserMilestoneApplicationTests {

  @Test
  @DisplayName("Test sample context loads")
  public void contextLoads() {
      // This method needs implementing
      Assert.assertTrue(true);
  }

}
