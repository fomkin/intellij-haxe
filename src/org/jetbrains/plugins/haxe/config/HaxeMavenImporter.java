package org.jetbrains.plugins.haxe.config;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugins.haxe.config.HaxeTarget;
import com.intellij.plugins.haxe.config.sdk.HaxemojosSdkAdditionalDataBase;
import com.intellij.plugins.haxe.config.sdk.HaxemojosSdkData;
import com.intellij.plugins.haxe.config.sdk.HaxemojosSdkType;
import com.intellij.plugins.haxe.ide.module.HaxeModuleSettings;
import com.intellij.plugins.haxe.ide.module.HaxeModuleType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HaxeMavenImporter extends MavenImporter {

  final static Logger logger = Logger.getInstance(HaxeMavenImporter.class);

  public static final String GROUP_ID = "com.tenderowls.opensource";
  public static final String ARTIFACT_ID = "haxemojos-maven-plugin";

  public HaxeMavenImporter() {
    super(GROUP_ID, ARTIFACT_ID);
  }

  @Override
  public void getSupportedPackagings(Collection<String> result) {
    // , "n", "jar"
    for (String packaging : new String[]{"swf", "swc"}) {
      result.add(packaging);
    }
  }

  @NotNull
  @Override
  public ModuleType getModuleType() {
    return HaxeModuleType.getInstance();
  }

  @Override
  public void preProcess(Module module, MavenProject project, MavenProjectChanges changes, MavenModifiableModelsProvider provider) {
  }

  @Override
  public void process(MavenModifiableModelsProvider provider,
                      Module module,
                      MavenRootModelAdapter adapter,
                      MavenProjectsTree tree,
                      MavenProject project,
                      MavenProjectChanges changes,
                      Map<MavenProject, String> map,
                      List<MavenProjectsProcessorTask> tasks) {

    final HaxeModuleSettings settings = HaxeModuleSettings.getInstance(module);
    settings.setHaxeTarget(packagingToHaxeTarget(project.getPackaging()));
    settings.setOutputFileName(project.getFinalName());
    Element eConfiguration = project.getPluginConfiguration(GROUP_ID, ARTIFACT_ID);

    if (eConfiguration != null) {
      Element eMain = eConfiguration.getChild("main");
      settings.setMainClass(eMain == null ? "" : eMain.getValue());
    }
    else {
      settings.setMainClass("");
    }

    final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    final ModifiableRootModel modifiableModel = moduleRootManager.getModifiableModel();
    // TODO setSdk doesn't working
    modifiableModel.setSdk(configureSdk(project));
    modifiableModel.commit();
  }

  @Nullable
  private Sdk configureSdk(MavenProject project) {

    MavenPlugin haxemojosPlugin = null;
    Sdk result = null;

    for (MavenPlugin plugin : project.getPlugins()) {
      if (plugin.getGroupId().equals(GROUP_ID) && plugin.getArtifactId().equals(ARTIFACT_ID)) {
        haxemojosPlugin = plugin;
        break;
      }
    }

    if (haxemojosPlugin != null) {

      String pluginHaxeVersion = null;
      String pluginNekoVersion = null;

      for (MavenId dependency : haxemojosPlugin.getDependencies()) {
        String artifactId = dependency.getArtifactId();
        if (artifactId != null) {
          if (artifactId.equals("haxe-compiler")) {
            pluginHaxeVersion = dependency.getVersion();
          }
          else if (artifactId.equals("nekovm")) {
            pluginNekoVersion = dependency.getVersion();
          }
        }
      }

      if (pluginHaxeVersion != null && pluginNekoVersion != null) {

        for (Sdk sdk : ProjectJdkTable.getInstance().getSdksOfType(HaxemojosSdkType.getInstance())) {
          HaxemojosSdkAdditionalDataBase sdkAdditionalData = (HaxemojosSdkAdditionalDataBase)sdk.getSdkAdditionalData();
          if (sdkAdditionalData != null) {
            String sdkVersion = HaxemojosSdkType.getInstance().getVersionString(sdk);
            boolean versionExists = sdkVersion != null && sdkVersion.equals(haxemojosPlugin.getVersion());
            boolean haxeVersionEquals = sdkAdditionalData.getHaxeVersion().equals(pluginHaxeVersion);
            boolean nekoVersionEquals = sdkAdditionalData.getNekoVersion().equals(pluginNekoVersion);
            if (versionExists && nekoVersionEquals && haxeVersionEquals) {
              result = sdk;
              break;
            }
          }
        }

        if (result == null) {

          String localSdkPath = haxemojosPlugin.getGroupId().replaceAll("\\.", "/") + "/" +
                                haxemojosPlugin.getArtifactId() + "/" +
                                haxemojosPlugin.getVersion() + "/" +
                                haxemojosPlugin.getArtifactId() + "-" +
                                haxemojosPlugin.getVersion();

          localSdkPath = FileUtil.toSystemIndependentName(localSdkPath);
          localSdkPath = new File(project.getLocalRepository(), localSdkPath).getAbsolutePath();

          result = SdkConfigurationUtil.createAndAddSDK(localSdkPath, HaxemojosSdkType.getInstance());
          if (result != null) {
            final SdkModificator modificator = result.getSdkModificator();
            setupSdkRoots(result, pluginHaxeVersion, modificator);
            modificator.setSdkAdditionalData(new HaxemojosSdkData(pluginNekoVersion, pluginHaxeVersion));
            modificator.commitChanges();
          }
          else {
            logger.info("Haxemojos is not resolved. Please run `mvn compile` first.");
          }
        }
      }
      else {
        logger.error("Can't import Haxemojos SDK: there is no haxe-compiler or nekovm dependency");
      }
    }

    return result;
  }

  private void setupSdkRoots(Sdk sdk, String haxeVersion, SdkModificator modificator) {
    VirtualFile sdkRoot = sdk.getHomeDirectory();
    if (sdkRoot != null) {
      VirtualFile haxeRoot = sdkRoot.findChild("haxe-compiler-" + haxeVersion);
      if (haxeRoot != null) {
        VirtualFile stdFile = haxeRoot.findChild("std");
        if (stdFile != null) {
          modificator.addRoot(stdFile, OrderRootType.SOURCES);
          modificator.addRoot(stdFile, OrderRootType.CLASSES);
        }
      }
    }
  }

  @Nullable
  private HaxeTarget packagingToHaxeTarget(String packaging) {
    HaxeTarget result = null;
    if (packaging.equals("swc") || packaging.equals("swf")) {
      result = HaxeTarget.FLASH;
    }
    return result;
  }
}
