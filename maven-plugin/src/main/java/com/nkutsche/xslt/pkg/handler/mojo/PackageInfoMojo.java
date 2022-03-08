package com.nkutsche.xslt.pkg.handler.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Properties;

@Mojo(name = "package-info", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class PackageInfoMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    public MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/classes")
    public File outDir;

    @Parameter(defaultValue = "META-INF/xslt/${project.groupId}/${project.artifactId}/packages", readonly = true, required = true)
    public String filepath;

    @Parameter(required = true)
    public String packagePath;


    public void execute() throws MojoExecutionException, MojoFailureException {
        String groupId = project.getGroupId();
        String artifactId = project.getArtifactId();


        Properties properties = new Properties();
        properties.setProperty(groupId + "." + artifactId, packagePath);

        File outFile = new File(outDir, filepath);

        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }

        try {
            properties.store(new FileOutputStream(outFile), null);
        } catch (IOException e) {
            throw new MojoFailureException(e.getMessage());
        }

    }
}
