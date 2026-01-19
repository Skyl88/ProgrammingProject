package com.mycompany.customersystem;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// ==================== CLASS 1: Customer ====================
class Customer implements Comparable<Customer> {
    private int id;
    private String name;
    private String email;
    private String phone;

    public Customer(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public int compareTo(Customer other) {
        return Integer.compare(this.id, other.id);
    }
}

// ==================== CLASS 2: CustomerDatabase ====================
class CustomerDatabase {
    private List<Customer> customers = new ArrayList<>();
    private int nextId = 1;

    // Create and add new customer
    public Customer createCustomer(String name, String email, String phone) {
        Customer c = new Customer(nextId++, name, email, phone);
        int insertIndex = findInsertionPoint(c.getId());
        customers.add(insertIndex, c);
        return c;
    }

    // Binary Search by ID
    public int binarySearchById(int id) {
        int left = 0;
        int right = customers.size() - 1;

        System.out.println("\n=== BINARY SEARCH START ===");
        System.out.println("Searching for ID: " + id);
        System.out.println("Total customers: " + customers.size());

        int iterations = 0;

        while (left <= right) {
            iterations++;
            int mid = left + (right - left) / 2;
            int midId = customers.get(mid).getId();

            System.out.println("Iteration " + iterations + ": left=" + left +
                    ", right=" + right + ", mid=" + mid + ", midId=" + midId);

            if (midId == id) {
                System.out.println("FOUND at index " + mid + " in " + iterations + " iterations!");
                System.out.println("=== BINARY SEARCH END ===\n");
                return mid;
            } else if (midId < id) {
                left = mid + 1;
                System.out.println("  → ID " + id + " > " + midId + ", searching RIGHT half");
            } else {
                right = mid - 1;
                System.out.println("  → ID " + id + " < " + midId + ", searching LEFT half");
            }
        }

        System.out.println("NOT FOUND after " + iterations + " iterations");
        System.out.println("=== BINARY SEARCH END ===\n");
        return -1;
    }

    // Find insertion point for sorted order
    private int findInsertionPoint(int id) {
        int left = 0;
        int right = customers.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midId = customers.get(mid).getId();

            if (midId < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    // Get customer by ID
    public Customer getById(int id) {
        int index = binarySearchById(id);
        if (index != -1) {
            return customers.get(index);
        }
        return null;
    }

    // Get customer by index
    public Customer getByIndex(int index) {
        if (index >= 0 && index < customers.size()) {
            return customers.get(index);
        }
        return null;
    }

    // Update customer
    public boolean update(int id, String name, String email, String phone) {
        int index = binarySearchById(id);
        if (index != -1) {
            Customer c = customers.get(index);
            c.setName(name);
            c.setEmail(email);
            c.setPhone(phone);
            return true;
        }
        return false;
    }

    // Delete customer
    public boolean delete(int id) {
        int index = binarySearchById(id);
        if (index != -1) {
            customers.remove(index);
            return true;
        }
        return false;
    }

    // Get all customers
    public List<Customer> getAll() {
        return customers;
    }

    // Get size
    public int size() {
        return customers.size();
    }
}

// ==================== CLASS 3: CustomerSystem ====================
public class CustomerSystem extends JFrame {
    private CustomerDatabase database = new CustomerDatabase();

    private JTextField txtId, txtName, txtEmail, txtPhone, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnResetSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public CustomerSystem() {
        setTitle("Customer Registration & Profile Management (Binary Search)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(formPanel, gbc, "ID:", txtId = new JTextField(10), 0);
        txtId.setEditable(false);

        addField(formPanel, gbc, "Name:", txtName = new JTextField(20), 1);
        addField(formPanel, gbc, "Email:", txtEmail = new JTextField(20), 2);
        addField(formPanel, gbc, "Phone:", txtPhone = new JTextField(20), 3);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAdd = new JButton("Register");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search by ID (Binary Search) "));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnResetSearch = new JButton("Show All");

        searchPanel.add(new JLabel("Find:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnResetSearch);

        topPanel.add(formPanel);
        topPanel.add(buttonPanel);
        topPanel.add(searchPanel);

        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelectedCustomer();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customer List (Sorted by ID for Binary Search)"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAlgorithm = new JLabel("Algorithm: Binary Search O(log n) | Data Structure: Sorted ArrayList");
        lblAlgorithm.setForeground(Color.BLUE);
        statusPanel.add(lblAlgorithm);
        add(statusPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        btnSearch.addActionListener(e -> searchCustomer());
        btnResetSearch.addActionListener(e -> {
            txtSearch.setText("");
            sorter.setRowFilter(null);
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchCustomer();
                }
            }
        });
    }

    private void addField(JPanel p, GridBagConstraints gbc, String label, JTextField field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(field, gbc);
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer c = database.createCustomer(name, email, phone);

        refreshTable();

        clearForm();
        JOptionPane.showMessageDialog(this,
                "Customer Registered Successfully!\n" +
                        "ID: " + c.getId() + "\n" +
                        "Total customers: " + database.size());
    }

    private void updateCustomer() {
        String idText = txtId.getText();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer from the table to update.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (database.update(id, name, email, phone)) {
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this,
                    "Customer Updated Successfully!\n" +
                            "Found using Binary Search");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String idText = txtId.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idText);

            if (database.delete(id)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this,
                        "Customer Deleted!\n" +
                                "Found using Binary Search");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCustomer() {
        String searchText = txtSearch.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        try {
            int id = Integer.parseInt(searchText);

            Customer c = database.getById(id);

            if (c != null) {
                txtId.setText(String.valueOf(c.getId()));
                txtName.setText(c.getName());
                txtEmail.setText(c.getEmail());
                txtPhone.setText(c.getPhone());

                // Find and select in table
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if ((int) tableModel.getValueAt(i, 0) == id) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        break;
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Customer found using BINARY SEARCH!\n" +
                                "ID: " + c.getId() + "\n" +
                                "Name: " + c.getName() + "\n" +
                                "Email: " + c.getEmail() + "\n" +
                                "Phone: " + c.getPhone(),
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Customer with ID " + id + " not found.\n" +
                                "Binary Search completed.",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException e) {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            JOptionPane.showMessageDialog(this,
                    "Using REGEX FILTER for text search.\n" +
                            "Enter a numeric ID for BINARY SEARCH.",
                    "Text Search", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Customer c : database.getAll()) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getEmail(), c.getPhone()
            });
        }
    }

    private void loadSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int modelRow = table.convertRowIndexToModel(row);

            txtId.setText(tableModel.getValueAt(modelRow, 0).toString());
            txtName.setText(tableModel.getValueAt(modelRow, 1).toString());
            txtEmail.setText(tableModel.getValueAt(modelRow, 2).toString());
            txtPhone.setText(tableModel.getValueAt(modelRow, 3).toString());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CustomerSystem().setVisible(true);
        });
    }
}