import java.nio.file.Path;
import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.decompiler.fernflower.FernflowerDecompiler;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import io.github.coolcrabs.brachyura.processing.ProcessorChain;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {
    @Override
    public VersionMeta createMcVersion() {
        return Minecraft.getVersion("1.18.2");
    }

    @Override
    public String getModId() {
        return "malilib-fabric";
    }

    @Override
    public String getMavenGroup() {
        return "fi.dy.masa.malilib";
    }

    @Override
    public String getVersion() {
        return "0.12.1";
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.18.2+build.1")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.13.3"));
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-resource-loader-v0", "0.4.16+55dca1a4d2"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE, ModDependencyFlag.JIJ);
        d.addMaven("https://maven.terraformersmc.com/releases/", new MavenId("com.terraformersmc:modmenu:3.1.0"), ModDependencyFlag.COMPILE);
        d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("com.google.code.findbugs:jsr305:3.0.2"), ModDependencyFlag.COMPILE);
    }

    @Override
    public int getJavaVersion() {
        return 17;
    }

    @Override
    public BrachyuraDecompiler decompiler() {
        return new FernflowerDecompiler(Maven.getMavenJarDep("https://maven.quiltmc.org/repository/release", new MavenId("org.quiltmc:quiltflower:1.7.0")));
    };

    @Override
    public Path getBuildJarPath() {
        return getBuildLibsDir().resolve(getModId() + "-" + createMcVersion().version + "-" + getVersion() + ".jar");
    }

    @Override
    public ProcessorChain resourcesProcessingChain() {
        return new ProcessorChain(super.resourcesProcessingChain(), new FmjVersionFixer(this));
    }
}
