import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class AddressRecord {
    String userName;
    String phoneNumber;
    String mobileNumber;
    String address;
    String memo;

    public AddressRecord(String userName, String phoneNumber, String mobileNumber, String address, String memo) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Name: " + userName + ", Phone: " + phoneNumber + ", Mobile: " + mobileNumber
                + ", Address: " + address + ", Memo: " + memo;
    }
}

public class AddressBookGUI extends JFrame {
    private static final int HASH_TABLE_SIZE = 100;
    private Map<Integer, List<AddressRecord>> userNameIndex = new HashMap<>();
    private Map<Integer, List<AddressRecord>> phoneNumberIndex = new HashMap<>();

    private JTextArea outputArea;

    public AddressBookGUI() {
        initializeFromFile("C:\\Users\\K M Mozaddedul Islam\\Desktop\\Project Design\\txt.csv");
        createGUI();
    }

    private void initializeFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                AddressRecord record = new AddressRecord(parts[0], parts[1], parts[2], parts[3], parts[4]);
                insertRecord(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertRecord(AddressRecord record) {
        int userNameIndexKey = hashFunction(record.userName);
        userNameIndex.computeIfAbsent(userNameIndexKey, k -> new ArrayList<>()).add(record);

        int phoneNumberIndexKey = hashFunction(record.phoneNumber);
        phoneNumberIndex.computeIfAbsent(phoneNumberIndexKey, k -> new ArrayList<>()).add(record);

        saveToFile("C:\\Users\\K M Mozaddedul Islam\\Desktop\\Project Design\\txt.csv");
    }

    private List<AddressRecord> searchByUserName(String userName) {
        long startTime = System.nanoTime();
        int index = hashFunction(userName);
        List<AddressRecord> result = userNameIndex.getOrDefault(index, new ArrayList<>());
        long endTime = System.nanoTime();
        displayOutput("Search time by User Name: " + (endTime - startTime) + " nanoseconds");
        return result;
    }

    private List<AddressRecord> searchByPhoneNumber(String phoneNumber) {
        long startTime = System.nanoTime();
        int index = hashFunction(phoneNumber);
        List<AddressRecord> result = phoneNumberIndex.getOrDefault(index, new ArrayList<>());
        long endTime = System.nanoTime();
        displayOutput("Search time by Phone Number: " + (endTime - startTime) + " nanoseconds");
        return result;
    }

    private boolean deleteRecord(String userName) {
        int userNameIndexKey = hashFunction(userName);
        List<AddressRecord> records = userNameIndex.get(userNameIndexKey);

        if (records != null) {
            Iterator<AddressRecord> iterator = records.iterator();
            while (iterator.hasNext()) {
                AddressRecord record = iterator.next();
                if (record.userName.equals(userName)) {
                    iterator.remove();
                    saveToFile("C:\\Users\\K M Mozaddedul Islam\\Desktop\\Project Design\\txt.csv");
                    return true;
                }
            }
        }

        return false;
    }

    private int hashFunction(String key) {
        return key.hashCode() % HASH_TABLE_SIZE;
    }

    private void saveToFile(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (List<AddressRecord> records : userNameIndex.values()) {
                for (AddressRecord record : records) {
                    writer.println(
                            record.userName + "," +
                                    record.phoneNumber + "," +
                                    record.mobileNumber + "," +
                                    record.address + "," +
                                    record.memo
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        setTitle("Address Book");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton searchByNameButton = new JButton("Search by User Name");
        JButton searchByPhoneButton = new JButton("Search by Phone Number");
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton displayAllButton = new JButton("Display All");
        JButton displayHashTableButton = new JButton("Display Hash Table");

        buttonPanel.add(searchByNameButton);
        buttonPanel.add(searchByPhoneButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(displayAllButton);
        buttonPanel.add(displayHashTableButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        searchByNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchUserName = JOptionPane.showInputDialog("Enter User Name to search:");
                List<AddressRecord> resultByName = searchByUserName(searchUserName);
                displaySearchResults(resultByName);
            }
        });

        searchByPhoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchPhoneNumber = JOptionPane.showInputDialog("Enter Phone Number to search:");
                List<AddressRecord> resultByPhone = searchByPhoneNumber(searchPhoneNumber);
                displaySearchResults(resultByPhone);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = JOptionPane.showInputDialog("Enter User Name:");
                String phoneNumber = JOptionPane.showInputDialog("Enter Phone Number:");
                String mobileNumber = JOptionPane.showInputDialog("Enter Mobile Number:");
                String address = JOptionPane.showInputDialog("Enter Address:");
                String memo = JOptionPane.showInputDialog("Enter Memo:");

                AddressRecord newRecord = new AddressRecord(userName, phoneNumber, mobileNumber, address, memo);
                insertRecord(newRecord);
                displayOutput("Record added successfully.");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deleteUserName = JOptionPane.showInputDialog("Enter User Name to delete:");
                if (deleteRecord(deleteUserName)) {
                    displayOutput("Record deleted successfully.");
                } else {
                    displayOutput("Record not found.");
                }
            }
        });

        displayAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllRecords();
            }
        });

        displayHashTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printHashTable();
            }
        });

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void displaySearchResults(List<AddressRecord> results) {
        outputArea.setText("");
        if (results.isEmpty()) {
            displayOutput("No matching records found.");
        } else {
            displayOutput("Matching Records:");
            for (AddressRecord record : results) {
                displayOutput(record.toString());
            }
        }
    }

    private void displayAllRecords() {
        outputArea.setText("");
        List<AddressRecord> allRecords = new ArrayList<>();
        for (List<AddressRecord> records : userNameIndex.values()) {
            allRecords.addAll(records);
        }

        if (allRecords.isEmpty()) {
            displayOutput("No records found.");
        } else {
            displayOutput("All Records:");
            for (AddressRecord record : allRecords) {
                displayOutput(record.toString());
            }
        }
    }

    private void printHashTable() {
        outputArea.setText("");
        displayOutput("User Name Hash Table:");
        for (Map.Entry<Integer, List<AddressRecord>> entry : userNameIndex.entrySet()) {
            displayOutput("Hash: " + entry.getKey() + ", Records: " + entry.getValue());
        }

        displayOutput("\nPhone Number Hash Table:");
        for (Map.Entry<Integer, List<AddressRecord>> entry : phoneNumberIndex.entrySet()) {
            displayOutput("Hash: " + entry.getKey() + ", Records: " + entry.getValue());
        }
    }

    private void displayOutput(String message) {
        outputArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AddressBookGUI addressBookGUI = new AddressBookGUI();
                addressBookGUI.setOutputFontSize(16); // Set the desired font size
            }
        });
    }

    protected void setOutputFontSize(int i) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setOutputFontSize'");
    }
}
