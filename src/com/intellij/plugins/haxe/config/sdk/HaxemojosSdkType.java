/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.plugins.haxe.config.sdk;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.JavadocOrderRootType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.plugins.haxe.HaxeBundle;
import com.intellij.plugins.haxe.HaxeCommonBundle;
import com.intellij.util.xmlb.XmlSerializer;
import icons.HaxeIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HaxemojosSdkType extends SdkType {

  public HaxemojosSdkType() {
    super(HaxeCommonBundle.message("haxemojos.sdk.name"));
  }

  @Override
  public Icon getIcon() {
    return HaxeIcons.Haxemojos_16;
  }

  @Override
  public Icon getIconForAddAction() {
    return HaxeIcons.Haxemojos_16;
  }

  public static HaxemojosSdkType getInstance() {
    return SdkType.findInstance(HaxemojosSdkType.class);
  }

  @Override
  public String getPresentableName() {
    return HaxeBundle.message("haxemojos.sdk.name.presentable");
  }

  @Override
  public String suggestSdkName(String currentSdkName, String sdkHome) {
    return HaxeBundle.message("haxemojos.sdk.name.suggest", getVersionString(sdkHome));
  }

  @Override
  public String getVersionString(String sdkHome) {
    Pattern pattern = Pattern.compile("haxemojos-maven-plugin-(.*)");
    Matcher matcher = pattern.matcher(sdkHome);
    if (matcher.find()) {
      return matcher.group(1);
    }
    else return "NA";
  }

  @Override
  public String suggestHomePath() {
    return "";
  }

  @Override
  public boolean isValidSdkHome(String path) {
    return true;
  }

  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
    return new HaxemojosSdkAdditionalDataConfigurable();
  }

  @Override
  public boolean isRootTypeApplicable(OrderRootType type) {
    return type == OrderRootType.SOURCES || type == OrderRootType.CLASSES || type == JavadocOrderRootType.getInstance();
  }

  @Override
  public void setupSdkPaths(@NotNull Sdk sdk) {
    final SdkModificator modificator = sdk.getSdkModificator();
    if (sdk.getSdkAdditionalData() == null) {
      HaxemojosSdkData haxemojosData = new HaxemojosSdkData("", "");
      modificator.setSdkAdditionalData(haxemojosData);
    }
    modificator.commitChanges();
    super.setupSdkPaths(sdk);
  }

  @Override
  public SdkAdditionalData loadAdditionalData(Element additional) {
    return XmlSerializer.deserialize(additional, HaxemojosSdkData.class);
  }

  @Override
  public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
    if (additionalData instanceof HaxemojosSdkData) {
      XmlSerializer.serializeInto(additionalData, additional);
    }
  }

  @Override
  public FileChooserDescriptor getHomeChooserDescriptor() {
    final FileChooserDescriptor result = super.getHomeChooserDescriptor();
    if (SystemInfo.isMac) {
      result.putUserData(PathChooserDialog.NATIVE_MAC_CHOOSER_SHOW_HIDDEN_FILES, Boolean.TRUE);
    }
    return result;
  }
}
