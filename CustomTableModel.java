/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mytunes;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Robinhood
 */
public class CustomTableModel extends DefaultTableModel{
    private int[] hiddenColumns;

    public CustomTableModel(Object[][] data, Object[] columnNames, int[] hiddenColumns) {
        super(data, columnNames);
        this.hiddenColumns = hiddenColumns;
    }

    @Override
    public String getColumnName(int column) {
        for (int hiddenColumn : hiddenColumns) {
            if (column >= hiddenColumn) {
                return super.getColumnName(column + 1);
            }
        }
        return super.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        for (int hiddenColumn : hiddenColumns) {
            if (columnIndex >= hiddenColumn) {
                return super.getColumnClass(columnIndex + 1);
            }
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int row, int column) {
        for (int hiddenColumn : hiddenColumns) {
            if (column >= hiddenColumn) {
                return super.getValueAt(row, column + 1);
            }
        }
        return super.getValueAt(row, column);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        for (int hiddenColumn : hiddenColumns) {
            if (column >= hiddenColumn) {
                super.setValueAt(aValue, row, column + 1);
                return;
            }
        }
        super.setValueAt(aValue, row, column);
    }

    @Override
    public int getColumnCount() {
        return super.getColumnCount() - hiddenColumns.length;
    }
    
}
