package com.lauvinson.source.open.assistant;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.LocalTerminalDirectRunner;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ShTerminalRunner {
    private static final Logger LOG = Logger.getInstance(LocalTerminalDirectRunner.class);

    private final Project project;

    protected ShTerminalRunner(@NotNull Project project) {
        this.project = project;
    }

    public void run(@NotNull String command, @NotNull String workingDirectory, String commandName) {
        TerminalView terminalView = TerminalView.getInstance(project);
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
        if (window == null) return;

        ContentManager contentManager = window.getContentManager();
        Pair<Content, ShellTerminalWidget> pair = getSuitableProcess(contentManager, commandName);
        try {
            if (pair == null) {
                ShellTerminalWidget shell = terminalView.createLocalShellWidget(workingDirectory, commandName);
                shell.executeCommand(command);
                return;
            }
            window.activate(null);
            contentManager.setSelectedContent(pair.first);
            pair.second.executeCommand(command);
        } catch (IOException e) {
            LOG.warn("Cannot run command:" + command, e);
        }
    }

    public boolean isAvailable(@NotNull Project project) {
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
        return window != null && window.isAvailable();
    }

    private static Pair<Content, ShellTerminalWidget> getSuitableProcess(@NotNull ContentManager contentManager, @NotNull String commandName) {
        return Arrays.stream(contentManager.getContents())
                .map(content -> getSuitableProcess(content, commandName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static Pair<Content, ShellTerminalWidget> getSuitableProcess(@NotNull Content content, @NotNull String commandName) {
        JBTerminalWidget widget = TerminalView.getWidgetByContent(content);
        if (!(widget instanceof ShellTerminalWidget)) return null;
        if (!content.getDisplayName().equals(commandName)) return null;
        ShellTerminalWidget shellTerminalWidget = (ShellTerminalWidget)widget;
        if (!shellTerminalWidget.getTypedShellCommand().isEmpty() || shellTerminalWidget.hasRunningCommands()) return null;
        return Pair.create(content, shellTerminalWidget);
    }
}
