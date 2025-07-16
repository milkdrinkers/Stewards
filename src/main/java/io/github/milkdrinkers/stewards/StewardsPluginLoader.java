package io.github.milkdrinkers.stewards;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class StewardsPluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        PluginLibraries pluginLibraries = load();
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        classpathBuilder.addLibrary(resolver);
    }

    public PluginLibraries load() {
        try (
            final InputStream is = getClass().getResourceAsStream("/paper-libraries.json")
        ) {
            if (is == null)
                throw new RuntimeException("InputStream of \"/paper-libraries.json\" is null");

            try (
                final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                final BufferedReader br = new BufferedReader(isr)
            ) {
                return new Gson().fromJson(br, PluginLibraries.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record PluginLibraries(@Nullable Map<String, String> repositories, @Nullable List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            if (dependencies == null)
                return Stream.empty();

            return dependencies.stream()
                .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            if (repositories == null)
                return Stream.empty();

            return repositories.entrySet().stream()
                .map(e -> new RemoteRepository.Builder(e.getKey(), "default", e.getValue()).build());
        }
    }
}
