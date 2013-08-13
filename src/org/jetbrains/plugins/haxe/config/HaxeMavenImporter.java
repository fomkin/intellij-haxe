package org.jetbrains.plugins.haxe.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.plugins.haxe.ide.module.HaxeModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.util.List;
import java.util.Map;

public class HaxeMavenImporter extends MavenImporter {

  public HaxeMavenImporter() {
    super("com.tenderowls.opensource", "haxemojos-maven-plugin");
  }


  @NotNull
  @Override
  public ModuleType getModuleType() {
    return HaxeModuleType.getInstance();
  }

  @Override
  public void preProcess(Module module, MavenProject project, MavenProjectChanges changes, MavenModifiableModelsProvider provider) {
    module.
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

  }
}
