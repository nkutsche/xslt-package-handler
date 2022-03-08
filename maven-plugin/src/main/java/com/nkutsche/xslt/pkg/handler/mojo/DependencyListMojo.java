package com.nkutsche.xslt.pkg.handler.mojo;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;

@Mojo(name = "package-list", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class DependencyListMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    public MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/classes")
    public File outDir;

    @Parameter(defaultValue = "META-INF/xslt/dependencies", readonly = true, required = true)
    public String filepath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        StringBuilder sb = new StringBuilder();
        for (Dependency dependency:
            project.getDependencies()) {
            sb.append(dependency.getGroupId() + ":" + dependency.getArtifactId());
            sb.append("\n");
        }

        File outFile = new File(outDir, filepath);

        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }

        try {
            FileWriter fw = new FileWriter(outFile);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
