package com.jzaoralek.scb.ui.common.template;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.ui.renderer.DochazkaRowRenderer;

/**
 * Project: scb-ui-zk
 *
 * Created: 16. 2. 2019
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class DochazkaGridMacro extends HtmlMacroComponent{

    private static final long serialVersionUID = 456122231888L;

    @Wire
    private Grid grid;

    private LearningLessonStatsWrapper model;

    @Override
    public void afterCompose() {
        super.afterCompose();
        grid.setRowRenderer(new DochazkaRowRenderer());
        render();
    }

    /**
     * 
     */
    private void render() {
        if (model == null) {
            return;
        }
        grid.setModel(new ListModelList<>(model.getLearnLessonsStatsList()));
        DochazkaRowRenderer.createGridHeaders(grid, model);
    }

    public LearningLessonStatsWrapper getModel() {
        return model;
    }

    public void setModel(LearningLessonStatsWrapper model) {
        this.model = model;
        render();
    }
}
