package com.google.sps.data;

/** Any sensitive data will be stored in this class **/
public class SensitiveData {

  // I will add this file to .gitignore once the pull request is approved
  private String APIKey = "REDACTED";
  
  public SensitiveData() { }

  public String getAPIKey() {
    return APIKey;
  }

}