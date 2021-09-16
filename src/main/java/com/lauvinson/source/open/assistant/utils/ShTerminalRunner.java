/*
 * The MIT License (MIT)
 * Copyright © 2019 <copyright holders>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY
 * OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Equivalent description see [http://rem.mit-license.org/]
 */

package com.lauvinson.source.open.assistant.utils;

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

    public ShTerminalRunner(@NotNull Project project) {
        this.project = project;
    }

    public void run(@NotNull String command, @NotNull String workingDirectory, String commandName) {
        if (!isAvailable(project)) {
            return;
        }
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
        return Pair.create(content, shellTerminalWidget);
    }
}
