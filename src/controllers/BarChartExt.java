/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import underdevelopment.LineChartOutput;

/**
 * Extended version of original Java-FX bar chart
 * Labeling is different than the original
 * 
 * @author Severin Simko
 */
public class BarChartExt<X,Y> extends BarChart<X,Y> {

    
private static final Logger log = Logger.getLogger(BarChartExt.class.getName());
        /**
         * Registry for text nodes of the bars
         */
        Map<Node, Node> nodeMap = new HashMap<>();

        public BarChartExt(Axis xAxis, Axis yAxis) {
            super(xAxis, yAxis);
        }

        /**
         * Add text for bars
         */
        @Override
        protected void seriesAdded(XYChart.Series<X, Y> series, int seriesIndex) {

            super.seriesAdded(series, seriesIndex);

            for (int j = 0; j < series.getData().size(); j++) {

                XYChart.Data<X, Y> item = series.getData().get(j);

                Node text = new Text(String.valueOf((int) Double.parseDouble(String.valueOf(item.getYValue()))));
                         
                nodeMap.put(item.getNode(), text);
                getPlotChildren().add(text);

            }

        }

        /**
         * Remove text of bars
         */
        @Override
        protected void seriesRemoved(final XYChart.Series<X, Y> series) {

            for (Node bar : nodeMap.keySet()) {

                Node text = nodeMap.get(bar);
                getPlotChildren().remove(text);

            }

            nodeMap.clear();

            super.seriesRemoved(series);
        }

        /**
         * Adjust text of bars, position them on top
         */
        @Override
        protected void layoutPlotChildren() {

            super.layoutPlotChildren();

            for (Node bar : nodeMap.keySet()) {

                Node text = nodeMap.get(bar);

                text.relocate(bar.getBoundsInParent().getMinX(), bar.getBoundsInParent().getMinY() - 25);

            }

        }
    
    }
    
    

