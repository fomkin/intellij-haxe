package com.intellij.plugins.haxe.config.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.plugins.haxe.config.sdk.ui.HaxemojosSdkAdditionalDataConfigurablePanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Aleksey Fomkin (aleksey.fomkin@gmail.com)
 */
public final class HaxemojosSdkAdditionalDataConfigurable implements AdditionalDataConfigurable {

  private Sdk sdk;

  private final HaxemojosSdkAdditionalDataConfigurablePanel panel;

  public HaxemojosSdkAdditionalDataConfigurable() {
    panel = new HaxemojosSdkAdditionalDataConfigurablePanel();
  }

  @Override
  public void setSdk(Sdk sdk) {
    this.sdk = sdk;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return panel.getRootComponent();
  }

  @Override
  public boolean isModified() {
    return matchSdkAdditionalData(new SdkMatcher<Boolean>() {
      @Override
      public Boolean some(HaxemojosSdkData data) {
        return !panel.getHaxeVersion().equals(data.getHaxeVersion()) ||
               !panel.getNekoVersion().equals(data.getNekoVersion());
      }

      @Override
      public Boolean none() {
        return true;
      }
    });
  }

  @Override
  public void apply() throws ConfigurationException {
    matchSdkAdditionalData(new SdkMatcher<Integer>() {
      @Override
      public Integer some(HaxemojosSdkData data) {
        if (isModified()) {
          HaxemojosSdkData newData = new HaxemojosSdkData(panel.getNekoVersion(), panel.getHaxeVersion());
          final SdkModificator modificator = sdk.getSdkModificator();
          modificator.setSdkAdditionalData(newData);
          ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
              modificator.commitChanges();
            }
          });
        }
        return 0;
      }

      @Override
      public Integer none() {
        return 0;
      }
    });
  }

  @Override
  public void reset() {
    matchSdkAdditionalData(new SdkMatcher<Integer>() {
      @Override
      public Integer some(HaxemojosSdkData data) {
        panel.setNekoVersion(data.getNekoVersion());
        panel.setHaxeVersion(data.getHaxeVersion());
        panel.getRootComponent().repaint();
        return 0;
      }

      @Override
      public Integer none() {
        panel.getRootComponent().repaint();
        return 0;
      }
    });
  }

  @Override
  public void disposeUIResources() {
    // TODO what should i do here?
  }

  private <T> T matchSdkAdditionalData(SdkMatcher<T> matcher) {
    SdkAdditionalData data = sdk.getSdkAdditionalData();
    if (data  instanceof HaxemojosSdkData) {
      return matcher.some((HaxemojosSdkData)data);
    }
    else {
      return matcher.none();
    }
  }

  private static interface SdkMatcher<T> {
    T some(HaxemojosSdkData data);
    T none();
  }
}

