package com.intellij.plugins.haxe.config.sdk.impl;

import com.intellij.plugins.haxe.config.sdk.HaxemojosSdkAdditionalDataBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author Aleksey Fomkin (aleksey.fomkin@gmail.com)
 */
public class HaxemojosSdkAdditionalDataBaseImpl implements HaxemojosSdkAdditionalDataBase {

  private String nekoVersion = "";
  private String haxeVersion = "";

  public HaxemojosSdkAdditionalDataBaseImpl(@NotNull String nekoVersion, @NotNull String haxeVersion) {
    this.nekoVersion = nekoVersion;
    this.haxeVersion = haxeVersion;
  }

  @NotNull
  public String getNekoVersion() {
    return nekoVersion;
  }

  public void setNekoVersion(@NotNull String nekoVersion) {
    this.nekoVersion = nekoVersion;
  }

  @NotNull
  public String getHaxeVersion() {
    return haxeVersion;
  }

  public void setHaxeVersion(@NotNull String haxeVersion) {
    this.haxeVersion = haxeVersion;
  }
}
