package com.walaa.collector;

import com.walaa.model.WalaaInvoiceHeader;
import com.walaa.model.WalaaInvoicesTableModel;
import com.walaa.model.WalaaInvoiceLine;
import com.walaa.model.WalaaLinesTableModel;
import com.walaa.view.WalaaFrame;
import com.walaa.view.WalaaInvoiceDialog;
import com.walaa.view.WalaaLineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class WalaaController implements ActionListener, ListSelectionListener {
    
    private WalaaFrame frame;
    private WalaaInvoiceDialog invoiceDialog;
    private WalaaLineDialog lineDialog;
    
    public WalaaController(WalaaFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("You Press Button: " + actionCommand);
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create New Item":
                createNewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "createInvoiceCancel":
                createInvoiceCancel();
                break;
            case "createInvoiceOK":
                createInvoiceOK();
                break;
            case "createLineCancel":
                createLineCancel();
                break;
            case "createLineOK":
                createLineOK();
                break;    
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getInvoiceTable().getSelectedRow();
        if (selectedIndex != -1) {
        System.out.println("You Select Row Number: " + selectedIndex);
        WalaaInvoiceHeader currentInvoice = frame.getInvoices().get(selectedIndex);
        frame.getInvoiceNumLabel().setText(""+ currentInvoice.getNum());
        frame.getInvoiceDateLabel().setText(currentInvoice.getDate());
        frame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
        frame.getInvoiceTotalLabel().setText("" + currentInvoice.getInvoiceTotal());
        WalaaLinesTableModel linesTableModel = new WalaaLinesTableModel(currentInvoice.getLines());
        frame.getLineTable().setModel(linesTableModel);
        linesTableModel.fireTableDataChanged();
        }
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        try {
        int result = fc.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File headerFile = fc.getSelectedFile();
            Path headerPath = Paths.get(headerFile.getAbsolutePath());
            List<String> headerLines = Files.readAllLines(headerPath);
            System.out.println("This Is Your Invoices");
            ArrayList<WalaaInvoiceHeader> invoicesArray = new ArrayList <>();
            for (String headerLine : headerLines) {
                try {
                String[] headerParts = headerLine.split(",");
                int invoiceNum = Integer.parseInt(headerParts[0]);
                String invoiceDate = headerParts[1];
                String customerName = headerParts[2];
                
                WalaaInvoiceHeader invoice = new WalaaInvoiceHeader(invoiceNum, invoiceDate, customerName);
                invoicesArray.add(invoice);
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(frame, "Wrong file format", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
            System.out.println("Check this O.O");
            result = fc.showOpenDialog(frame);
            if(result == JFileChooser.APPROVE_OPTION) {
                File lineFile = fc.getSelectedFile();
                Path linePath = Paths.get(lineFile.getAbsolutePath());
                List<String> lineLines = Files.readAllLines(linePath);
                System.out.println("This Is Your Lines");
                for (String lineLine : lineLines) {
                    try {
                    String lineParts[] = lineLine.split(",");
                    int invoiceNum = Integer.parseInt(lineParts[0]);
                    String itemName = lineParts[1];
                    double itemPrice = Double.parseDouble(lineParts[2]);
                    int count = Integer.parseInt(lineParts[3]);
                    WalaaInvoiceHeader inv = null;
                    for (WalaaInvoiceHeader invoice : invoicesArray) {
                        if (invoice.getNum() == invoiceNum) {
                            inv = invoice;
                            break;
                        }
                    }
                    WalaaInvoiceLine line = new WalaaInvoiceLine(itemName, itemPrice, count, inv);
                    inv.getLines().add(line);
                    } catch (Exception ex){
                        JOptionPane.showMessageDialog(frame, "Wrong file format", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
                System.out.println("Welcome To My World :D");
            }
            
            frame.setInvoices(invoicesArray);
            WalaaInvoicesTableModel invoicesTableModel = new WalaaInvoicesTableModel(invoicesArray);
            frame.setInvoicesTableModel(invoicesTableModel);
            frame.getInvoiceTable().setModel(invoicesTableModel);
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveFile() {
        ArrayList<WalaaInvoiceHeader> invoices = frame.getInvoices();
        String headers = "";
        String lines = "";
        for (WalaaInvoiceHeader invoice : invoices) {
            String invCSV = invoice.getAsCSV();
            headers += invCSV;
            headers += "\n";

            for (WalaaInvoiceLine line : invoice.getLines()) {
                String lineCSV = line.getAsCSV();
                lines += lineCSV;
                lines += "\n";
            }
        }
        System.out.println("Hello World :D");
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter lola = new FileWriter(headerFile);
                lola.write(headers);
                lola.flush();
                lola.close();
                result = fc.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter lolo = new FileWriter(lineFile);
                    lolo.write(lines);
                    lolo.flush();
                    lolo.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private void createNewInvoice() {
        invoiceDialog = new WalaaInvoiceDialog(frame);
        invoiceDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selectedRow = frame.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1) {
            frame.getInvoices().remove(selectedRow);
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createNewItem() {
        lineDialog = new WalaaLineDialog(frame);
        lineDialog.setVisible(true);
    }
    private void deleteItem() {
        int selectedInv = frame.getInvoiceTable().getSelectedRow();
        int selectedRow = frame.getLineTable().getSelectedRow();

        if (selectedInv != -1 && selectedRow != -1) {
            WalaaInvoiceHeader invoice = frame.getInvoices().get(selectedInv);
            invoice.getLines().remove(selectedRow);
            WalaaLinesTableModel linesTableModel = new WalaaLinesTableModel(invoice.getLines());
            frame.getLineTable().setModel(linesTableModel);
            linesTableModel.fireTableDataChanged();
        }
    }

    private void createInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void createInvoiceOK() {
        String date = invoiceDialog.getInvDateField().getText();
        String customer = invoiceDialog.getCustNameField().getText();
        int num = frame.getNextInvoiceNum();
        try {
            String[] dateParts = date.split("-");
            if (dateParts.length < 3) {
                JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                if (day > 31 || month > 12) {
                    JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    WalaaInvoiceHeader invoice = new WalaaInvoiceHeader(num, date, customer);
                    frame.getInvoices().add(invoice);
                    frame.getInvoicesTableModel().fireTableDataChanged();
                    invoiceDialog.setVisible(false);
                    invoiceDialog.dispose();
                    invoiceDialog = null;
                }
            }
        }catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

    private void createLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        String priceStr = lineDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = frame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            WalaaInvoiceHeader invoice = frame.getInvoices().get(selectedInvoice);
            WalaaInvoiceLine line = new WalaaInvoiceLine(item, price, count, invoice);
            invoice.getLines().add(line);
            WalaaLinesTableModel linesTableModel = (WalaaLinesTableModel) frame.getLineTable().getModel();
            linesTableModel.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

}
