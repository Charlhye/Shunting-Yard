import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GUI {
    private JPanel panel1;
    private JTextField outputTF;
    private JTextField inputTF;
    private JButton evaluateButton;
    private JButton stepButton;
    private JButton completeButton;
    private JTextField evalTF;
    private JPanel buttonsPanel;
    private JTable operatorStackList;
    private JButton restartB;

    public GUI(){
        JFrame frame=new JFrame();
        frame.setContentPane(panel1);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(650,600);

        inputTF.setBorder(BorderFactory.createTitledBorder("Input"));
        outputTF.setBorder(BorderFactory.createTitledBorder("Output"));
        operatorStackList.setBorder(BorderFactory.createTitledBorder("Stack"));
        evalTF.setBorder(BorderFactory.createTitledBorder("Evaluation"));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        DefaultTableModel model=new DefaultTableModel();
        model.addColumn("Precedence");
        model.addColumn("Stack");

        operatorStackList.setModel(model);



        inputTF.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {

            }
            public void removeUpdate(DocumentEvent e) {

            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {

            }
        });

        Queue<Character> input = new LinkedList<>();
        Queue<Character> output= new LinkedList<>();
        Stack<Character> operatorStack = new Stack<>();


        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputTF.getText().equals("")){
                    try {
                        while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                            char runTokenStack = operatorStack.pop();
                            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                            if (runTokenStack != '(' | runTokenStack != ')')//if they are different than parenthesis
                            {
                                output.add(runTokenStack);//pop the operator and enqueue it
                                outputTF.setText("");
                                for (char car : output) {
                                    outputTF.setText(outputTF.getText() + car);
                                }
                            } else {
                                throw new ParenthesisNotPairedException("Missing parenthesis");
                            }
                        }
                    }catch (ParenthesisNotPairedException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else {
                    inputTF.setText(inputTF.getText().substring(1));
                    try {
                        Algorithm(input.remove(), output, operatorStack);
                        System.out.print("");
                    }catch (ParenthesisNotPairedException ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        completeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (char it : input) {
                        Algorithm(it, output, operatorStack);
                    }
                    while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                        char runTokenStack = operatorStack.pop();
                        ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                        if (runTokenStack != '(' | runTokenStack != ')')//if they are different than parenthesis
                        {
                            output.add(runTokenStack);//pop the operator and enqueue it
                            outputTF.setText("");
                            for (char car : output) {
                                outputTF.setText(outputTF.getText() + car);
                            }
                        } else {
                            throw new ParenthesisNotPairedException("Missing parenthesis");

                        }
                        //Holiwis
                    }
                }catch (ParenthesisNotPairedException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    clear(input, output,operatorStack);
                }
            }
        });
        restartB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //clear(input,output,operatorStack);
                for (char it:inputTF.getText().toCharArray()) {
                    input.add(it);
                }
                completeButton.setEnabled(true);
                stepButton.setEnabled(true);
            }
        });
    }

    private void clear(Queue<Character> input, Queue<Character> output, Stack<Character> operatorStack ){
        while(!input.isEmpty()){
            input.remove();
        }
        while(!output.isEmpty()){
            output.remove();
        }
        outputTF.setText("");
        while(!operatorStack.isEmpty()){
            operatorStack.pop();
        }
        int rows=((DefaultTableModel) operatorStackList.getModel()).getRowCount();
        for (int i = 0; i < rows; i++) {
            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
        }
    }

    public void Algorithm(char token, Queue<Character> output, Stack<Character> operatorStack) throws ParenthesisNotPairedException {
        if(Character.isDigit(token)){//If token is a number
            output.add(token);//enqueue it
            outputTF.setText("");
            for (char car:output) {
                outputTF.setText(outputTF.getText()+car);
            }
        }
        else if(token == '^' || token == '*' || token == '/' || token == '+' || token == '-') {//If token is an operator o1
            boolean flag = false;
            if (!operatorStack.isEmpty() && operatorStack.peek() != '(') {//This excludes the open parenthesis
                while (!operatorStack.isEmpty() && (token != '^' && Operator.precedence(token, operatorStack.peek()) <= 0) || (token == '^' && Operator.precedence(token, operatorStack.peek()) < 0)) {//while there is a operator in the top of the stack and o1 is left associative and its precedence is less o equal than o2 or o1 is right associative and its precedence is less than o2
                    output.add(operatorStack.pop());//pop o2 from the stack and enqueue it
                    ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                    outputTF.setText("");
                    for (char car:output) {
                        outputTF.setText(outputTF.getText()+car);
                    }
                }
            }
            operatorStack.push(token);
            ((DefaultTableModel) operatorStackList.getModel()).insertRow(0,new String[]{"1",String.valueOf(token)});
        }else if (token == '('){//if token is an open parenthesis
            operatorStack.push(token);// push it in the top of the stack
            ((DefaultTableModel) operatorStackList.getModel()).insertRow(0,new String[]{"1",String.valueOf(token)});
        }else if (token == ')'){//if token is an close parenthesis
            char runTokenStack;
            //try catch if there is a parenthesis without its partner
            try {
                while ((runTokenStack = operatorStack.pop()) != '(') {//Until the top of the stack is an open parenthesis
                    ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
                    output.add(runTokenStack);//pop operands from the stack and enqueue them
                    outputTF.setText("");
                    for (char car : output) {
                        outputTF.setText(outputTF.getText() + car);
                    }
                }
            }catch (EmptyStackException ex){
                throw new ParenthesisNotPairedException("Missing parenthesis");
            }
            ((DefaultTableModel) operatorStackList.getModel()).removeRow(0);
        }

        //If there are no more tokens to read
    }



    public class Complete implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Queue<Character> input = new LinkedList<>();
            Queue<Character> output= new LinkedList<>();
            for (char charat:inputTF.getText().toCharArray()) {
                input.add(charat);
            }

            Stack<Character> operatorStack = new Stack<>();

            String res = "";
            for(char chare : output)
            {
                res += chare;
            }
            outputTF.setText(res);

            inputTF.setText("");

        }
    }
}
