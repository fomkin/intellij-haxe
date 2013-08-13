package org.jetbrains.plugins.haxe.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.plugins.haxe.config.HaxeTarget;
import com.intellij.plugins.haxe.ide.module.HaxeModuleSettings;
import com.intellij.plugins.haxe.ide.module.HaxeModuleType;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HaxeMavenImporter extends MavenImporter {

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
    Element eConfiguration = project.getPluginConfiguration(GROUP_ID, ARTIFACT_ID);

    if (eConfiguration != null) {
      Element eMain = eConfiguration.getChild("main");
      settings.setMainClass(eMain == null ? "" : eMain.getValue());
      settings.setOutputFileName(project.getFinalName());
    }
    else {
      settings.setMainClass("");
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
