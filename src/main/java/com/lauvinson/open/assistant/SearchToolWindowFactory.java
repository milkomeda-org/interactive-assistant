package com.lauvinson.open.assistant;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SearchToolWindowFactory implements ToolWindowFactory, DumbAware {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SearchToolWindowPanel toolWindowBuilder = new SearchToolWindowPanel();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JPanel toolWindowContent = toolWindowBuilder.createToolWindowPanel();
        Content content = contentFactory.createContent(toolWindowContent, null, false);
        toolWindow.getContentManager().addContent(content);
    }


}