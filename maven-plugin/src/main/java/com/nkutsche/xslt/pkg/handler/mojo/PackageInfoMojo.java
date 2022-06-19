package com.nkutsche.xslt.pkg.handler.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import static com.nkutsche.xslt.pkg.handler.PackageFinder.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "package-info", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class PackageInfoMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    public MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/classes")
    public File outDir;

    @Parameter(defaultValue = PACKAGE_INFO_PATH, readonly = true, required = true)
    public String filepath;

    @Parameter
    public String packagePath;

    @Parameter
    public String[] packagePaths;


    public void execute() throws MojoExecutionException, MojoFailureException {

        if(packagePath != null){
            if(packagePaths == null){
                packagePaths = new String[]{packagePath};
            } else {
                throw new MojoExecutionException("Specify either <packagePath> or <packagePaths> not both!");
            }
        }

        if(packagePaths == null){
            throw new MojoExecutionException("Specify the path to your package library by <packagePath> or <packagePaths>!");
        }

        File outFile = new File(outDir, filepath);

        writePackageInfo(packagePaths, outFile);

    }

    private void writePackageInfo(String[] packagePaths, File outFile) throws MojoExecutionException {
        BufferedWriter writer = null;
        try {
            if(!outFile.getParentFile().exists()){
                outFile.getParentFile().mkdirs();
            }
            Path path = Paths.get(outFile.toURI());
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

            for (String line:
                    packagePaths) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

}
