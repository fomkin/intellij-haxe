package com.intellij.plugins.haxe.config.sdk.ui;

import javax.swing.*;

/**
 * @author Aleksey Fomkin (aleksey.fomkin@gmail.com)
 */
public class HaxemojosSdkAdditionalDataConfigurablePanel {

  private JTextField haxeVersionTextField;
  private JTextField nekoVersionTextField;
  private JPanel panel;

  public void setNekoVersion(String value) {
    nekoVersionTextField.setText(value);
  }

  public String getNekoVersion() {
    return nekoVersionTextField.getText();
  }

  public void setHaxeVersion(String value) {
    haxeVersionTextField.setText(value);
  }

  public String getHaxeVersion() {
    return haxeVersionTextField.getText();
  }

  public JPanel getRootComponent() {
    return panel;
  }
}
