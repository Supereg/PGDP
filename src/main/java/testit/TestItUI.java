package testit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestItUI extends Frame implements ActionListener {

    public static void main(String[] args) {
        TestItUI client = new TestItUI();
    }

    private final TestItClientConnection connection;
    private static List<Frame> frameList = new ArrayList<>();

    private Label infoLabel = new Label("Connecting...", Label.CENTER);

    private Button exit = new Button("exit");
    private Button add = new Button("add");
    private Button list = new Button("list");
    private Button query = new Button("query");
    private Button count = new Button("count");
    private Button pageRank = new Button("pageRank");

    private TextField queryField = new TextField();
    private TextField countField = new TextField();

    public TestItUI() throws HeadlessException {
        super("TestIt UI - Hub");

        Panel queryPanel = new Panel(new GridLayout(1, 2, 0, 20));
        queryPanel.add(queryField);
        queryPanel.add(query);

        Panel countPanel = new Panel(new GridLayout(1, 2, 0, 20));
        countPanel.add(countField);
        countPanel.add(count);

        Panel buttons = new Panel(new GridLayout(6, 1, 10, 10));
        buttons.add(exit);
        buttons.add(add);
        buttons.add(list);
        buttons.add(queryPanel);
        buttons.add(countPanel);
        buttons.add(pageRank);

        setUIEnabled(false);

        Panel labelPanel = new Panel(new GridLayout(2, 1, 5, 5));

        Label title = new Label("TestIt UI Client", Label.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 20));
        infoLabel.setFont(new Font("Helvetica", Font.PLAIN, 15));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setBackground(Color.LIGHT_GRAY);
        labelPanel.add(title);
        labelPanel.add(infoLabel);

        setLayout(new BorderLayout(10, 10));
        add("Center", buttons);
        add("North", labelPanel);
        add("West", new Panel());
        add("East", new Panel());
        add("South", new Panel());

        pack();
        setResizable(false);
        setVisible(true);

        exit.addActionListener(this);
        add.addActionListener(this);
        list.addActionListener(this);
        query.addActionListener(this);
        count.addActionListener(this);
        pageRank.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        connection = new TestItClientConnection("localhost", 8000, this::connectionErrorOccurred);
        try {
            connection.connect();

            infoLabel.setText("Connected!");
            infoLabel.setForeground(Color.GREEN);
            setUIEnabled(true);
        } catch (IOException e) {
            infoLabel.setText("Could not connect: " + e.getMessage());
            infoLabel.setForeground(Color.RED);
            pack();

            setUIEnabled(false);
            exit.setEnabled(true);
        }
    }

    private void connectionErrorOccurred(ErrorType type, String message) {
        System.err.println("Connection error " +type + ": " + message);
        if (type.equals(ErrorType.MESSAGE))
            JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
        else if (type.equals(ErrorType.CONNECTION_ENDED)) {
            infoLabel.setText(message != null? "Disconnected: " + message: "Disconnected!");
            infoLabel.setForeground(Color.RED);
            pack();

            setUIEnabled(false);
            exit.setEnabled(true);

            for (Frame frame: frameList)
                frame.dispose();
        }
    }

    private void exitApplication() {
        if (connection.isActive())
            connection.enqueueCommand("exit");

        dispose();
        for (Frame frame: frameList)
            frame.dispose();

        System.exit(0);
    }

    public void setUIEnabled(boolean enabled) {
        exit.setEnabled(enabled);
        add.setEnabled(enabled);
        list.setEnabled(enabled);
        query.setEnabled(enabled);
        queryField.setEnabled(enabled);
        count.setEnabled(enabled);
        countField.setEnabled(enabled);
        pageRank.setEnabled(enabled);
    }

    public void openAddWindow() {
        Frame addFrame = new Frame("TestIt UI: Add Document");

        Label titleLabel = new Label("Title:", Label.RIGHT);
        TextField titelField = new TextField();
        Label contentLabel = new Label("Content:", Label.RIGHT);
        TextField contentField = new TextField();
        Label infoLabel = new Label("", Label.CENTER);
        Button addButton = new Button("Add");

        Panel titlePanel = new Panel(new GridLayout(1, 2, 10, 10));
        Panel contentPanel = new Panel(new GridLayout(1, 2, 10, 10));
        titlePanel.add(titleLabel);
        titlePanel.add(titelField);
        contentPanel.add(contentLabel);
        contentPanel.add(contentField);

        Panel inputPanel = new Panel(new GridLayout(4, 1, 10, 10));

        inputPanel.add(titlePanel);
        inputPanel.add(contentPanel);
        inputPanel.add(infoLabel);
        inputPanel.add(addButton);

        addFrame.add("Center", inputPanel);
        addFrame.add("North", new Panel());
        addFrame.add("South", new Panel());
        addFrame.add("West", new Panel());
        addFrame.add("East", new Panel());

        addFrame.pack();
        addFrame.setSize(300, addFrame.getHeight());
        addFrame.setVisible(true);

        addButton.addActionListener(event -> {
            String title = titelField.getText();
            String content = contentField.getText();

            infoLabel.setText("Adding...");
            connection.enqueueCommand("add " + title + ":" + content, message -> {
                infoLabel.setText("Successfully added '" + title + "'!");
                infoLabel.setBackground(Color.GREEN);
            });
        });

        addFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                addFrame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                frameList.remove(addFrame);
            }
        });

        frameList.add(addFrame);
    }

    public void openListWindow() {
        createSimpleContentWindow("TestIt UI: List results", "list");
    }

    public void openQueryWindow() {
        String query = queryField.getText();
        createSimpleContentWindow("TestIt UI - Query '" + query + "'", "query " + query);
    }

    public void openCountWindow() {
        String word = countField.getText();
        createSimpleContentWindow("TestIt UI - Count '" + word + "'", "count " + word);
    }

    public void openPageRankWindow() {
        createSimpleContentWindow("TestIt UI - PageRank", "pagerank");
    }

    private void createSimpleContentWindow(String title, String command) {
        Frame frame = new Frame(title);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        frame.add("Center", textArea);
        frame.add("North", new Panel());
        frame.add("South", new Panel());
        frame.add("West", new Panel());
        frame.add("East", new Panel());

        frame.setSize(300, 200);
        connection.enqueueCommand(command, message -> {
            if (message != null)
                textArea.append(message + "\n");
        });

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                frameList.remove(frame);
            }
        });

        frameList.add(frame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(exit))
            exitApplication();
        else if (source.equals(add))
            EventQueue.invokeLater(this::openAddWindow);
        else if (source.equals(list))
            EventQueue.invokeLater(this::openListWindow);
        else if (source.equals(query))
            EventQueue.invokeLater(this::openQueryWindow);
        else if (source.equals(count))
            EventQueue.invokeLater(this::openCountWindow);
        else if (source.equals(pageRank))
            EventQueue.invokeLater(this::openPageRankWindow);
    }

}