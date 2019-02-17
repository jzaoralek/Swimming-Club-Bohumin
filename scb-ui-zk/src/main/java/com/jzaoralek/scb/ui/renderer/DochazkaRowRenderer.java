package com.jzaoralek.scb.ui.renderer;

import java.sql.Time;
import java.util.Date;

import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Vlayout;

import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.LearningLessonParticipateStats;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStats;
import com.jzaoralek.scb.dataservice.domain.LearningLessonStatsWrapper;
import com.jzaoralek.scb.ui.common.converter.Converters;

/**
 * Project: scb-ui-zk
 *
 * Created: 17. 2. 2019
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class DochazkaRowRenderer implements RowRenderer<LearningLessonStats> {

    @Override
    public void render(Row row, LearningLessonStats data, int index) throws Exception {
        renderCellYears(row, data);
        renderCellSeason(row, data);
        renderCellMonth(row, data);
        renderDochazkaCells(row, data);
    }

    /**
     * Vygeneruje všechny hlavičky
     * 
     * @param grid
     * @param model
     */
    public static void createGridHeaders(Grid grid, LearningLessonStatsWrapper model) {
        Columns columns = grid.getColumns();
        if (columns == null) {
            columns = new Columns();
            grid.appendChild(columns);
        }
        columns.getChildren().clear();
        columns.appendChild(new Column(null, null, "100px"));
        columns.appendChild(new Column(null, null, "110px"));
        columns.appendChild(new Column(null, null, "110px"));
        
        
        for (LearningLessonParticipateStats item : model.getParticipantStatsList()) {
            Label label = new Label();
            label.setValue(item.getParticName());
            label.setSclass("header-name");

            Label label2 = new Label();
            label2.setValue("" + item.getParticAttendance() + " %");
            label2.setSclass("header-att");
            label2.setTooltiptext("účast na hodinách");
            
            Vlayout v = new Vlayout();
            v.appendChild(label);
            v.appendChild(label2);

            Column col = new Column();
            col.appendChild(v);
            col.setAlign("center");
            col.setSclass("header last d-users");
            col.setStyle("white-space: normal;");
            col.setWidth("110px");
            
            columns.appendChild(col);
        }
    }

    /**
     * 
     * @param row
     * @param data
     */
    private void renderCellYears(Row row, LearningLessonStats data) {
        Date d = data.getLearningLesson().getLessonDate();
        String s = Converters.getMonthConverter().coerceToUi(d, null, null);
        Label label = new Label(s);
        label.setVisible(data.getLearningLesson().isFirstInMonth());
        Cell cell = new Cell();
        cell.appendChild(label);
        cell.setSclass("years");
        row.appendChild(cell);
    }

    /**
     * 
     * @param row
     * @param data
     */
    private void renderCellSeason(Row row, LearningLessonStats data) {
        Date d = data.getLearningLesson().getLessonDate();
        String s = Converters.getDateconverter().coerceToUi(d, null, null);
        Cell cell = new Cell();
        cell.appendChild(new Label(s));
        cell.setSclass("season");
        row.appendChild(cell);
    }

    /**
     * 
     * @param row
     * @param data
     */
    private void renderCellMonth(Row row, LearningLessonStats data) {
        Date timeFrom = data.getLearningLesson().getTimeFrom();
        Date timeTo = data.getLearningLesson().getTimeTo();

        String s1 = Converters.getTimeconverter().coerceToUi((Time) timeFrom, null, null);
        String s2 = Converters.getTimeconverter().coerceToUi((Time) timeTo, null, null);

        Cell cell = new Cell();
        cell.appendChild(new Label(s1 + " - " + s2));
        cell.setSclass("month last");
        row.appendChild(cell);
    }

    /**
     * 
     * @param row
     * @param data
     */
    private void renderDochazkaCells(Row row, LearningLessonStats data) {
        for (CourseParticipant cp : data.getCourseParticipantList()) {
            Label label = new Label();
            if (cp.isLessonAttendance()) {
                label.setValue("Ano");
                label.setStyle("color:green");
            } else {
                label.setValue("Ne");
                label.setStyle("color:red");
            }
            Cell cell = new Cell();
            cell.appendChild(label);
            row.appendChild(cell);
        }
    }

}
