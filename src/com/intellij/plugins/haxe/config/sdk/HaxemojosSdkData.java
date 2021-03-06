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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.plugins.haxe.config.sdk.impl.HaxemojosSdkAdditionalDataBaseImpl;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * @author Aleksey Fomkin (aleksey.fomkin@gmail.com)
 */
public class HaxemojosSdkData extends HaxemojosSdkAdditionalDataBaseImpl
  implements SdkAdditionalData, PersistentStateComponent<HaxemojosSdkData> {

  public HaxemojosSdkData() {
    super("", "");
  }

  public HaxemojosSdkData(String nekoVersion, String haxeVersion) {
    super(nekoVersion, haxeVersion);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new HaxemojosSdkData(getNekoVersion(), getHaxeVersion());
  }

  public HaxemojosSdkData getState() {
    return this;
  }

  public void loadState(HaxemojosSdkData state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}