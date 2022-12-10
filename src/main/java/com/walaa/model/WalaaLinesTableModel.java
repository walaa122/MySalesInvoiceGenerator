package com.walaa.model;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class WalaaLinesTableModel extends AbstractTableModel {
    
    private ArrayList<WalaaInvoiceLine> lines;
    private String[] columns = {"No.", "Item Name", "Item Price", "Count", "Item Total"};

    public WalaaLinesTableModel(ArrayList<WalaaInvoiceLine> lines) {
        this.lines = lines;
    }
    

    @Override
    public int getRowCount() {
        return lines.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
    
    @Override
    public String getColumnName(int y){
        return columns[y];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WalaaInvoiceLine line = lines.get(rowIndex);
         switch (columnIndex) {
            case 0: return line.getInvoice().getNum();
            case 1: return line.getItem();
            case 2: return line.getPrice();
            case 3: return line.getCount();
            case 4: return line.getLineTotal();
            default: return "";    
        }
    }
    
}
