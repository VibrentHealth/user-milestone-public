package com.vibrent.milestone.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ImageNameSubstitutor;

@Component
@Log4j2
public class ContainerImageNameSubstitutor extends ImageNameSubstitutor {

  private static String prefix = "harbor.ssk8s.vibrenthealth.com/dockerhub/";

  @Override
  public DockerImageName apply(DockerImageName original) {

    String repository = original.asCanonicalNameString();

    if (repository.contains("/")) {
      repository = String.format("%s%s",  prefix, repository );
    } else {
      repository = String.format("%slibrary/%s",  prefix, repository );
    }
    ;
    // convert the original name to something appropriate for
    // our build environment
    return DockerImageName.parse(
      // your code goes here - silly example of capitalising
      // the original name is shown
      repository
    );
  }

  @Override
  protected String getDescription() {
    // used in logs
    return "example image name substitutor";
  }
}
