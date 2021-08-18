package ru.dmitrii.editor;

import ru.dmitrii.editor.listeners.FrameListener;
import ru.dmitrii.editor.listeners.TabbedPaneChangeListener;
import ru.dmitrii.editor.listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane htmlTextPane = new JTextPane();
    private JEditorPane plainTextPane = new JEditorPane();
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            ExceptionHandler.log(e);
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void init() {
        initGui();
        FrameListener frameListener= new FrameListener(this);
        this.addWindowListener(frameListener);
        this.setVisible(true);
    }
    public void initMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, menuBar);
        MenuHelper.initEditMenu (this, menuBar);
        MenuHelper.initStyleMenu (this, menuBar);
        MenuHelper.initAlignMenu (this, menuBar);
        MenuHelper.initColorMenu (this, menuBar);
        MenuHelper.initFontMenu (this, menuBar);
        MenuHelper.initHelpMenu (this, menuBar);
        this.getContentPane().add(menuBar, BorderLayout.NORTH);


    }
    public void initEditor(){
        htmlTextPane.setContentType("text/html");
        JScrollPane scrollPanehtml= new JScrollPane(htmlTextPane);
        tabbedPane.addTab( "HTML", scrollPanehtml);
        JScrollPane scrollPaneplain = new JScrollPane(plainTextPane);
        tabbedPane.addTab("Текст" , scrollPaneplain);
        tabbedPane.setPreferredSize(new Dimension(800, 600));
        tabbedPane.addChangeListener(new TabbedPaneChangeListener(this));
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
    public void initGui(){
        initMenuBar();
        initEditor();
        pack();
    }
    public boolean canUndo() {
        return undoManager.canUndo();
    }
    public boolean canRedo() {
        return undoManager.canRedo();
    }
    public void exit() {
        controller.exit();
    }

    public void selectedTabChanged(){
        if (tabbedPane.getSelectedIndex()==0) controller.setPlainText(plainTextPane.getText());
        else if (tabbedPane.getSelectedIndex()==1)  plainTextPane.setText(controller.getPlainText());
        resetUndo();
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            ExceptionHandler.log(e);
        }
    }
    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            ExceptionHandler.log(e);
        }
    }
    public void resetUndo(){
        undoManager.discardAllEdits();
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    public boolean isHtmlTabSelected() {
        return tabbedPane.getSelectedIndex()==0;
    }
    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);
        resetUndo();
    }
    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }
    public void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Version 1.0", "About", JOptionPane.INFORMATION_MESSAGE);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String comand = e.getActionCommand();
        switch (comand) {
            case "Новый": controller.createNewDocument();
            break;
            case "Открыть": controller.openDocument();
            break;
            case "Сохранить": controller.saveDocument();
            break;
            case "Сохранить как..." : controller.saveDocumentAs();
            break;
            case "Выход" : controller.exit();
            break;
            case  "О программе" : this.showAbout();
        }

    }
}
