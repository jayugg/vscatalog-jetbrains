package com.github.sirnoname2705.vscatalog;

import com.intellij.openapi.project.Project;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.ide.JsonSchemaService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*
 * Komplett neuer approach. Wir zerteilen die funktion in mehrere Fälle.
 * 1. ist alles ist schon gedownloaded und im local vfs. Es gibt keine updates bzw. internetverbidung.
 * 2. Es gibt keine lokalen files da es das erstemal startet. Also alles neu.
 * 3. Es gibt lokale files und internet verbidung, dann muss geprüft werden ob es ein update gab und geupdatet werden soll.
 * Falls nicht ist es quasi Fall 1 und falls doch wird es quasi fall 2.
 * Wenn alles fertig ist wird immer resetSchemaService() aufgerufen.
 * Ob eine neue version vorhanden ist, wird anhand der version attributes im katalog geprüft.
 * Wichtig wäre es das wir nur schemas providen sobald wir alles auch wirklich gedownloaded haben, also libs usw.
 * Sodass auch die schema version mit richtig aus der methode gebracht wird.
 * Der Katalog sollte nach möglichkeit auch wie ein schema behandelt werden, wenn möglich.
 * Um das ganze auch erweiterbar zu halten sollte ich eine liste an base katalog urls übergeben und dies soll dann immer für alle kataloge passieren.
 * Nur muss man gucken ob man die updates iwie nur pro katalog machen kann.
 * Zudem sollte ich in den settings iwie eine option haben versionen auszuwählen bzw. updates auschlaten zu können.
 *
 */

public class SchemaProviderFactory implements JsonSchemaProviderFactory {
    public static Project project;
    public static boolean IS_READY;
    public static boolean SHOULD_INIT = true;
    public static List<JsonSchemaFileProvider> providers = new ArrayList<>();
    public static PluginInitializer pluginInitializer;


    private void init(Project project) {
        if (pluginInitializer == null) {
            SchemaProviderFactory.project = project;
            pluginInitializer = PluginInitializer.getInstance();
        }
        if (SHOULD_INIT) {
            com.intellij.openapi.application.ApplicationManager.getApplication()
                    .executeOnPooledThread(pluginInitializer::initializePlugin);
//            ProcessIOExecutorService.INSTANCE.execute(pluginInitializer::initializedPlugin);
            SHOULD_INIT = false;
        }

    }

    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        init(project);
        if (!IS_READY) {
            return new ArrayList<>();
        }
        return providers;
    }

    public static void setJsonSchemasReady() {
        IS_READY = true;
        resetSchemaService();
    }

    public static void resetSchemaService() {
        project.getService(JsonSchemaService.class).reset();
    }

    public static void restartPlugin() {
        IS_READY = false;
        SHOULD_INIT = true;
        DependencyResolver.clearDependencyCache();
        resetSchemaService();
    }


}
