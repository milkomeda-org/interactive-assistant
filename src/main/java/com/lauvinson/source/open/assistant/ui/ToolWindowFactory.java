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

package com.lauvinson.source.open.assistant.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.TimeUnit;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory, DumbAware {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ToolWindowPanel toolWindowBuilder = new ToolWindowPanel();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JPanel toolWindowContent = toolWindowBuilder.createToolWindowPanel();
        Content content = contentFactory.createContent(toolWindowContent, null, false);
        toolWindow.getContentManager().addContent(content);
    }


    /**
     * SearchToolWindowPanel
     *
     * @author created by vinson on 2019/7/2
     */
    @SuppressWarnings("unused")
    static
    public class ToolWindowPanel {

        private JPanel panel;
        private JList html;


        ToolWindowPanel() {
        }

        JPanel createToolWindowPanel() {
            JPanel star = new draw_star();
            return star;
        }

        private void resetStats() {

        }

        private void createUIComponents() {

        }
    }

    static class thread_star extends Thread {
        int x0;
        int y0;
        int r0;
        int d0;
        double angle;
        thread_star(int x,int y,int r,double a)
        {
            x0=x;
            y0=y;
            r0=r;
            d0=x0-760;
            angle=a;
        }
        public void run() {
            double an=angle/3;
            while(true)
            {
                x0=(int) (760+d0*Math.cos(angle));
                y0=(int) (440+d0*Math.sin(angle));
                angle=angle+an/10;
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    static class draw_star extends JPanel {

        thread_star[] s = {new thread_star(1200, 440, 30, (Math.PI / 20)), new thread_star(1600, 440, 40, (Math.PI / 40))
                , new thread_star(1300, 440, 25, (Math.PI / 30)), new thread_star(1520, 440, 30, (Math.PI / 36)),
                new thread_star(1080, 440, 18, (Math.PI / 10))
        };

        draw_star() {
            for (ToolWindowFactory.thread_star thread_star : s) thread_star.start();
        }

        public void paint(Graphics g) {
            g.fillRect(0, 0, getWidth(), getHeight());
            Image offScreenImage = this.createImage(this.getWidth(), this.getHeight());
            Graphics gImage = offScreenImage.getGraphics();
            super.paint(gImage);
            g.drawImage(offScreenImage, 0, 0, null);
        }

        public void paintComponent(Graphics g0) {
            Graphics2D g;
            Color[] c = {
                    JBColor.RED,
                    JBColor.BLUE,
                    JBColor.WHITE,
                    JBColor.BLACK,
                    JBColor.GRAY,
                    JBColor.LIGHT_GRAY,
                    JBColor.DARK_GRAY,
                    JBColor.PINK,
                    JBColor.ORANGE,
                    JBColor.YELLOW,
                    JBColor.GREEN,
                    JBColor.MAGENTA,
                    JBColor.CYAN,
            };
            for (int i = 0; i < s.length; i++) {
                g = (Graphics2D) (g0);
                g.setColor(c[i]);
                Ellipse2D ellipse2D = new Ellipse2D.Double(s[i].x0 - s[i].r0, s[i].y0 - s[i].r0, 2 * s[i].r0, 2 * s[i].r0);
                g.fill(ellipse2D);
            }
            repaint();
        }
    }

}