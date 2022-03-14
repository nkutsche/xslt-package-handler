package com.nkutsche.xslt.pkg.handler.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.CollectingDependencyNodeVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "dependency-list", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class DependencyListMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    public MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/classes")
    public File outDir;

    @Parameter(defaultValue = "META-INF/xslt/dependencies", readonly = true, required = true)
    public String filepath;


    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        StringBuilder sb = new StringBuilder();
        try {
            for (DependencyNode dependency:
                createDependencyList()) {
                sb.append(dependency.getArtifact().getGroupId() + ":" + dependency.getArtifact().getArtifactId());
                sb.append("\n");
            }
        } catch (DependencyGraphBuilderException e) {
            throw new MojoExecutionException(e.getMessage());
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

    private List<DependencyNode> createDependencyList() throws DependencyGraphBuilderException {
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(project);
        DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null);
        CollectingDependencyNodeVisitor visitor = new CollectingDependencyNodeVisitor();

        rootNode.accept(visitor);

        List<DependencyNode> nodes = visitor.getNodes();


        return nodes;
    }
}
